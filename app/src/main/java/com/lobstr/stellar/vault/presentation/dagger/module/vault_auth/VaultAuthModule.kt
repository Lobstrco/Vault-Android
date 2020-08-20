package com.lobstr.stellar.vault.presentation.dagger.module.vault_auth

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import com.lobstr.stellar.vault.domain.vault_auth_screen.VaultAuthInteractor
import com.lobstr.stellar.vault.domain.vault_auth_screen.VaultAuthInteractorImpl
import com.lobstr.stellar.vault.presentation.fcm.FcmHelper
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import com.lobstr.stellar.vault.presentation.vault_auth.auth.VaultAuthFrPresenter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object VaultAuthModule {

    @Provides
    fun provideVaultAuthFrPresenter(
        vaultAuthInteractor: VaultAuthInteractor,
        eventProviderModule: EventProviderModule
    ): VaultAuthFrPresenter {
        return VaultAuthFrPresenter(vaultAuthInteractor, eventProviderModule)
    }

    @Provides
    fun provideVaultAuthInteractor(
        prefsUtil: PrefsUtil,
        vaultAuthRepository: VaultAuthRepository,
        stellarRepository: StellarRepository,
        accountRepository: AccountRepository,
        keyStoreRepository: KeyStoreRepository,
        fcmHelper: FcmHelper
    ): VaultAuthInteractor {
        return VaultAuthInteractorImpl(
            vaultAuthRepository,
            stellarRepository,
            accountRepository,
            keyStoreRepository,
            prefsUtil,
            fcmHelper
        )
    }
}