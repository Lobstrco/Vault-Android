package com.lobstr.stellar.vault.presentation.dagger.component.transaction_error

import com.lobstr.stellar.vault.presentation.dagger.module.transaction_error.TransactionErrorModule
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.home.transactions.submit_error.ErrorPresenter
import dagger.Subcomponent

@Subcomponent(modules = [TransactionErrorModule::class])
@HomeScope
interface TransactionErrorComponent {
    fun inject(presenter: ErrorPresenter)
}