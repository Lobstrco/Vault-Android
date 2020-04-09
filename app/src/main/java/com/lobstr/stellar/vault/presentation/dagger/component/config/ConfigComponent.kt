package com.lobstr.stellar.vault.presentation.dagger.component.config

import com.lobstr.stellar.vault.presentation.dagger.module.config.ConfigModule
import com.lobstr.stellar.vault.presentation.dagger.scope.ConfigScope
import com.lobstr.stellar.vault.presentation.home.settings.config.ConfigPresenter
import dagger.Subcomponent

@Subcomponent(modules = [ConfigModule::class])
@ConfigScope
interface ConfigComponent {
    fun inject(presenter: ConfigPresenter)
}