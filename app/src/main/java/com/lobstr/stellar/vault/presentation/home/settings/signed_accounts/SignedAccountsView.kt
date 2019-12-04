package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts

import androidx.annotation.StringRes

import com.lobstr.stellar.vault.presentation.entities.account.Account
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SignedAccountsView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun initRecycledView()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyState()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideEmptyState()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showErrorMessage(message: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun notifyAdapter(accounts: List<Account>)

    @StateStrategyType(SkipStrategy::class)
    fun scrollListToPosition(position: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showEditAccountDialog(address: String)

    @StateStrategyType(SkipStrategy::class)
    fun copyToClipBoard(text: String)
}