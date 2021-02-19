package com.lobstr.stellar.vault.presentation.app_version

import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.domain.app_version.AppVersionLoaderInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.presentation.app_version.AppVersionLoader.State.COUNTER
import com.lobstr.stellar.vault.presentation.app_version.AppVersionLoader.State.RECOMMENDED
import com.lobstr.stellar.vault.presentation.app_version.AppVersionLoader.State.REQUIRED
import com.lobstr.stellar.vault.presentation.app_version.AppVersionLoader.State.UNSPECIFIED
import com.lobstr.stellar.vault.presentation.entities.account.AppVersion
import com.lobstr.stellar.vault.presentation.util.AppVersionUtil
import com.lobstr.stellar.vault.presentation.util.Constant.Counter.APP_UPDATE
import com.lobstr.stellar.vault.presentation.util.Constant.Counter.START
import com.lobstr.stellar.vault.presentation.util.manager.network.NetworkWorker
import com.lobstr.stellar.vault.presentation.util.manager.network.WorkerManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*


open class AppVersionLoader(
    private val interactor: AppVersionLoaderInteractor,
    private val eventProviderModule: EventProviderModule
) {

    /**
     * State of APP UPDATE.
     */
    object State {
        const val UNSPECIFIED: Byte = 0
        const val RECOMMENDED: Byte = 1
        const val COUNTER: Byte = 2
        const val REQUIRED: Byte = 3
    }

    // App Version Info holder. Reset it when don't needed.
    var appVersion: AppVersion? = null

    var appUpdateState: Byte = UNSPECIFIED

    // State for check required action of flow. If true - need show dialog in all cases. False - ignore it action
    // Change flag state to false after dialog actions like skip/update.
    // Used for restore dialog after Skip screens and etc.
    var isShowDialogState: Boolean = false

    init {
        registerEventProvider()
        checkAppUpdateTimer()
        getAppVersion()
    }

    private var networkWorkerId: UUID? = null
    private var needCheckConnectionState: Boolean = false

    private fun registerEventProvider() {
        eventProviderModule.networkEventSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it.type) {
                    Network.Type.CONNECTED -> {
                        if (needCheckConnectionState) {
                            getAppVersion()
                        }
                        cancelNetworkWorker(false)
                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun checkAppUpdateTimer() {
        /** This counter is needed to count the number of program starts
         * for AppUpdateDialog.*/
        var counter = interactor.getAppUpdatedCounterTimer()
        if (counter < APP_UPDATE) {
            counter++
            interactor.setAppUpdatedCounterTimer(counter)
        }
    }

    private fun getAppVersion() {
        interactor.getAppVersion()
            .doOnSuccess {
                // Check required fields and update appVersion info object.
                appVersion = when {
                    it.currentVersion.isNullOrEmpty() || it.minAppVersion.isNullOrEmpty() || it.recommended.isNullOrEmpty() -> {
                        // Case when api is disabled.
                        // Reset appVersion info for future check.
                        interactor.setAppUpdateRecommendedState(true)
                        interactor.setAppUpdatedCounterTimer(APP_UPDATE)
                        null
                    }
                    else -> {
                        it // Save appVersion info for future check.
                    }
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // Notify UI about success.
                appVersion?.let { eventProviderModule.appVersionUpdateSubject.onNext(appVersion) }
            }, {
                when (it) {
                    is NoInternetConnectionException -> handleNoInternetConnection()
                    else -> resetAppVersionInfo()
                }
            })
    }

    /**
     * Logic for check app version flow.
     * @return Single<Boolean> with flag for show app update dialog.
     */
    fun checkAppVersionFlow(appVersionInfo: AppVersion): Single<Boolean> {
        return Single.fromCallable {
            val appCurrent = BuildConfig.VERSION_NAME

            val current = appVersionInfo.currentVersion
            val recommended = appVersionInfo.recommended
            val minAppVersion = appVersionInfo.minAppVersion

            val isUserAuthorized = interactor.isUserAuthorized()

            when {
                current.isNullOrEmpty() || recommended.isNullOrEmpty() || minAppVersion.isNullOrEmpty() -> {
                    // Case when api is disabled.
                    interactor.setAppUpdateRecommendedState(true)
                    interactor.setAppUpdatedCounterTimer(APP_UPDATE)
                    resetAppVersionInfo()
                    return@fromCallable false
                }
                AppVersionUtil.compareVersions(
                    appCurrent,
                    current
                ) != -1 -> {
                    // Case for current app version is most actual. Don't show anything.
                    interactor.setAppUpdateRecommendedState(true)
                    interactor.setAppUpdatedCounterTimer(APP_UPDATE)
                    resetAppVersionInfo()
                    return@fromCallable false
                }
                AppVersionUtil.compareVersions(
                    appCurrent,
                    recommended
                ) != -1 -> {
                    // Case when app version between current and recommended - show warning dialog on first launch in signed state.
                    appUpdateState = RECOMMENDED
                    if (isUserAuthorized) {
                        if (interactor.getAppUpdateRecommendedState()) {
                            interactor.setAppUpdateRecommendedState(false)
                            interactor.setAppUpdatedCounterTimer(APP_UPDATE)
                        } else {
                            resetAppVersionInfo()
                            return@fromCallable false
                        }
                    } else {
                        return@fromCallable false
                    }
                }
                AppVersionUtil.compareVersions(
                    appCurrent,
                    recommended
                ) == -1 && AppVersionUtil.compareVersions(appCurrent, minAppVersion) != -1 -> {
                    // Case for showing warning dialog with skip action.
                    appUpdateState = COUNTER
                    if (isUserAuthorized) {
                        if (interactor.getAppUpdatedCounterTimer() == APP_UPDATE) {
                            interactor.setAppUpdatedCounterTimer(START)
                            interactor.setAppUpdateRecommendedState(true)
                        } else {
                            resetAppVersionInfo()
                            return@fromCallable false
                        }
                    } else {
                        return@fromCallable false
                    }
                }
                AppVersionUtil.compareVersions(
                    appCurrent,
                    minAppVersion
                ) == -1 -> {
                    // Case for showing required dialog.
                    appUpdateState = REQUIRED
                    interactor.setAppUpdateRecommendedState(true)
                    interactor.setAppUpdatedCounterTimer(APP_UPDATE)
                }
                else -> {
                    // Case for unhandled states.
                    interactor.setAppUpdateRecommendedState(true)
                    interactor.setAppUpdatedCounterTimer(APP_UPDATE)
                    resetAppVersionInfo()
                    return@fromCallable false
                }
            }

            return@fromCallable true

        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                // Save state for check it in future.
                if (it) isShowDialogState = it
            }
            .doOnError { resetAppVersionInfo() }

    }

    fun resetAppVersionInfo() {
        appVersion = null
    }

    /**
     * Call it after internet connection fail.
     * @param checkConnectionState Notify [needCheckConnectionState] flag about changes if needed. By default true. null - ignore it.
     */
    private fun handleNoInternetConnection(checkConnectionState: Boolean? = true) {
        // Prevent creation several NetworkWorkers.
        if (networkWorkerId != null) {
            return
        }

        needCheckConnectionState = checkConnectionState ?: needCheckConnectionState
        networkWorkerId = WorkerManager.createNetworkStateWorker(NetworkWorker::class.java)
    }

    /**
     * Cancel Network Worker.
     * @param checkConnectionState Notify [needCheckConnectionState] flag about changes if needed. By default null - ignore it.
     */
    private fun cancelNetworkWorker(checkConnectionState: Boolean? = null) {
        needCheckConnectionState = checkConnectionState ?: needCheckConnectionState
        WorkerManager.cancelWorkById(networkWorkerId)
        networkWorkerId = null
    }
}