package com.lobstr.stellar.vault.presentation.pin

import android.app.Activity
import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.pin.PinInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.pin.PinModule
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Main IDEA - when secretKey was empty - confirmation action, else - save secret key
 */
@InjectViewState
class PinPresenter(
    private var needCreatePin: Boolean?, private var needChangePin: Boolean?,
    private var needConfirmPin: Boolean?
) :
    BasePresenter<PinView>() {

    @Inject
    lateinit var interactor: PinInteractor

    private var newPin: String? = null

    private val commonPinArray: ArrayList<String> = arrayListOf("123456", "121212")

    init {
        LVApplication.sAppComponent.plusPinComponent(PinModule()).inject(this)
        if (needCreatePin == null) {
            needCreatePin = false
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        when {
            needCreatePin!! -> {
                viewState.showTitle(R.string.text_title_create_pin)
                viewState.setScreenStyle(PinActivity.STYLE_CREATE_PIN)
            }
            needChangePin!! -> {
                viewState.showTitle(R.string.text_title_enter_old_pin)
                viewState.setScreenStyle(PinActivity.STYLE_CREATE_PIN)
            }
            needConfirmPin!! -> {
                viewState.showTitle(R.string.text_title_confirm_pin)
                viewState.setScreenStyle(PinActivity.STYLE_CREATE_PIN)
            }
            else -> {
                viewState.setScreenStyle(PinActivity.STYLE_ENTER_PIN)
            }
        }
    }

    override fun attachView(view: PinView?) {
        super.attachView(view)

        // logic for show fingerprint
        if (!needChangePin!! && !needCreatePin!! && interactor.isTouchIdEnabled() &&
            BiometricUtils.isFingerprintAvailable(LVApplication.sAppComponent.context)
        ) {
            viewState.showBiometricDialog()
        }
    }

    /**
     * pin - your completely entered pin
     */
    fun onPinComplete(pin: String?) {
        // TODO check viewState.showCommonPinPatternDialog()
        if (pin != null) {
            when {
                needCreatePin!! -> createPin(pin)
                needChangePin!! -> confirmPin(pin, false)
                needConfirmPin!! -> confirmPin(pin, false)
                else -> confirmPin(pin, false)
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
                unsubscribeOnDestroy(
                    interactor.savePin(pin)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            viewState.showProgressDialog(true)
                        }
                        .doOnEvent {
                            viewState.showProgressDialog(false)
                        }
                        .doOnComplete {
                            if (needChangePin!!) {
                                viewState.finishScreenWithResult(Activity.RESULT_OK)
                            } else {
                                checkAuthState()
                            }
                        }
                        .subscribe()
                )
            }
            else -> {
                when {
                    needChangePin!! -> viewState.showErrorMessage(R.string.text_error_pin_do_not_match)
                    else -> viewState.showErrorMessage(R.string.text_error_incorrect_pin)
                }
                viewState.resetPin()
            }
        }
    }

    private fun confirmPin(pin: String, needCheckOldPint: Boolean) {
        unsubscribeOnDestroy(
            interactor.checkPinValidation(pin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _: Boolean, _: Throwable? ->
                    viewState.showProgressDialog(false)
                }
                .doOnSuccess { success ->
                    if (needCheckOldPint) {
                        if (success) {
                            viewState.showErrorMessage(R.string.text_error_use_old_pin)
                            viewState.resetPin()
                        } else {
                            newPin = pin
                            if (needChangePin!!) {
                                viewState.showTitle(R.string.text_title_confirm_new_pin)
                            } else {
                                viewState.showTitle(R.string.text_title_confirm_pin)
                            }
                            viewState.resetPin()
                        }
                    } else {
                        if (success) {
                            when {
                                needConfirmPin!! -> viewState.finishScreenWithResult(Activity.RESULT_OK)
                                needCreatePin!! -> viewState.finishScreenWithResult(Activity.RESULT_OK)
                                needChangePin!! -> showCreatePinState()
                                else -> checkAuthState()
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

    private fun showCreatePinState() {
        needCreatePin = true
        viewState.resetPin()
        viewState.showTitle(R.string.text_title_create_new_pin)
    }

    fun biometricAuthenticationSuccessful() {
        when {
            needConfirmPin!! -> viewState.finishScreenWithResult(Activity.RESULT_OK)
            else -> checkAuthState()
        }
    }

    private fun checkAuthState() {
        when {
            interactor.accountHasSigners() -> viewState.showHomeScreen()
            else -> {
                when {
                    !interactor.isTouchIdSetUp() && BiometricUtils.isBiometricSupported(LVApplication.sAppComponent.context) -> viewState.showFingerprintSetUpScreen()
                    else -> viewState.showVaultAuthScreen()
                }
            }
        }
    }

    fun logoutClicked() {
        viewState.showLogOutDialog()
    }

    fun onAlertDialogPositiveButtonClicked(tag: String?) {
        if (tag.isNullOrEmpty()) {
            return
        }

        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.LOG_OUT -> {
                interactor.clearUserData()
                viewState.showAuthScreen()
            }

            AlertDialogFragment.DialogFragmentIdentifier.COMMON_PIN_PATTERN -> {
                // TODO CHANGE PIN clicked
            }
        }
    }

    fun onAlertDialogNegativeButtonClicked(tag: String?) {
        if (tag.isNullOrEmpty()) {
            return
        }

        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.COMMON_PIN_PATTERN -> {
                // TODO CONTINUE clicked
            }
        }
    }


    // TODO check this method
    private fun isCommonPin(pin: String): Boolean {
        // first check common pins from array
        if (commonPinArray.contains(pin)) {
            return true
        }

        // check same symbols like 111111 and etc
        pin.forEach {
            if (it != pin[0]) {
                return false
            }
        }

        return true
    }
}