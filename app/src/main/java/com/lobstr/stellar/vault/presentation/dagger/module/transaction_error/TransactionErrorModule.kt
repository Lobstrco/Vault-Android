package com.lobstr.stellar.vault.presentation.dagger.module.transaction_error

import com.lobstr.stellar.vault.domain.transaction_error.TransactionErrorInteractor
import com.lobstr.stellar.vault.domain.transaction_error.TransactionErrorInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class TransactionErrorModule {
    @Provides
    @HomeScope
    fun provideTransactionErrorInteractor(
        prefsUtil: PrefsUtil
    ): TransactionErrorInteractor {
        return TransactionErrorInteractorImpl(
            prefsUtil
        )
    }
}