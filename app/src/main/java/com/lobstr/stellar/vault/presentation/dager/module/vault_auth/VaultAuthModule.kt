package com.lobstr.stellar.vault.presentation.dager.module.vault_auth

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import com.lobstr.stellar.vault.domain.vault_auth_screen.VaultAuthInteractor
import com.lobstr.stellar.vault.domain.vault_auth_screen.VaultAuthInteractorImpl
import com.lobstr.stellar.vault.presentation.dager.scope.VaultAuthScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class VaultAuthModule {
    @Provides
    @VaultAuthScope
    internal fun provideVaultAuthInteractor(
        prefsUtil: PrefsUtil,
        vaultAuthRepository: VaultAuthRepository,
        stellarRepository: StellarRepository,
        keyStoreRepository: KeyStoreRepository
    ): VaultAuthInteractor {
        return VaultAuthInteractorImpl(vaultAuthRepository, stellarRepository, keyStoreRepository, prefsUtil)
    }
}