package com.lobstr.stellar.vault.presentation.dagger.module.pin

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.pin.PinInteractor
import com.lobstr.stellar.vault.domain.pin.PinInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.PinScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class PinModule {

    @Provides
    @PinScope
    internal fun provideRecoveryKeyInteractor(
        keyStoreRepository: KeyStoreRepository,
        prefsUtil: PrefsUtil
    ): PinInteractor {
        return PinInteractorImpl(keyStoreRepository, prefsUtil)
    }
}