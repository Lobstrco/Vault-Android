package com.lobstr.stellar.vault.presentation.dagger.component.re_check_signer

import com.lobstr.stellar.vault.presentation.dagger.module.re_check_signer.RecheckSignerModule
import com.lobstr.stellar.vault.presentation.dagger.scope.VaultAuthScope
import com.lobstr.stellar.vault.presentation.vault_auth.recheck_signer.RecheckSignerPresenter
import dagger.Subcomponent

@Subcomponent(modules = [RecheckSignerModule::class])
@VaultAuthScope
interface RecheckSignerComponent {
    fun inject(presenter: RecheckSignerPresenter)
}