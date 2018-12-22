package com.lobstr.stellar.vault.presentation.vault_auth.recheck_signer

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface RecheckSignerView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupUserPublicKey(userPublicKey: String?)
}