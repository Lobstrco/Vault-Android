package com.lobstr.stellar.vault.presentation.dagger.module.accounts

import com.lobstr.stellar.vault.domain.accounts.AccountsInteractor
import com.lobstr.stellar.vault.domain.accounts.AccountsInteractorImpl
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object AccountsModule {
    @Provides
    fun provideAccountsInteractor(
        prefsUtil: PrefsUtil,
        stellarRepository: StellarRepository,
        vaultAuthRepository: VaultAuthRepository,
        keyStoreRepository: KeyStoreRepository,
        localDataRepository: LocalDataRepository
    ): AccountsInteractor = AccountsInteractorImpl(
        prefsUtil,
        stellarRepository,
        vaultAuthRepository,
        keyStoreRepository,
        localDataRepository
    )
}