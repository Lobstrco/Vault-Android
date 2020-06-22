package com.lobstr.stellar.vault.presentation.dagger.module.base

import com.lobstr.stellar.vault.domain.base.BaseInteractor
import com.lobstr.stellar.vault.domain.base.BaseInteractorImpl
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import com.lobstr.stellar.vault.presentation.dagger.scope.BaseScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class BaseModule {
    @Provides
    @BaseScope
    internal fun provideBaseInteractor(
        vaultAuthRepository: VaultAuthRepository,
        prefsUtil: PrefsUtil
    ): BaseInteractor {
        return BaseInteractorImpl(vaultAuthRepository, prefsUtil)
    }
}