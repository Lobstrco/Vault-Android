package com.lobstr.stellar.vault.presentation.dagger.module

import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.data.net.AccountApi
import com.lobstr.stellar.vault.data.net.FcmApi
import com.lobstr.stellar.vault.data.net.TransactionApi
import com.lobstr.stellar.vault.data.net.VaultAuthApi
import com.lobstr.stellar.vault.presentation.util.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.stellar.sdk.Network
import org.stellar.sdk.Server
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApiModule {

    private const val BASE_STAGING_URL = "https://vault-staging.lobstr.co/api/"
    private const val BASE_PRODUCTION_URL = "https://vault.lobstr.co/api/"

    // Horizon server.
    private const val HOST_HORIZON_PRODUCTION = "https://horizon.stellar.lobstr.co/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(1, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES)

        if (BuildConfig.BUILD_TYPE == Constant.BuildType.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logging)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRestAdapter(okHttpClient: OkHttpClient): Retrofit {
        val builder = Retrofit.Builder()

        builder.client(okHttpClient)
            .baseUrl(if (BuildConfig.BUILD_TYPE == Constant.BuildType.RELEASE) BASE_PRODUCTION_URL else BASE_STAGING_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideFcmApi(restAdapter: Retrofit): FcmApi {
        return restAdapter.create(FcmApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHorizonNetwork(): Network {
        return Network.PUBLIC
    }

    @Provides
    @Singleton
    fun provideHorizonServer(): Server {
        return Server(HOST_HORIZON_PRODUCTION)
    }

    @Provides
    @Singleton
    fun provideVaultAuthApi(restAdapter: Retrofit): VaultAuthApi {
        return restAdapter.create(VaultAuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTransactionApi(restAdapter: Retrofit): TransactionApi {
        return restAdapter.create(TransactionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAccountApi(restAdapter: Retrofit): AccountApi {
        return restAdapter.create(AccountApi::class.java)
    }
}