package com.lobstr.stellar.vault.presentation.dagger.module.operation_details

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.operation_details.OperationDetailsInteractor
import com.lobstr.stellar.vault.domain.operation_details.OperationDetailsInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object OperationDetailsModule{
    @Provides
    fun provideOperationDetailsInteractor(
        accountRepository: AccountRepository
    ): OperationDetailsInteractor {
        return OperationDetailsInteractorImpl(
            accountRepository
        )
    }
}