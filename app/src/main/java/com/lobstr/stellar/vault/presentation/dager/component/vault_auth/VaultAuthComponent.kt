package com.lobstr.stellar.vault.presentation.dager.component.vault_auth

import com.lobstr.stellar.vault.presentation.dager.module.vault_auth.VaultAuthModule
import com.lobstr.stellar.vault.presentation.dager.scope.VaultAuthScope
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthPresenter
import dagger.Subcomponent

@Subcomponent(modules = [VaultAuthModule::class])
@VaultAuthScope
interface VaultAuthComponent {
    fun inject(presenter: VaultAuthPresenter)
}