package com.lobstr.stellar.vault.presentation.vault_auth.recheck_signer

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.presentation.BasePresenter

@InjectViewState
class RecheckSignerPresenter(private val userPublicKey: String) : BasePresenter<RecheckSignerView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupUserPublicKey(userPublicKey)
    }

    fun recheckClicked() {
        //TODO
    }
}