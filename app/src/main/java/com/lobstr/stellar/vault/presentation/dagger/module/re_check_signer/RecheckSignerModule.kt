package com.lobstr.stellar.vault.presentation.dagger.module.re_check_signer

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.re_check_signer.RecheckSignerInteractor
import com.lobstr.stellar.vault.domain.re_check_signer.RecheckSignerInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.VaultAuthScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class RecheckSignerModule {
    @Provides
    @VaultAuthScope
    fun provideRecheckSignerInteractor(
        accountRepository: AccountRepository,
        keyStoreRepository: KeyStoreRepository,
        prefsUtil: PrefsUtil
    ): RecheckSignerInteractor {
        return RecheckSignerInteractorImpl(accountRepository, keyStoreRepository, prefsUtil)
    }
}