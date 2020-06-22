package com.lobstr.stellar.vault.presentation.dagger.module.transaction_success

import com.lobstr.stellar.vault.domain.transaction_success.TransactionSuccessInteractor
import com.lobstr.stellar.vault.domain.transaction_success.TransactionSuccessInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides


@Module
class TransactionSuccessModule {
    @Provides
    @HomeScope
    fun provideTransactionSuccessInteractor(
        prefsUtil: PrefsUtil
    ): TransactionSuccessInteractor {
        return TransactionSuccessInteractorImpl(
            prefsUtil
        )
    }
}