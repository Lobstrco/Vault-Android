package com.lobstr.stellar.vault.presentation.dagger.component.transaction

import com.lobstr.stellar.vault.presentation.dagger.module.transaction.TransactionModule
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.home.transactions.TransactionsPresenter
import dagger.Subcomponent

@Subcomponent(modules = [TransactionModule::class])
@HomeScope
interface TransactionComponent {
    fun inject(presenter: TransactionsPresenter)
}