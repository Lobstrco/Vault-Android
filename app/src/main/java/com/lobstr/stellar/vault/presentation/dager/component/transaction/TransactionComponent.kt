package com.lobstr.stellar.vault.presentation.dager.component.transaction

import com.lobstr.stellar.vault.presentation.dager.module.transaction.TransactionModule
import com.lobstr.stellar.vault.presentation.dager.scope.HomeScope
import com.lobstr.stellar.vault.presentation.home.transactions.TransactionsPresenter
import dagger.Subcomponent

@Subcomponent(modules = [TransactionModule::class])
@HomeScope
interface TransactionComponent {
    fun inject(presenter: TransactionsPresenter)
}