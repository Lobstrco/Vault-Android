package com.lobstr.stellar.vault.presentation.dagger.module

import android.content.Context
import com.lobstr.stellar.vault.domain.fcm.FcmInteractor
import com.lobstr.stellar.vault.domain.fcm.FcmInteractorImpl
import com.lobstr.stellar.vault.domain.fcm.FcmRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.presentation.fcm.FcmHelper
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FcmModule {

    @Provides
    @Singleton
    fun provideFcmHelper(
        fcmInteractor: FcmInteractor, @ApplicationContext context: Context
    ): FcmHelper = FcmHelper(context, fcmInteractor)

    @Provides
    @Singleton
    fun provideFcmInteractor(
        fcmRepository: FcmRepository,
        localDataRepository: LocalDataRepository,
        prefsUtil: PrefsUtil
    ): FcmInteractor = FcmInteractorImpl(fcmRepository, localDataRepository, prefsUtil)
}