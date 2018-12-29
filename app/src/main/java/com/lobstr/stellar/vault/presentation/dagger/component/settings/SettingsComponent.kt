package com.lobstr.stellar.vault.presentation.dagger.component.settings

import com.lobstr.stellar.vault.presentation.dagger.module.settings.SettingsModule
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.home.settings.SettingsPresenter
import dagger.Subcomponent


@Subcomponent(modules = [SettingsModule::class])
@HomeScope
interface SettingsComponent {
    fun inject(presenter: SettingsPresenter)
}