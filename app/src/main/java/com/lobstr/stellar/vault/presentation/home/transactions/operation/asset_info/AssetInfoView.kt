package com.lobstr.stellar.vault.presentation.home.transactions.operation.asset_info

import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

@Skip
interface AssetInfoView : MvpView {
    fun openExplorer(url: String)
}