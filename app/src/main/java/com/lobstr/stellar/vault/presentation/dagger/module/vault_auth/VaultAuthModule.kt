package com.lobstr.stellar.vault.presentation.dagger.module.vault_auth

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import com.lobstr.stellar.vault.domain.vault_auth_screen.VaultAuthInteractor
import com.lobstr.stellar.vault.domain.vault_auth_screen.VaultAuthInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.VaultAuthScope
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
        accountRepository: AccountRepository,
        keyStoreRepository: KeyStoreRepository
    ): VaultAuthInteractor {
        return VaultAuthInteractorImpl(
            vaultAuthRepository,
            stellarRepository,
            accountRepository,
            keyStoreRepository,
            prefsUtil
        )
    }
}