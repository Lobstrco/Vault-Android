package com.lobstr.stellar.vault.presentation.dagger.module.signed_account

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.signed_account.SignedAccountInteractor
import com.lobstr.stellar.vault.domain.signed_account.SignedAccountInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.SignedAccountScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class SignedAccountModule {

    @Provides
    @SignedAccountScope
    internal fun provideSignedAccountInteractor(
        accountRepository: AccountRepository,
        prefsUtil: PrefsUtil
    ): SignedAccountInteractor {
        return SignedAccountInteractorImpl(accountRepository, prefsUtil)
    }
}