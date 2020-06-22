package com.lobstr.stellar.vault.presentation.pin.main

import android.app.Activity
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.pin.PinInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.pin.PinModule
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.pin.main.PinFragment.Companion.STYLE_CREATE_PIN
import com.lobstr.stellar.vault.presentation.pin.main.PinFragment.Companion.STYLE_ENTER_PIN
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.PinMode.CHANGE
import com.lobstr.stellar.vault.presentation.util.Constant.PinMode.CONFIRM
import com.lobstr.stellar.vault.presentation.util.Constant.PinMode.CREATE
import com.lobstr.stellar.vault.presentation.util.Constant.PinMode.ENTER
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricUtils
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class PinFrPresenter(private var pinMode: Byte) : BasePresenter<PinFrView>() {
    @Inject
    lateinit var interactor: PinInteractor

    // Temp value for handle pin creation after change.
    private var isCreationAfterChangePinMode: Boolean = false

    private var newPin: String? = null
    private var tempCommonPin: String? = null
    private var needBlockPinView = false

    private val commonPinArray: ArrayList<String> =
        arrayListOf(
            "123456", "121212", "101010", "112233", "200000", "696969", "131313",
            "654321", "222111", "111222", "121111", "111112", "011111", "111110",
            "010000", "000111", "111000", "000001", "100000"
        )

    init {
        LVApplication.appComponent.plusPinComponent(PinModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.windowLightNavigationBar(pinMode != ENTER)
        viewState.setupToolbar(
            pinMode == CONFIRM || pinMode == CHANGE,
            pinMode == ENTER,
            if (pinMode == ENTER) R.color.color_primary else android.R.color.white,
            R.drawable.ic_arrow_back,
            if (pinMode == ENTER) android.R.color.white else R.color.color_primary
        )

        when (pinMode) {
            CREATE -> {
                viewState.showTitle(R.string.text_title_create_pin)
                viewState.setScreenStyle(STYLE_CREATE_PIN)
            }
            CHANGE -> {
                viewState.showTitle(R.string.text_title_enter_old_pin)
                viewState.setScreenStyle(STYLE_CREATE_PIN)
            }
            CONFIRM -> {
                viewState.showTitle(R.string.text_title_confirm_pin)
                viewState.setScreenStyle(STYLE_CREATE_PIN)
            }
            ENTER -> {
                viewState.setScreenStyle(STYLE_ENTER_PIN)
            }
        }
    }

    override fun attachView(view: PinFrView?) {
        super.attachView(view)

        // Logic for show biometric.
        if (pinMode == ENTER && interactor.isTouchIdEnabled() &&
            BiometricUtils.isBiometricAvailable(AppUtil.getAppContext())
        ) {
            viewState.showBiometricDialog(true)
        }
    }

    override fun detachView(view: PinFrView) {
        viewState.showBiometricDialog(false)
        super.detachView(view)
    }

    /**
     * @param pin Your completely entered pin
     */
    fun onPinComplete(pin: String?) {
        if (pin != null) {
            when (pinMode) {
                CREATE -> createPin(pin)
                CHANGE -> confirmPin(pin, false)
                CONFIRM -> confirmPin(pin, false)
                ENTER -> confirmPin(pin, false)
            }
        } else {
            viewState.showErrorMessage(R.string.text_error_incorrect_pin)
            viewState.resetPin()
        }
    }

    private fun createPin(pin: String) {
        when {
            newPin.isNullOrEmpty() -> {
                confirmPin(pin, true)
            }
            newPin.equals(pin) -> {
                if (needBlockPinView) {
                    return
                }

                unsubscribeOnDestroy(
                    interactor.savePin(pin)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            needBlockPinView = true
                            viewState.showProgressDialog(true)
                        }
                        .doOnEvent {
                            needBlockPinView = false
                            viewState.showProgressDialog(false)
                        }
                        .doOnComplete {
                            if (isCreationAfterChangePinMode) {
                                viewState.finishScreenWithResult(Activity.RESULT_OK)
                            } else {
                                checkAuthState()
                            }
                        }
                        .subscribe()
                )
            }
            else -> {
                when (pinMode) {
                    CHANGE -> viewState.showErrorMessage(R.string.text_error_pin_do_not_match)
                    else -> viewState.showErrorMessage(R.string.text_error_incorrect_pin)
                }
                viewState.resetPin()
            }
        }
    }

    private fun confirmPin(pin: String, needCheckOldPint: Boolean) {
        if (needBlockPinView) {
            return
        }

        unsubscribeOnDestroy(
            interactor.checkPinValidation(pin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    needBlockPinView = true
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _: Boolean, _: Throwable? ->
                    needBlockPinView = false
                    viewState.showProgressDialog(false)
                }
                .doOnSuccess { success ->
                    if (needCheckOldPint) {
                        if (success) {
                            viewState.showErrorMessage(R.string.text_error_use_old_pin)
                            viewState.resetPin()
                        } else {
                            if (isCommonPin(pin)) {
                                tempCommonPin = pin
                                viewState.showCommonPinPatternDialog()
                            } else {
                                newPin = pin
                                if (pinMode == CHANGE) {
                                    viewState.showTitle(R.string.text_title_confirm_new_pin)
                                } else {
                                    viewState.showTitle(R.string.text_title_confirm_pin)
                                }
                                viewState.resetPin()
                            }
                        }
                    } else {
                        if (success) {
                            when (pinMode) {
                                CONFIRM -> viewState.finishScreenWithResult(
                                    Activity.RESULT_OK
                                )
                                CREATE -> viewState.finishScreenWithResult(Activity.RESULT_OK)
                                CHANGE -> showCreateAfterChangePinState()
                                ENTER -> checkAuthState()
                            }
                        } else {
                            viewState.showErrorMessage(R.string.text_error_incorrect_pin)
                            viewState.resetPin()
                        }
                    }
                }
                .subscribe()
        )
    }

    private fun showCreateAfterChangePinState() {
        pinMode = CREATE
        isCreationAfterChangePinMode = true
        viewState.resetPin()
        viewState.showTitle(R.string.text_title_create_new_pin)
    }

    fun biometricAuthenticationSuccessful() {
        when (pinMode) {
            CONFIRM -> viewState.finishScreenWithResult(Activity.RESULT_OK)
            else -> checkAuthState()
        }
    }

    private fun checkAuthState() {
        when {
            interactor.accountHasToken() -> viewState.showHomeScreen()
            else -> {
                when {
                    !interactor.isTouchIdSetUp() && BiometricUtils.isBiometricSupported(AppUtil.getAppContext()) -> viewState.showBiometricSetUpScreen(pinMode)
                    else -> viewState.showVaultAuthScreen()
                }
            }
        }
    }

    fun logoutClicked() {
        viewState.showLogOutDialog()
    }

    fun onAlertDialogPositiveButtonClicked(tag: String?) {
        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.LOG_OUT -> {
                interactor.clearUserData()
                viewState.showAuthScreen()
            }

            AlertDialogFragment.DialogFragmentIdentifier.COMMON_PIN_PATTERN -> {
                newPin = null
                tempCommonPin = null
                viewState.resetPin()
            }
        }
    }

    fun onAlertDialogNegativeButtonClicked(tag: String?) {
        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.COMMON_PIN_PATTERN -> {
                newPin = tempCommonPin
                tempCommonPin = null
                if (pinMode == CHANGE) {
                    viewState.showTitle(R.string.text_title_confirm_new_pin)
                } else {
                    viewState.showTitle(R.string.text_title_confirm_pin)
                }
                viewState.resetPin()
            }
        }
    }

    private fun isCommonPin(pin: String?): Boolean {
        // First check common pins from array.
        if (commonPinArray.contains(pin)) {
            return true
        }

        // Check same symbols like 111111 and etc.
        pin?.forEach {
            if (it != pin[0]) {
                return false
            }
        }

        return true
    }

    fun needHelpClicked() {
        viewState.sendMail(
            Constant.Social.SUPPORT_MAIL,
            SupportManager.createSupportMailSubject(),
            SupportManager.createSupportMailBody(userId = interactor.getUserPublicKey())
        )
    }
}