package com.lobstr.stellar.vault.presentation.home.transactions.operation.contract_info

import com.lobstr.stellar.vault.presentation.util.Constant.Explorer
import moxy.MvpPresenter

class ContractInfoPresenter(private val contractId: String) :
    MvpPresenter<ContractInfoView>() {
    fun openExplorerClicked() {
        viewState.openExplorer(
            Explorer.CONTRACT.plus(contractId)
                .plus("?${String.format(Explorer.QUERY_FILTER, Explorer.VALUE_INTERFACE)}")
        )
    }
}