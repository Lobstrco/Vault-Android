package com.lobstr.stellar.vault.presentation.dagger.module.import_xdr

import com.lobstr.stellar.vault.domain.import_xdr.ImportXdrInteractor
import com.lobstr.stellar.vault.domain.import_xdr.ImportXdrInteractorImpl
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class ImportXdrModule {
    @Provides
    @HomeScope
    fun provideImportXdrModuleInteractor(
        transactionRepository: TransactionRepository,
        stellarRepository: StellarRepository,
        keyStoreRepository: KeyStoreRepository,
        prefsUtil: PrefsUtil
    ): ImportXdrInteractor {
        return ImportXdrInteractorImpl(transactionRepository, stellarRepository, keyStoreRepository, prefsUtil)
    }
}