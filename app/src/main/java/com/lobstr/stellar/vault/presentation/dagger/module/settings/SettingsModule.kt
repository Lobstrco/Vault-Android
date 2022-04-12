package com.lobstr.stellar.vault.presentation.dagger.module.settings

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.domain.settings.SettingsInteractor
import com.lobstr.stellar.vault.domain.settings.SettingsInteractorImpl
import com.lobstr.stellar.vault.presentation.fcm.FcmHelper
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object SettingsModule {
    @Provides
    fun provideSettingsInteractor(
        prefsUtil: PrefsUtil,
        accountRepository: AccountRepository,
        keyStoreRepository: KeyStoreRepository,
        localDataRepository: LocalDataRepository,
        fcmHelper: FcmHelper
    ): SettingsInteractor = SettingsInteractorImpl(
        prefsUtil,
        accountRepository,
        keyStoreRepository,
        localDataRepository,
        fcmHelper
    )
}