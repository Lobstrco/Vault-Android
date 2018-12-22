package com.lobstr.stellar.vault.presentation.pin

import android.app.Activity
import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.pin.PinInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dager.module.pin.PinModule
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Main IDEA - when secretKey was empty - confirmation action, else - save secret key
 */
@InjectViewState
class PinPresenter(private var needCreatePin: Boolean?) : BasePresenter<PinView>() {

    @Inject
    lateinit var interactor: PinInteractor

    private var newPin: String? = null

    init {
        LVApplication.sAppComponent.plusPinComponent(PinModule()).inject(this)
        if (needCreatePin == null) {
            needCreatePin = false
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.attachIndicatorDots()
        if (!needCreatePin!!) {
            viewState.showDescriptionMessage(R.string.text_enter_pin)
        } else {
            viewState.showDescriptionMessage(R.string.text_create_pin)
        }
    }

    /**
     * pin - your completely entered pin
     */
    fun onPinComplete(pin: String?) {
        if (pin != null) {
            if (needCreatePin!!) {
                createPin(pin)
            } else {
                confirmPin(pin)
            }
        } else {
            viewState.showErrorMessage(R.string.text_error_incorrect_pin)
            viewState.resetPin()
        }
    }

    private fun createPin(pin: String) {
        when {
            newPin.isNullOrEmpty() -> {
                newPin = pin
                viewState.showDescriptionMessage(R.string.text_reenter_pin)
                viewState.resetPin()
            }
            newPin.equals(pin) -> {
                unsubscribeOnDestroy(
                    interactor.savePin(pin)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            viewState.showProgressDialog()
                        }
                        .doOnEvent {
                            viewState.hideProgressDialog()
                        }
                        .doOnComplete {
                            checkAuthState()
                        }
                        .subscribe()
                )
            }
            else -> {
                viewState.showErrorMessage(R.string.text_error_incorrect_pin)
                viewState.resetPin()
            }
        }
    }

    private fun confirmPin(pin: String) {
        unsubscribeOnDestroy(
            interactor.checkPinValidation(pin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog()
                }
                .doOnEvent { _: Boolean, _: Throwable? ->
                    viewState.hideProgressDialog()
                }
                .doOnSuccess { success ->
                    if (success) {
                        if (!needCreatePin!!) {
                            checkAuthState()
                        } else {
                            viewState.finishScreenWithResult(Activity.RESULT_OK)
                        }
                    } else {
                        viewState.showErrorMessage(R.string.text_error_incorrect_pin)
                        viewState.resetPin()
                    }
                }
                .subscribe()
        )
    }

    private fun checkAuthState() {
        when {
            interactor.isUserSignerForLobstr() -> viewState.showHomeScreen()
            else -> viewState.showVaultAuthScreen()
        }
    }
}