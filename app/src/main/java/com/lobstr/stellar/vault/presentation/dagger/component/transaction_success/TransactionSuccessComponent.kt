package com.lobstr.stellar.vault.presentation.dagger.component.transaction_success

import com.lobstr.stellar.vault.presentation.dagger.module.transaction_success.TransactionSuccessModule
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.home.transactions.submit_success.SuccessPresenter
import dagger.Subcomponent

@Subcomponent(modules = [TransactionSuccessModule::class])
@HomeScope
interface TransactionSuccessComponent {
    fun inject(presenter: SuccessPresenter)
}