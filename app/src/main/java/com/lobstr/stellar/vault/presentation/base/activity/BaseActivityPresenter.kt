package com.lobstr.stellar.vault.presentation.base.activity

import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.domain.base.BaseInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Update
import com.lobstr.stellar.vault.domain.util.event.Update.Type.AUTH_EVENT_SUCCESS
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.app_version.AppVersionLoader
import com.lobstr.stellar.vault.presentation.app_version.AppVersionLoader.State.REQUIRED
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.Social.STORE_URL
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class BaseActivityPresenter @Inject constructor(
    private val interactor: BaseInteractor,
    private val eventProviderModule: EventProviderModule,
    private val appVersionLoader: AppVersionLoader
) : BasePresenter<BaseActivityView>() {

    var userAccount: String? = null
    private var authInProcess = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        userAccount?.apply {
            interactor.changePublicKeyInfo(this)
        }

        registerEventProvider()
    }

    override fun attachView(view: BaseActivityView?) {
        super.attachView(view)
        // Called for determine target activity check.
        viewState.checkAppVersionBehavior()
    }

    private fun registerEventProvider() {
        unsubscribeOnDestroy(
            eventProviderModule.authEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (attachedViews.size == 0) return@subscribe
                    // Try auth.
                    authViaTangem()
                }, {
                    it.printStackTrace()
                })
        )

        unsubscribeOnDestroy(
            eventProviderModule.updateEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        AUTH_EVENT_SUCCESS -> viewState.showTangemScreen(false) // Dismiss Tangem Auth dialog.
                    }
                }, {
                    it.printStackTrace()
                })
        )

        unsubscribeOnDestroy(
            eventProviderModule.appVersionUpdateSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.checkAppVersionBehavior() // Called for determine target activity check.
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun setActionBarTitle(title: String?) {
        viewState.setActionBarTitle(title)
    }

    fun changeHomeBtnVisibility(visible: Boolean) {
        viewState.changeActionBarIconVisibility(visible)
    }

    /**
     * Try authenticate via Tangem after [com.lobstr.stellar.vault.domain.util.event.Auth] event
     * received from [com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException]
     * with [com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException.Action.AUTH_REQUIRED] action.
     */
    private fun authViaTangem() {
        if (authInProcess) {
            return
        }
        unsubscribeOnDestroy(
            interactor.getChallenge()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    authInProcess = true
                }
                .doOnEvent { _, _ ->
                    authInProcess = false
                }
                .subscribe({
                    viewState.showTangemScreen(
                        show = true,
                        tangemInfo = TangemInfo().apply {
                            accountId = interactor.getUserPublicKey()
                            cardId = interactor.getTangemCardId()
                            pendingTransaction = it
                            message =
                                AppUtil.getString(R.string.text_tv_tangem_dialog_tittle_session_expired)
                            description =
                                AppUtil.getString(R.string.text_tv_tangem_dialog_description_session_expired)
                        }
                    )
                }, {
                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showMessage(it.details)
                            handleNoInternetConnection()
                        }
                        is DefaultException -> {
                            viewState.showMessage(it.details)
                        }
                        else -> {
                            viewState.showMessage(it.message ?: "")
                        }
                    }
                })
        )
    }

    fun handleTangemInfo(tangemInfo: TangemInfo?) {
        if (tangemInfo != null) {
            unsubscribeOnDestroy(
                interactor.authorizeVault(tangemInfo.signedTransaction!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        viewState.showProgressDialog(true)
                    }
                    .doOnEvent { _, _ ->
                        viewState.showProgressDialog(false)
                    }
                    .subscribe({
                        // Notify about update content event.
                        eventProviderModule.updateEventSubject.onNext(Update(AUTH_EVENT_SUCCESS))
                    }, {
                        when (it) {
                            is DefaultException -> {
                                viewState.showMessage(it.details)
                            }
                            else -> {
                                viewState.showMessage(it.message ?: "")
                            }
                        }
                    })
            )
        }
    }

    fun checkPinAppearance() {
        if (attachedViews.size == 0) return

        if (interactor.hasEncryptedPin()) {
            viewState.proceedPinActivityAppearance()
        } else {
            LVApplication.checkPinAppearance = false
        }
    }

    /**
     * Called from activity for exclude some screens for check.
     */
    fun startCheckAppVersion() {
        checkAppVersion()
    }

    /**
     * Main method for check app version. Only for attached state and etc.
     */
    private fun checkAppVersion() {
        if (attachedViews.size == 0) return

        val appVersionInfo = appVersionLoader.appVersion
        // Check version in cases when app info object exist and 'show dialog state' don't required.
        if (appVersionInfo != null && !appVersionLoader.isShowDialogState) {
            unsubscribeOnDestroy(
                appVersionLoader.checkAppVersionFlow(appVersionInfo)
                    .subscribe({
                        if (it || appVersionLoader.isShowDialogState) {
                            viewState.showAppUpdateDialog(
                                true,
                                AppUtil.getString(if (appVersionLoader.appUpdateState == REQUIRED) R.string.text_app_update_title_required else R.string.text_app_update_title_weak),
                                AppUtil.getString(if (appVersionLoader.appUpdateState == REQUIRED) R.string.text_app_update_message_required else R.string.text_app_update_message_weak),
                                AppUtil.getString(R.string.btn_text_app_update),
                                if (appVersionLoader.appUpdateState != REQUIRED) AppUtil.getString(R.string.btn_text_app_update_skip) else null
                            )
                        }
                    }, {
                        viewState.showAppUpdateDialog(false)
                    })
            )
        } else {
            // Additional check required flag for dialog.
            if (appVersionLoader.isShowDialogState) {
                viewState.showAppUpdateDialog(
                    true,
                    AppUtil.getString(if (appVersionLoader.appUpdateState == REQUIRED) R.string.text_app_update_title_required else R.string.text_app_update_title_weak),
                    AppUtil.getString(if (appVersionLoader.appUpdateState == REQUIRED) R.string.text_app_update_message_required else R.string.text_app_update_message_weak),
                    AppUtil.getString(R.string.btn_text_app_update),
                    if (appVersionLoader.appUpdateState != REQUIRED) AppUtil.getString(R.string.btn_text_app_update_skip) else null
                )
            } else {
                viewState.showAppUpdateDialog(false)
            }
        }
    }

    fun updateAppClicked() {
        viewState.showStore(STORE_URL.plus(BuildConfig.APPLICATION_ID))

        if (appVersionLoader.appUpdateState != REQUIRED) {
            // Reset data and hide dialog.
            appVersionLoader.resetAppVersionInfo()
            viewState.showAppUpdateDialog(false)
        }
        appVersionLoader.isShowDialogState = false
    }

    fun skipAppUpdateClicked() {
        // Reset data.
        appVersionLoader.resetAppVersionInfo()
        appVersionLoader.isShowDialogState = false
    }

    fun onNewIntentCalled(userAccount: String?) {
        if (!userAccount.isNullOrEmpty() && interactor.getUserPublicKey() != userAccount) {
            interactor.changePublicKeyInfo(userAccount)
            viewState.reloadAccountData()
        }
    }
}