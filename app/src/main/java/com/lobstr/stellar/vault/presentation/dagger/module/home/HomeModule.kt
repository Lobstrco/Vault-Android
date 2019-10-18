package com.lobstr.stellar.vault.presentation.dagger.module.home

import com.lobstr.stellar.vault.domain.home.HomeInteractor
import com.lobstr.stellar.vault.domain.home.HomeInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides


@Module
class HomeModule {
    @Provides
    @HomeScope
    internal fun provideHomeInteractor(prefsUtil: PrefsUtil): HomeInteractor {
        return HomeInteractorImpl(prefsUtil)
    }
}