package com.lobstr.stellar.vault.presentation.dagger.module.biometric

import com.lobstr.stellar.vault.domain.biometric.BiometricSetUpInteractor
import com.lobstr.stellar.vault.domain.biometric.BiometricSetUpInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.AuthScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides


@Module
class BiometricSetUpModule {
    @Provides
    @AuthScope
    internal fun provideBiometricSetUpInteractor(
        prefsUtil: PrefsUtil
    ): BiometricSetUpInteractor {
        return BiometricSetUpInteractorImpl(prefsUtil)
    }
}