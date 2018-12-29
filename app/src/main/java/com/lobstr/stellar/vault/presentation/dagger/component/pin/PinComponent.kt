package com.lobstr.stellar.vault.presentation.dagger.component.pin

import com.lobstr.stellar.vault.presentation.dagger.module.pin.PinModule
import com.lobstr.stellar.vault.presentation.dagger.scope.PinScope
import com.lobstr.stellar.vault.presentation.pin.PinPresenter
import dagger.Subcomponent

@Subcomponent(modules = [PinModule::class])
@PinScope
interface PinComponent {
    fun inject(presenter: PinPresenter)
}