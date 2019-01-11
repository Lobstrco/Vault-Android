package com.lobstr.stellar.vault.presentation.dagger.module

import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.data.net.AccountApi
import com.lobstr.stellar.vault.data.net.FcmApi
import com.lobstr.stellar.vault.data.net.TransactionApi
import com.lobstr.stellar.vault.data.net.VaultAuthApi
import com.lobstr.stellar.vault.presentation.util.Constant
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.stellar.sdk.Network
import org.stellar.sdk.Server
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ApiModule {

    private val BASE_STAGING_URL = "https://vault-staging.lobstr.co/api/"
    private val BASE_PRODUCTION_URL = "https://vault.lobstr.co/api/"

    // Horizon server
    private val HOST_HORIZON_PRODUCTION = "https://horizon.stellar.org/"

    @Provides
    @Singleton
    internal fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(1, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES)

        if (BuildConfig.BUILD_TYPE == Constant.BuildType.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(logging)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    internal fun provideRestAdapter(okHttpClient: OkHttpClient): Retrofit {
        val builder = Retrofit.Builder()

        builder.client(okHttpClient)
            .baseUrl(if (BuildConfig.BUILD_TYPE == Constant.BuildType.RELEASE) BASE_PRODUCTION_URL else BASE_STAGING_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

        return builder.build()
    }

    @Provides
    @Singleton
    internal fun provideFcmApi(restAdapter: Retrofit): FcmApi {
        return restAdapter.create<FcmApi>(FcmApi::class.java)
    }

    @Provides
    @Singleton
    internal fun provideHorizonServer(): Server {
        Network.usePublicNetwork()
        return Server(HOST_HORIZON_PRODUCTION)
    }

    @Provides
    @Singleton
    internal fun provideVaultAuthApi(restAdapter: Retrofit): VaultAuthApi {
        return restAdapter.create<VaultAuthApi>(VaultAuthApi::class.java)
    }

    @Provides
    @Singleton
    internal fun provideTransactionApi(restAdapter: Retrofit): TransactionApi {
        return restAdapter.create<TransactionApi>(TransactionApi::class.java)
    }

    @Provides
    @Singleton
    internal fun provideAccountApi(restAdapter: Retrofit): AccountApi {
        return restAdapter.create<AccountApi>(AccountApi::class.java)
    }
}