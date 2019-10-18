package com.lobstr.stellar.vault.presentation.dagger.module.fcm

import android.content.Context
import com.lobstr.stellar.vault.domain.fcm.FcmInteractor
import com.lobstr.stellar.vault.domain.fcm.FcmInteractorImpl
import com.lobstr.stellar.vault.domain.fcm.FcmRepository
import com.lobstr.stellar.vault.presentation.dagger.scope.FcmInternalScope
import com.lobstr.stellar.vault.presentation.fcm.FcmHelper
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class FcmInternalModule {

    @Provides
    @FcmInternalScope
    fun provideFcmHelper(
        fcmInteractor: FcmInteractor, context: Context
    ): FcmHelper {
        return FcmHelper(context, fcmInteractor)
    }

    @Provides
    @FcmInternalScope
    fun provideFcmInteractor(fcmRepository: FcmRepository, prefsUtil: PrefsUtil): FcmInteractor {
        return FcmInteractorImpl(fcmRepository, prefsUtil)
    }
}