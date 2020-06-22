package com.lobstr.stellar.vault.presentation.dagger.module.splash

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.splash.SplashInteractor
import com.lobstr.stellar.vault.domain.splash.SplashInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.AuthScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class SplashModule {
    @Provides
    @AuthScope
    internal fun provideSplashInteractor(
        prefsUtil: PrefsUtil,
        keyStoreRepository: KeyStoreRepository
    ): SplashInteractor {
        return SplashInteractorImpl(prefsUtil, keyStoreRepository)
    }
}