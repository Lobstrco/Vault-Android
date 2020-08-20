package com.lobstr.stellar.vault.presentation.dagger.module.import_xdr

import com.lobstr.stellar.vault.domain.import_xdr.ImportXdrInteractor
import com.lobstr.stellar.vault.domain.import_xdr.ImportXdrInteractorImpl
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.home.transactions.import_xdr.ImportXdrPresenter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object ImportXdrModule {

    @Provides
    fun provideImportXdrPresenter(
        importXdrInteractor: ImportXdrInteractor
    ): ImportXdrPresenter {
        return ImportXdrPresenter(importXdrInteractor)
    }

    @Provides
    fun provideImportXdrModuleInteractor(
        stellarRepository: StellarRepository
    ): ImportXdrInteractor {
        return ImportXdrInteractorImpl(stellarRepository)
    }
}