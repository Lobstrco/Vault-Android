package com.lobstr.stellar.vault.presentation.dager.module.recovery_key

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.recovery_key.RecoveryKeyInteractor
import com.lobstr.stellar.vault.domain.recovery_key.RecoveryKeyInteractorImpl
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.dager.scope.AuthScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class RecoveryKeyModule {

    @Provides
    @AuthScope
    internal fun provideRecoveryKeyInteractor(
        stellarRepository: StellarRepository,
        keyStoreRepository: KeyStoreRepository,
        prefsUtil: PrefsUtil
    ): RecoveryKeyInteractor {
        return RecoveryKeyInteractorImpl(stellarRepository, keyStoreRepository, prefsUtil)
    }
}