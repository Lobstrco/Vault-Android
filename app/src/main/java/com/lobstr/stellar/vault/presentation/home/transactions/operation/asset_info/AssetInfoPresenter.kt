package com.lobstr.stellar.vault.presentation.home.transactions.operation.asset_info

import com.lobstr.stellar.vault.presentation.util.Constant
import moxy.MvpPresenter


class AssetInfoPresenter(private val code: String, private val issuer: String?) :
    MvpPresenter<AssetInfoView>() {
    fun openExplorerClicked() {
        viewState.openExplorer(
            Constant.Explorer.ASSET.plus(code).plus(
                if (issuer.isNullOrEmpty()) {
                    ""
                } else {
                    "-${issuer}"
                }
            )
        )
    }
}