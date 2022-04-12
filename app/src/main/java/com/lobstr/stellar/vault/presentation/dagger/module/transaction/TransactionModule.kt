package com.lobstr.stellar.vault.presentation.dagger.module.transaction

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.domain.transaction.TransactionInteractor
import com.lobstr.stellar.vault.domain.transaction.TransactionInteractorImpl
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object TransactionModule {
    @Provides
    fun provideTransactionInteractor(
        accountRepository: AccountRepository,
        transactionRepository: TransactionRepository,
        localDataRepository: LocalDataRepository,
        prefsUtil: PrefsUtil
    ): TransactionInteractor = TransactionInteractorImpl(
        accountRepository,
        transactionRepository,
        localDataRepository,
        prefsUtil
    )
}