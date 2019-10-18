package com.lobstr.stellar.vault.presentation.dagger.module.recovery_key

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.recovery_key.RecoverKeyInteractor
import com.lobstr.stellar.vault.domain.recovery_key.RecoverKeyInteractorImpl
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.dagger.scope.AuthScope
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
    ): RecoverKeyInteractor {
        return RecoverKeyInteractorImpl(stellarRepository, keyStoreRepository, prefsUtil)
    }
}