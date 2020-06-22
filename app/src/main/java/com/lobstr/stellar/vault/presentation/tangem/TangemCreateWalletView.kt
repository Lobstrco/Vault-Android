package com.lobstr.stellar.vault.presentation.tangem

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface TangemCreateWalletView : MvpView {

    @AddToEndSingle
    fun setupToolbar(
        @ColorRes toolbarColor: Int,
        @DrawableRes upArrow: Int,
        @ColorRes upArrowColor: Int,
        @ColorRes titleColor: Int
    )

    @Skip
    fun showNfcCheckDialog()

    @Skip
    fun showNfcDeviceSettings()

    @Skip
    fun showTangemScreen(tangemInfo: TangemInfo)

    @Skip
    fun successfullyCompleteOperation(tangemInfo: TangemInfo?)

    @Skip
    fun showMessage(message: String?)

    @Skip
    fun showHelpScreen(articleId: Long)
}