package com.lobstr.stellar.vault.presentation.pin.main

import android.app.Activity
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.pin.PinInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
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
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


class PinFrPresenter @Inject constructor(private val interactor: PinInteractor) : BasePresenter<PinFrView>() {

    var pinMode: Byte = ENTER

    // Used for determine back button visibility on pin flow start.
    private var isHomeAsUpVisibleOnStart: Boolean = false

    // Temp value for handle pin creation after change.
    private var isCreationAfterChangePinMode: Boolean = false

    // State for indicate Pin confirmation state on UI.
    private var isCreatePinState: Boolean = false

    private var newPin: String? = null
    private var tempCommonPin: String? = null
    private var needBlockPinView = false

    private val commonPinArray: ArrayList<String> =
        arrayListOf(
            "123456", "121212", "101010", "112233", "200000", "696969", "131313",
            "654321", "222111", "111222", "121111", "111112", "011111", "111110",
            "010000", "000111", "111000", "000001", "100000"
        )

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupNavigationBar(
            if (pinMode != ENTER) android.R.color.transparent else R.color.color_primary,
            pinMode != ENTER
        )
        viewState.setupToolbar(
            pinMode == ENTER,
            if (pinMode == ENTER) R.color.color_primary else android.R.color.white,
            R.drawable.ic_arrow_back,
            if (pinMode == ENTER) android.R.color.white else R.color.color_primary
        )

        isHomeAsUpVisibleOnStart = pinMode == CONFIRM || pinMode == CHANGE
        viewState.showHomeAsUp(isHomeAsUpVisibleOnStart)

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
            viewState.showBiometricDialog()
        }
    }

    /**
     * @param pin Your completely entered pin
     */
    fun onPinComplete(pin: String) {
        if (pin.isNotEmpty()) {
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
                                viewState.finishScreen(Activity.RESULT_OK)
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
                }
                .doOnEvent { _: Boolean, _: Throwable? ->
                    needBlockPinView = false
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
                                // Save Pin flow state for handle 'Back Press' action.
                                isCreatePinState = true
                                // Show back button for return to the first step of pin creation flow.
                                viewState.showHomeAsUp(true)

                                newPin = pin
                                if (isCreationAfterChangePinMode) {
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
                                CONFIRM -> viewState.finishScreen(
                                    Activity.RESULT_OK
                                )
                                CREATE -> viewState.finishScreen(Activity.RESULT_OK)
                                CHANGE -> showCreateAfterChangePinState()
                                ENTER -> {
                                    when {
                                        !interactor.isTouchIdSetUp() && BiometricUtils.isBiometricSupported(
                                            AppUtil.getAppContext()
                                        ) -> {
                                            viewState.setResult(Activity.RESULT_OK)
                                            viewState.showBiometricSetUpScreen(pinMode)
                                        }
                                        else -> viewState.finishScreen(Activity.RESULT_OK)
                                    }
                                }
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
        viewState.finishScreen(Activity.RESULT_OK)
    }

    private fun checkAuthState() {
        // NOTE Skip pin appearance check for prevent pin screen duplication after finish.
        LVApplication.checkPinAppearance = false
        when {
            interactor.accountHasToken() -> viewState.showHomeScreen()
            else -> {
                when {
                    !interactor.isTouchIdSetUp() && BiometricUtils.isBiometricSupported(AppUtil.getAppContext()) -> {
                        viewState.setResult(Activity.RESULT_OK)
                        viewState.showBiometricSetUpScreen(pinMode)
                        viewState.showHomeAsUp(false)
                    }
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
                if (isCreationAfterChangePinMode) {
                    viewState.showTitle(R.string.text_title_confirm_new_pin)
                } else {
                    viewState.showTitle(R.string.text_title_confirm_pin)
                }
                viewState.resetPin()

                // Save Pin flow state for handle 'Back Press' action.
                isCreatePinState = true
                // Show back button for return to the first step of Pin creation flow.
                viewState.showHomeAsUp(true)
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

    fun onBackPressed() {
        if (isCreatePinState) {
            // Handle 'Back Press' action for return to the first step of Pin creation flow.
            // Reset Pin data.
            newPin = null
            tempCommonPin = null
            viewState.resetPin()
            viewState.showTitle(if(isCreationAfterChangePinMode) R.string.text_title_create_new_pin else R.string.text_title_create_pin)
            viewState.showHomeAsUp(isHomeAsUpVisibleOnStart)
            isCreatePinState = false
        } else {
            // Otherwise - handle regular behavior.
            when (pinMode) {
                ENTER -> viewState.finishApp()
                else -> viewState.finishScreen()
            }
        }
    }
}