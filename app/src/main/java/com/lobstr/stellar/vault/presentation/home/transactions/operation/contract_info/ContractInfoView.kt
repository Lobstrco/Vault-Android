package com.lobstr.stellar.vault.presentation.home.transactions.operation.contract_info

import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

@Skip
interface ContractInfoView : MvpView {
    fun openExplorer(url: String)
}
