package com.lobstr.stellar.vault.presentation.dager.component.transaction_details

import com.lobstr.stellar.vault.presentation.dager.module.transaction_details.TransactionDetailsModule
import com.lobstr.stellar.vault.presentation.dager.scope.HomeScope
import com.lobstr.stellar.vault.presentation.home.transactions.details.TransactionDetailsPresenter
import dagger.Subcomponent

@Subcomponent(modules = [TransactionDetailsModule::class])
@HomeScope
interface TransactionDetailsComponent {
    fun inject(presenter: TransactionDetailsPresenter)
}