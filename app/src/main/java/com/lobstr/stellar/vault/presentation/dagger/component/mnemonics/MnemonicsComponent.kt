package com.lobstr.stellar.vault.presentation.dagger.component.mnemonics

import com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic.MnemonicsPresenter
import com.lobstr.stellar.vault.presentation.dagger.module.mnemonics.MnemonicsModule
import com.lobstr.stellar.vault.presentation.dagger.scope.MnemonicsScope
import dagger.Subcomponent


@Subcomponent(modules = [MnemonicsModule::class])
@MnemonicsScope
interface MnemonicsComponent {
    fun inject(presenter: MnemonicsPresenter)
}