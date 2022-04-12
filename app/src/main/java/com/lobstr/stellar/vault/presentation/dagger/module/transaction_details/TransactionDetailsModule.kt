package com.lobstr.stellar.vault.presentation.dagger.module.transaction_details

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.domain.transaction_details.TransactionDetailsInteractor
import com.lobstr.stellar.vault.domain.transaction_details.TransactionDetailsInteractorImpl
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object TransactionDetailsModule {
    @Provides
    fun provideTransactionDetailsInteractor(
        accountRepository: AccountRepository,
        transactionRepository: TransactionRepository,
        stellarRepository: StellarRepository,
        keyStoreRepository: KeyStoreRepository,
        localDataRepository: LocalDataRepository,
        prefsUtil: PrefsUtil
    ): TransactionDetailsInteractor = TransactionDetailsInteractorImpl(
        accountRepository,
        transactionRepository,
        stellarRepository,
        keyStoreRepository,
        localDataRepository,
        prefsUtil
    )
}