package com.lobstr.stellar.vault.presentation.auth.tangem

import androidx.annotation.ColorRes
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

@Skip
interface TangemView : MvpView {

    fun setupToolbar(
        @ColorRes toolbarColor: Int
    )

    fun showTangemScreen()

    fun showHelpScreen(articleId: Long)

    fun showVaultAuthScreen()

    fun showTangemCreateWalletScreen(tangemInfo: TangemInfo)

    fun showMessage(message: String?)

    fun showNfcNotAvailable()

    fun showWebPage(url: String)
}