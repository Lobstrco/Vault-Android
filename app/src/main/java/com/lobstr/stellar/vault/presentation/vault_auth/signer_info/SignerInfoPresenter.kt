package com.lobstr.stellar.vault.presentation.vault_auth.signer_info

import com.lobstr.stellar.vault.domain.signer_info.SignerInfoInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.signer_info.SignerInfoModule
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SignerInfoPresenter : BasePresenter<SignerInfoView>() {

    companion object {
        private const val REQUEST_PERIOD = 3L
    }

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    @Inject
    lateinit var interactor: SignerInfoInteractor

    private var checkLobstrAppIntervalSubscription: Disposable? = null

    init {
        LVApplication.appComponent.plusSignerInfoComponent(SignerInfoModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        registerEventProvider()
        viewState.setupUserPublicKey(
            AppUtil.ellipsizeStrInMiddle(
                interactor.getUserPublicKey(),
                PK_TRUNCATE_COUNT
            )
        )
    }

    private fun registerEventProvider() {
        unsubscribeOnDestroy(
            eventProviderModule.notificationEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Notification.Type.SIGNED_NEW_ACCOUNT-> {
                            viewState.finishScreen()
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    override fun attachView(view: SignerInfoView?) {
        super.attachView(view)
        viewState.checkExistenceLobstrApp()
    }

    override fun detachView(view: SignerInfoView) {
        super.detachView(view)
        startCheckExistenceLobstrAppWithInterval(false)
    }

    fun startCheckExistenceLobstrAppWithInterval(start: Boolean) {
        if (start) {
            if (checkLobstrAppIntervalSubscription?.isDisposed != false) {
                checkLobstrAppIntervalSubscription =
                    Observable.interval(0, REQUEST_PERIOD, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { viewState.checkExistenceLobstrApp() },
                            { it.printStackTrace() })
            }
        } else {
            checkLobstrAppIntervalSubscription?.dispose()
        }
    }

    fun downloadLobstrAppClicked() {
        viewState.downloadLobstrApp()
    }

    fun openLobstrAppClicked() {
        viewState.openLobstrMultisigSetupScreen()
    }

    fun copyUserPublicKeyClicked() {
        interactor.getUserPublicKey()?.let { viewState.copyToClipBoard(it) }
    }

    fun showQrClicked() {
        interactor.getUserPublicKey()?.let { viewState.showPublicKeyDialog(it) }
    }

    fun infoClicked() {
        viewState.showHelpScreen(interactor.getUserPublicKey())
    }
}