package com.lobstr.stellar.vault.presentation.dagger.module.biometric

import com.lobstr.stellar.vault.domain.biometric.BiometricSetUpInteractor
import com.lobstr.stellar.vault.domain.biometric.BiometricSetUpInteractorImpl
import com.lobstr.stellar.vault.presentation.auth.biometric.BiometricSetUpPresenter
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent


@Module
@InstallIn(FragmentComponent::class)
object BiometricSetUpModule {

    @Provides
    fun provideBiometricSetUpPresenter(
        biometricSetUpInteractor: BiometricSetUpInteractor
    ): BiometricSetUpPresenter {
        return BiometricSetUpPresenter(biometricSetUpInteractor)
    }

    @Provides
    fun provideBiometricSetUpInteractor(
        prefsUtil: PrefsUtil
    ): BiometricSetUpInteractor {
        return BiometricSetUpInteractorImpl(prefsUtil)
    }
}