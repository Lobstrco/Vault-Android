package com.lobstr.stellar.vault.presentation.dagger.component.signer_info

import com.lobstr.stellar.vault.presentation.dagger.module.signer_info.SignerInfoModule
import com.lobstr.stellar.vault.presentation.dagger.scope.VaultAuthScope
import com.lobstr.stellar.vault.presentation.vault_auth.signer_info.SignerInfoPresenter
import dagger.Subcomponent

@Subcomponent(modules = [SignerInfoModule::class])
@VaultAuthScope
interface SignerInfoComponent {
    fun inject(presenter: SignerInfoPresenter)
}