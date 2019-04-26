package com.lobstr.stellar.vault.presentation.home.settings

import android.app.Activity
import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.settings.SettingsInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.settings.SettingsModule
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class SettingsPresenter : BasePresenter<SettingsView>() {

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    @Inject
    lateinit var interactor: SettingsInteractor

    init {
        LVApplication.sAppComponent.plusSettingsComponent(SettingsModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupToolbarTitle(R.string.title_toolbar_settings)
        registerEventProvider()
        viewState.setupSettingsData(
            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            BiometricUtils.isBiometricSupported(LVApplication.sAppComponent.context)
        )
        viewState.setTouchIdChecked(
            interactor.isTouchIdEnabled()
                    && BiometricUtils.isFingerprintAvailable(LVApplication.sAppComponent.context)
        )
        viewState.setNotificationsChecked(interactor.isNotificationsEnabled())
        viewState.setTrConfirmationChecked(interactor.isTrConfirmationEnabled())
        viewState.setupPolicyYear(R.string.text_all_rights_reserved)
    }

    private fun registerEventProvider() {
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
        // always check signers count
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

    fun touchIdSwitched(checked: Boolean) {
        when {
            checked -> {
                if (BiometricUtils.isFingerprintAvailable(LVApplication.sAppComponent.context)) {
                    // Add additional logic if needed
                    interactor.setTouchIdEnabled(true)
                    viewState.setTouchIdChecked(true)
                } else {
                    interactor.setTouchIdEnabled(false)
                    viewState.setTouchIdChecked(false)
                    viewState.showFingerprintInfoDialog(
                        R.string.title_finger_print_dialog,
                        R.string.msg_finger_print_dialog
                    )
                }
            }
            else -> {
                interactor.setTouchIdEnabled(false)
            }
        }
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
        viewState.showStore(Constant.Social.STORE_URL)
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
            unsubscribeOnDestroy(
                interactor.getSignedAccounts()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        viewState.setupSignersCount(it.size)
                    }, {})
            )
        }
    }
}