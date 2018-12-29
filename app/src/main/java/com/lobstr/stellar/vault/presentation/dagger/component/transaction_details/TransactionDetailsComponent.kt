package com.lobstr.stellar.vault.presentation.dagger.component.transaction_details

import com.lobstr.stellar.vault.presentation.dagger.module.transaction_details.TransactionDetailsModule
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.home.transactions.details.TransactionDetailsPresenter
import dagger.Subcomponent

@Subcomponent(modules = [TransactionDetailsModule::class])
@HomeScope
interface TransactionDetailsComponent {
    fun inject(presenter: TransactionDetailsPresenter)
}