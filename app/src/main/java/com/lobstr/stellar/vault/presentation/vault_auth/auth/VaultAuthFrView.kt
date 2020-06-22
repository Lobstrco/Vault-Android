package com.lobstr.stellar.vault.presentation.vault_auth.auth

import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface VaultAuthFrView : MvpView {

    @AddToEndSingle
    fun setupInfo(
        showIdentityLogo: Boolean,
        showTangemLogo: Boolean,
        identityIconUrl: String,
        title: String?,
        description: String?,
        descriptionMain: String,
        button: String
    )

    @Skip
    fun copyToClipBoard(text: String)

    @Skip
    fun showTangemScreen(tangemInfo: TangemInfo)

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @AddToEndSingle
    fun showIdentityContent(show: Boolean)

    @Skip
    fun showMessage(message: String?)

    @AddToEndSingle
    fun showHomeScreen()

    @AddToEndSingle
    fun showAuthScreen()

    @Skip
    fun showLogOutDialog(title: String?, message: String?)
}