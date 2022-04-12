package com.lobstr.stellar.vault.presentation.home.dashboard

import com.lobstr.stellar.vault.presentation.entities.account.Account
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface DashboardView : MvpView {

    @AddToEndSingle
    fun initSignedAccountsRecycledView()

    @AddToEndSingle
    fun notifySignedAccountsAdapter(accounts: List<Account>)

    @AddToEndSingle
    fun showVaultInfo(
        hasTangem: Boolean,
        identityIconUrl: String,
        publicKey: String,
        publicKeyTitle: String
    )

    @AddToEndSingle
    fun showSignersCount(count: Int)

    @AddToEndSingle
    fun showDashboardInfo(count: Int?)

    @Skip
    fun showSignersScreen()

    @Skip
    fun showSignerInfoScreen()

    @Skip
    fun showErrorMessage(message: String)

    @Skip
    fun navigateToTransactionList()

    @Skip
    fun showAccountsDialog()

    @Skip
    fun showEditAccountDialog(address: String)

    @Skip
    fun copyToClipBoard(text: String)

    @AddToEndSingle
    fun showSignersProgress(show: Boolean)

    @AddToEndSingle
    fun showSignersEmptyState(show: Boolean)

    @Skip
    fun showRefreshAnimation(show: Boolean)

    @AddToEndSingle
    fun changeAccountName(name: String)
}