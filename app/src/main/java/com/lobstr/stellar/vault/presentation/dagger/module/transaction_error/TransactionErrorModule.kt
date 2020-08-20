package com.lobstr.stellar.vault.presentation.dagger.module.transaction_error

import com.lobstr.stellar.vault.domain.transaction_error.TransactionErrorInteractor
import com.lobstr.stellar.vault.domain.transaction_error.TransactionErrorInteractorImpl
import com.lobstr.stellar.vault.presentation.home.transactions.submit_error.ErrorPresenter
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object TransactionErrorModule {

    @Provides
    fun provideErrorPresenter(
        transactionErrorInteractor: TransactionErrorInteractor
    ): ErrorPresenter {
        return ErrorPresenter(transactionErrorInteractor)
    }

    @Provides
    fun provideTransactionErrorInteractor(
        prefsUtil: PrefsUtil
    ): TransactionErrorInteractor {
        return TransactionErrorInteractorImpl(
            prefsUtil
        )
    }
}