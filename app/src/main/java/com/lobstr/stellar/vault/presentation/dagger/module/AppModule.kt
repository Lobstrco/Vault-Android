package com.lobstr.stellar.vault.presentation.dagger.module

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.lobstr.stellar.tsmapper.data.claim.ClaimantMapper
import com.lobstr.stellar.tsmapper.data.transaction.TsMapper
import com.lobstr.stellar.tsmapper.data.transaction.result.TsResultMapper
import com.lobstr.stellar.vault.data.error.ExceptionMapper
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.presentation.util.FileStreamUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.stellar.sdk.AccountConverter
import org.stellar.sdk.Network
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext context: Context): PrefsUtil {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return PrefsUtil(prefs)
    }

    @Provides
    @Singleton
    fun provideFileStreamUtil(@ApplicationContext context: Context): FileStreamUtil {
        return FileStreamUtil(context)
    }

    @Provides
    @Singleton
    fun provideExceptionMapper(@ApplicationContext context: Context): ExceptionMapper {
        return ExceptionMapper(context)
    }

    @Provides
    @Singleton
    fun provideTsMapper(network: Network, accountConverter: AccountConverter): TsMapper {
        return TsMapper(network, accountConverter, ClaimantMapper())
    }

    @Provides
    @Singleton
    fun provideTsResultMapper(@ApplicationContext context: Context): TsResultMapper {
        return TsResultMapper(context)
    }

    @Provides
    @Singleton
    fun provideEventProviderModule(): EventProviderModule {
        return EventProviderModule()
    }
}