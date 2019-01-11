package com.lobstr.stellar.vault.presentation.vault_auth.signer_info

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.lobstr.stellar.vault.domain.signer_info.SignerInfoInteractor
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.signer_info.SignerInfoModule
import javax.inject.Inject

@InjectViewState
class SignerInfoPresenter : MvpPresenter<SignerInfoView>() {

    @Inject
    lateinit var interactor: SignerInfoInteractor

    init {
        LVApplication.sAppComponent.plusSignerInfoComponent(SignerInfoModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupUserPublicKey(interactor.getUserPublicKey())
    }

    fun copyUserPublicKey(userPublicKey: String?) {
        if (userPublicKey.isNullOrEmpty()) {
            return
        }

        viewState.copyToClipBoard(userPublicKey)
    }

    fun btnNextClicked() {
        viewState.showRecheckSingerScreen()
    }
}