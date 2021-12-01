package com.lobstr.stellar.vault.presentation.dagger.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.stellar.sdk.AccountConverter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StellarModule {
    @Provides
    @Singleton
    fun provideAccountConverter(): AccountConverter {
        return AccountConverter.enableMuxed()
    }
}