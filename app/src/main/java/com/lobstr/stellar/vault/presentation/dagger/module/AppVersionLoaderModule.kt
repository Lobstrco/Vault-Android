package com.lobstr.stellar.vault.presentation.dagger.module

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.app_version.AppVersionLoaderInteractor
import com.lobstr.stellar.vault.domain.app_version.AppVersionLoaderInteractorImpl
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.presentation.app_version.AppVersionLoader
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppVersionLoaderModule {

    @Provides
    @Singleton
    fun provideAppVersionLoader(
        appVersionLoaderInteractor: AppVersionLoaderInteractor,
        eventProviderModule: EventProviderModule
    ): AppVersionLoader {
        return AppVersionLoader(
            appVersionLoaderInteractor,
            eventProviderModule
        )
    }

    @Provides
    @Singleton
    fun provideAppVersionLoaderInteractor(
        prefsUtil: PrefsUtil,
        accountRepository: AccountRepository
    ): AppVersionLoaderInteractor {
        return AppVersionLoaderInteractorImpl(prefsUtil, accountRepository)
    }
}