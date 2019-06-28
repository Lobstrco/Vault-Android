package com.lobstr.stellar.vault.presentation.dagger.module

import android.content.Context
import com.lobstr.stellar.vault.data.account.AccountEntityMapper
import com.lobstr.stellar.vault.data.account.AccountRepositoryImpl
import com.lobstr.stellar.vault.data.error.ExceptionMapper
import com.lobstr.stellar.vault.data.error.RxErrorUtilsImpl
import com.lobstr.stellar.vault.data.fcm.FcmEntityMapper
import com.lobstr.stellar.vault.data.fcm.FcmRepositoryImpl
import com.lobstr.stellar.vault.data.key_store.KeyStoreRepositoryImpl
import com.lobstr.stellar.vault.data.mnemonic.MnemonicsMapper
import com.lobstr.stellar.vault.data.net.AccountApi
import com.lobstr.stellar.vault.data.net.FcmApi
import com.lobstr.stellar.vault.data.net.TransactionApi
import com.lobstr.stellar.vault.data.net.VaultAuthApi
import com.lobstr.stellar.vault.data.stellar.StellarRepositoryImpl
import com.lobstr.stellar.vault.data.transaction.TransactionEntityMapper
import com.lobstr.stellar.vault.data.transaction.TransactionRepositoryImpl
import com.lobstr.stellar.vault.data.vault_auth.VaultAuthRepositoryImpl
import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.error.RxErrorUtils
import com.lobstr.stellar.vault.domain.fcm.FcmRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import org.stellar.sdk.Network
import org.stellar.sdk.Server
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideStellarRepository(context: Context, network: Network, server: Server): StellarRepository {
        return StellarRepositoryImpl(context, network, server, MnemonicsMapper(), TransactionEntityMapper(network))
    }

    @Singleton
    @Provides
    fun provideKeyStoreRepository(context: Context, prefsUtil: PrefsUtil): KeyStoreRepository {
        return KeyStoreRepositoryImpl(context, prefsUtil, MnemonicsMapper())
    }

    @Singleton
    @Provides
    fun provideRxErrorRepository(
        exceptionMapper: ExceptionMapper,
        apiVaultAuthApi: VaultAuthApi,
        stellarRepository: StellarRepository,
        keyStoreRepository: KeyStoreRepository,
        prefsUtil: PrefsUtil
    ): RxErrorUtils {
        return RxErrorUtilsImpl(exceptionMapper, apiVaultAuthApi, stellarRepository, keyStoreRepository, prefsUtil)
    }

    @Singleton
    @Provides
    fun provideFcmRepository(fcmApi: FcmApi, network: Network, rxErrorUtils: RxErrorUtils): FcmRepository {
        return FcmRepositoryImpl(
            fcmApi,
            FcmEntityMapper(),
            TransactionEntityMapper(network),
            AccountEntityMapper(),
            rxErrorUtils
        )
    }

    @Singleton
    @Provides
    fun provideVaultAuthRepository(
        vaultAuthApi: VaultAuthApi,
        rxErrorUtils: RxErrorUtils
    ): VaultAuthRepository {
        return VaultAuthRepositoryImpl(vaultAuthApi, rxErrorUtils)
    }

    @Singleton
    @Provides
    fun provideTransactionRepository(
        transactionApi: TransactionApi,
        network: Network,
        rxErrorUtils: RxErrorUtils
    ): TransactionRepository {
        return TransactionRepositoryImpl(transactionApi, TransactionEntityMapper(network), rxErrorUtils)
    }

    @Singleton
    @Provides
    fun provideAccountRepository(accountApi: AccountApi, rxErrorUtils: RxErrorUtils): AccountRepository {
        return AccountRepositoryImpl(accountApi, AccountEntityMapper(), rxErrorUtils)
    }
}