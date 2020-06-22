package com.lobstr.stellar.vault.presentation.home.settings

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface SettingsView : MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @AddToEndSingle
    fun setupSettingsData(
        buildVersion: String,
        isBiometricSupported: Boolean,
        isRecoveryCodeAvailable: Boolean,
        isSignerCardInfoAvailable: Boolean,
        isTransactionConfirmationAvailable: Boolean,
        isChangePinAvailable: Boolean
    )

    @AddToEndSingle
    fun setupSignersCount(
        signersCount: Int
    )

    @Skip
    fun showSuccessMessage(@StringRes message: Int)

    @Skip
    fun showAuthScreen()

    @Skip
    fun showPublicKeyDialog(publicKey: String)

    @Skip
    fun showSignersScreen()

    @Skip
    fun showMnemonicsScreen()

    @Skip
    fun showConfirmPinCodeScreen()

    @Skip
    fun showChangePinScreen()

    @Skip
    fun showHelpScreen(userId: String?)

    @AddToEndSingle
    fun setBiometricChecked(checked: Boolean)

    @AddToEndSingle
    fun setSpamProtection(config: String?)

    @AddToEndSingle
    fun setNotificationsChecked(checked: Boolean)

    @AddToEndSingle
    fun setTrConfirmation(config: String?)

    @Skip
    fun showBiometricInfoDialog(@StringRes titleRes: Int, @StringRes messageRes: Int)

    @Skip
    fun showLicenseScreen()

    @AddToEndSingle
    fun setupPolicyYear(@StringRes id: Int)

    @Skip
    fun showLogOutDialog(title: String?, message: String?)

    @Skip
    fun showWebPage(storeUrl: String)

    @Skip
    fun sendMail(mail: String, subject: String, body: String? = null)

    @Skip
    fun showConfigScreen(config: Int)

    @Skip
    fun showMessage(message: String)
}