package com.lobstr.stellar.vault.presentation.dager.module

import android.content.Context
import com.lobstr.stellar.vault.data.account.AccountRepositoryImpl
import com.lobstr.stellar.vault.data.fcm.FcmEntityMapper
import com.lobstr.stellar.vault.data.fcm.FcmRepositoryImpl
import com.lobstr.stellar.vault.data.key_store.KeyStoreRepositoryImpl
import com.lobstr.stellar.vault.data.net.AccountApi
import com.lobstr.stellar.vault.data.net.FcmApi
import com.lobstr.stellar.vault.data.net.TransactionApi
import com.lobstr.stellar.vault.data.net.VaultAuthApi
import com.lobstr.stellar.vault.data.stellar.StellarRepositoryImpl
import com.lobstr.stellar.vault.data.transaction.TransactionEntityMapper
import com.lobstr.stellar.vault.data.transaction.TransactionRepositoryImpl
import com.lobstr.stellar.vault.data.vault_auth.VaultAuthRepositoryImpl
import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.fcm.FcmRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import dagger.Module
import dagger.Provides
import org.stellar.sdk.Server
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideStellarRepository(context: Context, server: Server): StellarRepository {
        return StellarRepositoryImpl(context, server)
    }

    @Singleton
    @Provides
    fun provideKeyStoreRepository(context: Context): KeyStoreRepository {
        return KeyStoreRepositoryImpl(context)
    }

    @Singleton
    @Provides
    fun provideFcmRepository(fcmApi: FcmApi): FcmRepository {
        return FcmRepositoryImpl(fcmApi, FcmEntityMapper())
    }

    @Singleton
    @Provides
    fun provideVaultAuthRepository(vaultAuthApi: VaultAuthApi): VaultAuthRepository {
        return VaultAuthRepositoryImpl(vaultAuthApi)
    }

    @Singleton
    @Provides
    fun provideTransactionRepository(transactionApi: TransactionApi): TransactionRepository {
        return TransactionRepositoryImpl(transactionApi, TransactionEntityMapper())
    }

    @Singleton
    @Provides
    fun provideAccountRepository(accountApi: AccountApi): AccountRepository {
        return AccountRepositoryImpl(accountApi)
    }
}