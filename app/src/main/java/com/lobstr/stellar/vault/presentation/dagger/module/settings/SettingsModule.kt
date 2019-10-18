package com.lobstr.stellar.vault.presentation.dagger.module.settings

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.settings.SettingsInteractor
import com.lobstr.stellar.vault.domain.settings.SettingsInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class SettingsModule {
    @Provides
    @HomeScope
    internal fun provideSettingsInteractor(
        prefsUtil: PrefsUtil,
        accountRepository: AccountRepository,
        keyStoreRepository: KeyStoreRepository
    ): SettingsInteractor {
        return SettingsInteractorImpl(prefsUtil, accountRepository, keyStoreRepository)
    }
}