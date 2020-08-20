package com.lobstr.stellar.vault.presentation.dagger.module.transaction_success

import com.lobstr.stellar.vault.domain.transaction_success.TransactionSuccessInteractor
import com.lobstr.stellar.vault.domain.transaction_success.TransactionSuccessInteractorImpl
import com.lobstr.stellar.vault.presentation.home.transactions.submit_success.SuccessPresenter
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent


@Module
@InstallIn(FragmentComponent::class)
object TransactionSuccessModule {

    @Provides
    fun provideSuccessPresenter(
        transactionSuccessInteractor: TransactionSuccessInteractor
    ): SuccessPresenter {
        return SuccessPresenter(transactionSuccessInteractor)
    }

    @Provides
    fun provideTransactionSuccessInteractor(
        prefsUtil: PrefsUtil
    ): TransactionSuccessInteractor {
        return TransactionSuccessInteractorImpl(
            prefsUtil
        )
    }
}