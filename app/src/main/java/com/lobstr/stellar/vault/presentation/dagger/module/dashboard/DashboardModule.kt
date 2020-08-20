package com.lobstr.stellar.vault.presentation.dagger.module.dashboard

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.dashboard.DashboardInteractor
import com.lobstr.stellar.vault.domain.dashboard.DashboardInteractorImpl
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.presentation.home.dashboard.DashboardPresenter
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object DashboardModule {

    @Provides
    fun provideDashboardPresenter(
        dashboardInteractor: DashboardInteractor,
        eventProviderModule: EventProviderModule
    ): DashboardPresenter {
        return DashboardPresenter(
            dashboardInteractor,
            eventProviderModule
        )
    }

    @Provides
    fun provideDashboardInteractor(
        transactionRepository: TransactionRepository,
        accountRepository: AccountRepository,
        prefsUtil: PrefsUtil
    ): DashboardInteractor {
        return DashboardInteractorImpl(transactionRepository, accountRepository, prefsUtil)
    }
}