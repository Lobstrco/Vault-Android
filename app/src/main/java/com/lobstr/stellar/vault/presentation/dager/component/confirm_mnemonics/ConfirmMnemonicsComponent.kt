package com.lobstr.stellar.vault.presentation.dager.component.confirm_mnemonics

import com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic.ConfirmMnemonicsPresenter
import com.lobstr.stellar.vault.presentation.dager.module.confirm_mnemonics.ConfirmMnemonicsModule
import com.lobstr.stellar.vault.presentation.dager.scope.AuthScope
import dagger.Subcomponent

@Subcomponent(modules = [ConfirmMnemonicsModule::class])
@AuthScope
interface ConfirmMnemonicsComponent {
    fun inject(presenter: ConfirmMnemonicsPresenter)
}