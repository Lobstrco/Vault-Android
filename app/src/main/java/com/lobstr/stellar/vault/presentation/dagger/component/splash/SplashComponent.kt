package com.lobstr.stellar.vault.presentation.dagger.component.splash

import com.lobstr.stellar.vault.presentation.dagger.module.splash.SplashModule
import com.lobstr.stellar.vault.presentation.dagger.scope.AuthScope
import com.lobstr.stellar.vault.presentation.splash.SplashPresenter
import dagger.Subcomponent

@Subcomponent(modules = [SplashModule::class])
@AuthScope
interface SplashComponent {
    fun inject(presenter: SplashPresenter)
}