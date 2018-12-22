package com.lobstr.stellar.vault.presentation.dager.module.transaction

import com.lobstr.stellar.vault.domain.transaction.TransactionInteractor
import com.lobstr.stellar.vault.domain.transaction.TransactionInteractorImpl
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.dager.scope.HomeScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class TransactionModule {

    @Provides
    @HomeScope
    internal fun provideRecoveryKeyInteractor(
        transactionRepository: TransactionRepository,
        prefsUtil: PrefsUtil
    ): TransactionInteractor {
        return TransactionInteractorImpl(transactionRepository, prefsUtil)
    }
}