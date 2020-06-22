package com.lobstr.stellar.vault.presentation.dagger.component.pin

import com.lobstr.stellar.vault.presentation.dagger.module.pin.PinModule
import com.lobstr.stellar.vault.presentation.dagger.scope.PinScope
import com.lobstr.stellar.vault.presentation.pin.PinPresenter
import com.lobstr.stellar.vault.presentation.pin.main.PinFrPresenter
import dagger.Subcomponent

@Subcomponent(modules = [PinModule::class])
@PinScope
interface PinComponent {
    fun inject(presenter: PinPresenter)
    fun inject(presenter: PinFrPresenter)
}