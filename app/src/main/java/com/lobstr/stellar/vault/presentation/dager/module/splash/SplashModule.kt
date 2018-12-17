package com.lobstr.stellar.vault.presentation.dager.module.splash

import com.lobstr.stellar.vault.domain.splash.SplashInteractor
import com.lobstr.stellar.vault.domain.splash.SplashInteractorImpl
import com.lobstr.stellar.vault.presentation.dager.scope.AuthScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class SplashModule {
    @Provides
    @AuthScope
    internal fun provideSplashInteractor(
        prefsUtil: PrefsUtil
    ): SplashInteractor {
        return SplashInteractorImpl(prefsUtil)
    }
}