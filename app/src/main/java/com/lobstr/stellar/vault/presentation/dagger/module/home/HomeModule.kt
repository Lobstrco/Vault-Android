package com.lobstr.stellar.vault.presentation.dagger.module.home

import com.lobstr.stellar.vault.domain.home.HomeInteractor
import com.lobstr.stellar.vault.domain.home.HomeInteractorImpl
import com.lobstr.stellar.vault.presentation.fcm.FcmHelper
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@Module
@InstallIn(ActivityComponent::class)
object HomeModule {
    @Provides
    fun provideHomeInteractor(prefsUtil: PrefsUtil, fcmHelper: FcmHelper): HomeInteractor {
        return HomeInteractorImpl(prefsUtil, fcmHelper)
    }
}