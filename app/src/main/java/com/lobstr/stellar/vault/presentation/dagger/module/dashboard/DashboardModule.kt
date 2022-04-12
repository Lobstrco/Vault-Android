package com.lobstr.stellar.vault.presentation.dagger.module.dashboard

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.dashboard.DashboardInteractor
import com.lobstr.stellar.vault.domain.dashboard.DashboardInteractorImpl
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object DashboardModule {
    @Provides
    fun provideDashboardInteractor(
        transactionRepository: TransactionRepository,
        accountRepository: AccountRepository,
        localDataRepository: LocalDataRepository,
        prefsUtil: PrefsUtil
    ): DashboardInteractor = DashboardInteractorImpl(
        transactionRepository,
        accountRepository,
        localDataRepository,
        prefsUtil
    )
}