package com.lobstr.stellar.vault.presentation.dagger.module.import_xdr

import com.lobstr.stellar.vault.domain.import_xdr.ImportXdrInteractor
import com.lobstr.stellar.vault.domain.import_xdr.ImportXdrInteractorImpl
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import dagger.Module
import dagger.Provides

@Module
class ImportXdrModule {
    @Provides
    @HomeScope
    fun provideImportXdrModuleInteractor(
        stellarRepository: StellarRepository
    ): ImportXdrInteractor {
        return ImportXdrInteractorImpl(stellarRepository)
    }
}