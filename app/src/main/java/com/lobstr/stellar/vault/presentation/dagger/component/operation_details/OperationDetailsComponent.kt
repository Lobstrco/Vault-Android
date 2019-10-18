package com.lobstr.stellar.vault.presentation.dagger.component.operation_details

import com.lobstr.stellar.vault.presentation.dagger.module.operation_details.OperationDetailsModule
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details.OperationDetailsPresenter
import dagger.Subcomponent

@Subcomponent(modules = [OperationDetailsModule::class])
@HomeScope
interface OperationDetailsComponent {
    fun inject(presenter: OperationDetailsPresenter)
}