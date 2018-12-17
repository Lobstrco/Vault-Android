package com.lobstr.stellar.vault.presentation.dager.component.pin

import com.lobstr.stellar.vault.presentation.dager.module.pin.PinModule
import com.lobstr.stellar.vault.presentation.dager.scope.PinScope
import com.lobstr.stellar.vault.presentation.pin.PinPresenter
import dagger.Subcomponent

@Subcomponent(modules = [PinModule::class])
@PinScope
interface PinComponent {
    fun inject(presenter: PinPresenter)
}