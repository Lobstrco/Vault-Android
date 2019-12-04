package com.lobstr.stellar.vault.presentation.home.dashboard

import com.lobstr.stellar.vault.presentation.entities.account.Account
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface DashboardView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun initSignedAccountsRecycledView()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun notifySignedAccountsAdapter(accounts: List<Account>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showPublicKey(publicKey: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showSignersCount(count: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showDashboardInfo(count: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showSignersScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage(message: String)

    @StateStrategyType(SkipStrategy::class)
    fun navigateToTransactionList()

    @StateStrategyType(SkipStrategy::class)
    fun showEditAccountDialog(address: String)

    @StateStrategyType(SkipStrategy::class)
    fun copyToClipBoard(text: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showSignersProgress(show: Boolean)
}