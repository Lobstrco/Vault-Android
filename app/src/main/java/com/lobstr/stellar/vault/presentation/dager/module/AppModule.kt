package com.lobstr.stellar.vault.presentation.dager.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule (val application: Application) {

    @Provides
    @Singleton
    internal fun provideApplicationContext(): Context {
        return application
    }

    @Provides
    @Singleton
    internal fun providePreferences(context: Context): PrefsUtil {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return PrefsUtil(prefs)
    }
}