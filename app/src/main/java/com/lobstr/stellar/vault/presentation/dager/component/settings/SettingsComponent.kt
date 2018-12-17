package com.lobstr.stellar.vault.presentation.dager.component.settings

import com.lobstr.stellar.vault.presentation.dager.module.settings.SettingsModule
import com.lobstr.stellar.vault.presentation.dager.scope.HomeScope
import com.lobstr.stellar.vault.presentation.home.settings.SettingsPresenter
import dagger.Subcomponent


@Subcomponent(modules = [SettingsModule::class])
@HomeScope
interface SettingsComponent {
    fun inject(presenter: SettingsPresenter)
}