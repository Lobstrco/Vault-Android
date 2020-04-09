package com.lobstr.stellar.vault.presentation.dagger.module.config

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.config.ConfigInteractor
import com.lobstr.stellar.vault.domain.config.ConfigInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.ConfigScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class ConfigModule {
    @Provides
    @ConfigScope
    internal fun provideConfigInteractor(
        accountRepository: AccountRepository,
        prefsUtil: PrefsUtil
    ): ConfigInteractor {
        return ConfigInteractorImpl(accountRepository, prefsUtil)
    }
}