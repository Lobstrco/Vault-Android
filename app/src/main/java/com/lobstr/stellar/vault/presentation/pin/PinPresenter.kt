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
import java.security.KeyPair
import javax.inject.Inject

/**
 * Main IDEA - when secretKey was empty - confirmation action, else - save secret key
 */
@InjectViewState
class PinPresenter(private val secretKey: String?) : BasePresenter<PinView>() {

    @Inject
    lateinit var mInteractor: PinInteractor

    private var mNewPin: String? = null

    init {
        LVApplication.sAppComponent.plusPinComponent(PinModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.attachIndicatorDots()
        if (secretKey.isNullOrEmpty()) {
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
            if (!secretKey.isNullOrEmpty()) {
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
            mNewPin.isNullOrEmpty() -> {
                mNewPin = pin
                viewState.showDescriptionMessage(R.string.text_reenter_pin)
                viewState.resetPin()
            }
            mNewPin.equals(pin) -> {
                unsubscribeOnDestroy(
                    mInteractor.saveSecretKey(pin, secretKey!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            viewState.showProgressDialog()
                        }
                        .doOnEvent {
                            viewState.hideProgressDialog()
                        }
                        .doOnComplete {
                            viewState.showHomeScreen()
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
            mInteractor.checkPinValidation(pin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog()
                }
                .doOnEvent { _: KeyPair, _: Throwable? ->
                    viewState.hideProgressDialog()
                }
                .doOnSuccess { keyPair ->
                    if (keyPair.public != null && keyPair.private != null) {
                        if (secretKey.isNullOrEmpty()) {
                            viewState.showHomeScreen()
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
}