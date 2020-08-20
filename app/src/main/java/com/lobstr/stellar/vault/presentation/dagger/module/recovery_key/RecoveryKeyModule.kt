package com.lobstr.stellar.vault.presentation.dagger.module.recovery_key

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.recovery_key.RecoverKeyInteractor
import com.lobstr.stellar.vault.domain.recovery_key.RecoverKeyInteractorImpl
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.auth.restore_key.RecoverKeyFrPresenter
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object RecoveryKeyModule {

    @Provides
    fun provideRecoverKeyFrPresenter(
        recoverKeyInteractor: RecoverKeyInteractor
    ): RecoverKeyFrPresenter {
        return RecoverKeyFrPresenter(recoverKeyInteractor)
    }

    @Provides
    fun provideRecoveryKeyInteractor(
        stellarRepository: StellarRepository,
        keyStoreRepository: KeyStoreRepository,
        prefsUtil: PrefsUtil
    ): RecoverKeyInteractor {
        return RecoverKeyInteractorImpl(stellarRepository, keyStoreRepository, prefsUtil)
    }
}