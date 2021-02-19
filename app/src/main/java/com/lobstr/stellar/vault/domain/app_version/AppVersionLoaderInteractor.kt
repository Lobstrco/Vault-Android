package com.lobstr.stellar.vault.domain.app_version

import com.lobstr.stellar.vault.presentation.entities.account.AppVersion
import io.reactivex.rxjava3.core.Single


interface AppVersionLoaderInteractor {

    fun getAppVersion(): Single<AppVersion>

    fun setAppUpdatedCounterTimer(count: Int)

    fun getAppUpdatedCounterTimer(): Int

    fun setAppUpdateRecommendedState(enabled: Boolean)

    fun getAppUpdateRecommendedState(): Boolean

    fun isUserAuthorized(): Boolean
}