package com.lobstr.stellar.vault.presentation.dagger.module.import_xdr

import com.lobstr.stellar.vault.domain.import_xdr.ImportXdrInteractor
import com.lobstr.stellar.vault.domain.import_xdr.ImportXdrInteractorImpl
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object ImportXdrModule {
    @Provides
    fun provideImportXdrModuleInteractor(
        transactionRepository: TransactionRepository
    ): ImportXdrInteractor {
        return ImportXdrInteractorImpl(transactionRepository)
    }
}