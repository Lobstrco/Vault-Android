package com.lobstr.stellar.vault.presentation.dagger.module.pin

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.pin.PinInteractor
import com.lobstr.stellar.vault.domain.pin.PinInteractorImpl
import com.lobstr.stellar.vault.presentation.fcm.FcmHelper
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object PinModule {
    @Provides
    fun provideRecoveryKeyInteractor(
        keyStoreRepository: KeyStoreRepository,
        prefsUtil: PrefsUtil,
        fcmHelper: FcmHelper
    ): PinInteractor {
        return PinInteractorImpl(keyStoreRepository, prefsUtil, fcmHelper)
    }
}