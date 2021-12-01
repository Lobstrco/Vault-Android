package com.lobstr.stellar.vault.presentation.dagger.module

import android.content.Context
import com.lobstr.stellar.tsmapper.data.transaction.TsMapper
import com.lobstr.stellar.vault.data.account.AccountEntityMapper
import com.lobstr.stellar.vault.data.account.AccountRepositoryImpl
import com.lobstr.stellar.vault.data.error.ExceptionMapper
import com.lobstr.stellar.vault.data.error.RxErrorRepositoryImpl
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
import com.lobstr.stellar.vault.data.tangem.TangemRepositoryImpl
import com.lobstr.stellar.vault.data.transaction.TransactionEntityMapper
import com.lobstr.stellar.vault.data.transaction.TransactionRepositoryImpl
import com.lobstr.stellar.vault.data.vault_auth.VaultAuthRepositoryImpl
import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.error.RxErrorRepository
import com.lobstr.stellar.vault.domain.error.RxErrorUtils
import com.lobstr.stellar.vault.domain.fcm.FcmRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.tangem.TangemRepository
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import com.lobstr.stellar.vault.presentation.util.FileStreamUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.stellar.sdk.AccountConverter
import org.stellar.sdk.Network
import org.stellar.sdk.Server
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideStellarRepository(
        accountConverter: AccountConverter,
        network: Network,
        server: Server,
        rxErrorUtils: RxErrorUtils
    ): StellarRepository {
        return StellarRepositoryImpl(
            accountConverter,
            network,
            server,
            MnemonicsMapper(),
            rxErrorUtils
        )
    }

    @Singleton
    @Provides
    fun provideTangemRepository(): TangemRepository {
        return TangemRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideKeyStoreRepository(@ApplicationContext context: Context, prefsUtil: PrefsUtil): KeyStoreRepository {
        return KeyStoreRepositoryImpl(context, prefsUtil, MnemonicsMapper())
    }

    @Singleton
    @Provides
    fun provideRxErrorUtils(
        exceptionMapper: ExceptionMapper,
        apiVaultAuthApi: VaultAuthApi,
        rxErrorRepository: RxErrorRepository,
        keyStoreRepository: KeyStoreRepository,
        prefsUtil: PrefsUtil
    ): RxErrorUtils {
        return RxErrorUtilsImpl(
            exceptionMapper,
            apiVaultAuthApi,
            rxErrorRepository,
            keyStoreRepository,
            prefsUtil
        )
    }

    @Singleton
    @Provides
    fun provideRxErrorRepository(
        accountConverter: AccountConverter,
        network: Network
    ): RxErrorRepository {
        return RxErrorRepositoryImpl(accountConverter, network)
    }

    @Singleton
    @Provides
    fun provideFcmRepository(
        fcmApi: FcmApi,
        tsMapper: TsMapper,
        rxErrorUtils: RxErrorUtils
    ): FcmRepository {
        return FcmRepositoryImpl(
            fcmApi,
            FcmEntityMapper(),
            TransactionEntityMapper(tsMapper),
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
        tsMapper: TsMapper,
        rxErrorUtils: RxErrorUtils
    ): TransactionRepository {
        return TransactionRepositoryImpl(
            transactionApi,
            TransactionEntityMapper(tsMapper),
            rxErrorUtils
        )
    }

    @Singleton
    @Provides
    fun provideAccountRepository(
        accountApi: AccountApi,
        rxErrorUtils: RxErrorUtils,
        fileStreamUtil: FileStreamUtil
    ): AccountRepository {
        return AccountRepositoryImpl(accountApi, AccountEntityMapper(), rxErrorUtils, fileStreamUtil)
    }
}