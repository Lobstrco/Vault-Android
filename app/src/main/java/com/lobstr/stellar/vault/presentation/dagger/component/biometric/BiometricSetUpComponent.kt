package com.lobstr.stellar.vault.presentation.dagger.component.biometric

import com.lobstr.stellar.vault.presentation.auth.biometric.BiometricSetUpPresenter
import com.lobstr.stellar.vault.presentation.dagger.module.biometric.BiometricSetUpModule
import com.lobstr.stellar.vault.presentation.dagger.scope.AuthScope
import dagger.Subcomponent

@Subcomponent(modules = [BiometricSetUpModule::class])
@AuthScope
interface BiometricSetUpComponent {
    fun inject(presenter: BiometricSetUpPresenter)
}