package com.lobstr.stellar.vault.presentation.dagger.module.dashboard

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.dashboard.DashboardInteractor
import com.lobstr.stellar.vault.domain.dashboard.DashboardInteractorImpl
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class DashboardModule {

    @Provides
    @HomeScope
    internal fun provideDashboardInteractor(
        transactionRepository: TransactionRepository,
        accountRepository: AccountRepository,
        prefsUtil: PrefsUtil
    ): DashboardInteractor {
        return DashboardInteractorImpl(transactionRepository, accountRepository, prefsUtil)
    }
}