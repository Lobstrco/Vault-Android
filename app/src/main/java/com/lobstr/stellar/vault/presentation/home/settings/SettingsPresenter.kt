package com.lobstr.stellar.vault.presentation.home.settings

import android.app.Activity
import android.content.Intent
import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.settings.SettingsInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.settings.SettingsModule
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Social.STORE_URL
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class SettingsPresenter : BasePresenter<SettingsView>() {

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    @Inject
    lateinit var interactor: SettingsInteractor

    private var loadSignedAccountsInProcess = false

    private var loadAccountConfigInProcess = false

    private var updateAccountConfigInProcess = false

    init {
        LVApplication.appComponent.plusSettingsComponent(SettingsModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupToolbarTitle(R.string.title_toolbar_settings)
        registerEventProvider()
        viewState.setupSettingsData(
            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            BiometricUtils.isBiometricSupported(AppUtil.getAppContext())
        )
        viewState.setBiometricChecked(
            interactor.isBiometricEnabled()
                    && BiometricUtils.isBiometricAvailable(AppUtil.getAppContext())
        )
        getAccountConfig()
        viewState.setSpamProtectionChecked(!interactor.isSpamProtectionEnabled())
        viewState.setNotificationsChecked(interactor.isNotificationsEnabled())
        viewState.setTrConfirmationChecked(interactor.isTrConfirmationEnabled())
        viewState.setupPolicyYear(R.string.text_all_rights_reserved)
    }

    private fun registerEventProvider() {
        unsubscribeOnDestroy(
            eventProviderModule.networkEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Network.Type.CONNECTED -> {
                            if (needCheckConnectionState) {
                                getAccountConfig()
                            }
                            cancelNetworkWorker(false)
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )

        unsubscribeOnDestroy(
            eventProviderModule.notificationEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Notification.Type.SIGNED_NEW_ACCOUNT,
                        Notification.Type.REMOVED_SIGNER -> {
                            viewState.setupSignersCount(interactor.getSignersCount())
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    override fun attachView(view: SettingsView?) {
        super.attachView(view)
        // Always check signers count.
        viewState.setupSignersCount(interactor.getSignersCount())
    }

    fun handleOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constant.Code.CHANGE_PIN -> viewState.showSuccessMessage(R.string.text_success_change_pin)
                Constant.Code.CONFIRM_PIN_FOR_MNEMONIC -> viewState.showMnemonicsScreen()
            }
        }
    }

    fun logOutClicked() {
        viewState.showLogOutDialog()
    }

    fun signersClicked() {
        viewState.showSignersScreen()
    }

    fun mnemonicsClicked() {
        viewState.showConfirmPinCodeScreen()
    }

    fun changePinClicked() {
        viewState.showChangePinScreen()
    }

    fun helpClicked() {
        viewState.showHelpScreen()
    }

    fun biometricSwitched(checked: Boolean) {
        when {
            checked -> {
                if (BiometricUtils.isBiometricAvailable(AppUtil.getAppContext())) {
                    // Add additional logic if needed
                    interactor.setBiometricEnabled(true)
                    viewState.setBiometricChecked(true)
                } else {
                    interactor.setBiometricEnabled(false)
                    viewState.setBiometricChecked(false)
                    viewState.showBiometricInfoDialog(
                        R.string.title_biometric_not_set_up_dialog,
                        R.string.msg_biometric_not_set_up_dialog
                    )
                }
            }
            else -> {
                interactor.setBiometricEnabled(false)
            }
        }
    }

    fun spamProtectionSwitched(checked: Boolean) {
        updateAccountConfig(!checked)
    }

    fun notificationsSwitched(checked: Boolean) {
        interactor.setNotificationsEnabled(checked)
    }

    fun trConfirmationSwitched(checked: Boolean) {
        interactor.setTrConfirmationEnabled(checked)
    }

    fun publicKeyClicked() {
        viewState.showPublicKeyDialog(interactor.getUserPublicKey()!!)
    }

    fun licenseClicked() {
        viewState.showLicenseScreen()
    }

    fun rateUsClicked() {
        viewState.showStore(STORE_URL.plus(BuildConfig.APPLICATION_ID))
    }

    fun onAlertDialogPositiveButtonClicked(tag: String?) {
        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.LOG_OUT -> {
                interactor.clearUserData()
                viewState.showAuthScreen()
            }
        }
    }

    fun userVisibleHintCalled(visible: Boolean) {
        if (visible) {
            if (loadSignedAccountsInProcess) {
                return
            }
            unsubscribeOnDestroy(
                interactor.getSignedAccounts()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { loadSignedAccountsInProcess = true }
                    .doOnEvent { _, _ -> loadSignedAccountsInProcess = false }
                    .subscribe({
                        viewState.setupSignersCount(it.size)
                    }, {})
            )

            getAccountConfig()
        }
    }

    private fun getAccountConfig() {
        if (loadAccountConfigInProcess) {
            return
        }

        unsubscribeOnDestroy(
            interactor.getAccountConfig()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loadAccountConfigInProcess = true }
                .doOnEvent { _, _ -> loadAccountConfigInProcess = false }
                .subscribe({
                    interactor.setSpamProtectionEnabled(it.spamProtectionEnabled)
                    viewState.setSpamProtectionChecked(!it.spamProtectionEnabled)
                }, {
                    viewState.setSpamProtectionChecked(!interactor.isSpamProtectionEnabled())
                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showErrorMessage(it.details)
                            handleNoInternetConnection()
                        }
                        is UserNotAuthorizedException -> {
                            getAccountConfig()
                        }
                        is DefaultException -> {
                            viewState.showErrorMessage(it.details)
                        }
                        else -> {
                            viewState.showErrorMessage(it.message ?: "")
                        }
                    }
                })
        )
    }

    private fun updateAccountConfig(spamProtectionEnabled: Boolean) {
        if (updateAccountConfigInProcess) {
            return
        }

        unsubscribeOnDestroy(
            interactor.updatedAccountConfig(spamProtectionEnabled)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    updateAccountConfigInProcess = true
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _, _ ->
                    viewState.showProgressDialog(false)
                    updateAccountConfigInProcess = false
                }
                .subscribe({
                    interactor.setSpamProtectionEnabled(it.spamProtectionEnabled)
                    viewState.setSpamProtectionChecked(!it.spamProtectionEnabled)
                }, {
                    viewState.setSpamProtectionChecked(!interactor.isSpamProtectionEnabled())
                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showErrorMessage(it.details)
                        }
                        is UserNotAuthorizedException -> {
                            updateAccountConfig(spamProtectionEnabled)
                        }
                        is DefaultException -> {
                            viewState.showErrorMessage(it.details)
                        }
                        else -> {
                            viewState.showErrorMessage(it.message ?: "")
                        }
                    }
                })
        )
    }
}