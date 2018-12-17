package com.lobstr.stellar.vault.presentation.dager.component.splash

import com.lobstr.stellar.vault.presentation.dager.module.splash.SplashModule
import com.lobstr.stellar.vault.presentation.dager.scope.AuthScope
import com.lobstr.stellar.vault.presentation.splash.SplashPresenter
import dagger.Subcomponent

@Subcomponent(modules = [SplashModule::class])
@AuthScope
interface SplashComponent {
    fun inject(presenter: SplashPresenter)
}