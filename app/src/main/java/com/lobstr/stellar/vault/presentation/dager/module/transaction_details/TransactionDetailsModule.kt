package com.lobstr.stellar.vault.presentation.dager.module.transaction_details

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.domain.transaction_details.TransactionDetailsInteractor
import com.lobstr.stellar.vault.domain.transaction_details.TransactionDetailsInteractorImpl
import com.lobstr.stellar.vault.presentation.dager.scope.HomeScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class TransactionDetailsModule {
    @Provides
    @HomeScope
     fun provideTransactionDetailsInteractor(
        transactionRepository: TransactionRepository,
        stellarRepository: StellarRepository,
        keyStoreRepository: KeyStoreRepository,
        prefsUtil: PrefsUtil
    ): TransactionDetailsInteractor {
        return TransactionDetailsInteractorImpl(transactionRepository, stellarRepository, keyStoreRepository, prefsUtil)
    }
}