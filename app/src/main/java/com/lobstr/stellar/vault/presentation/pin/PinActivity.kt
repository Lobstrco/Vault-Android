package com.lobstr.stellar.vault.presentation.pin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.andrognito.pinlockview.PinLockListener
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatActivity
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricCallback
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricManager
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity
import kotlinx.android.synthetic.main.activity_pin.*

class PinActivity : BaseMvpAppCompatActivity(), PinView, PinLockListener,
    BiometricCallback, View.OnClickListener, AlertDialogFragment.OnDefaultAlertDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = PinActivity::class.simpleName
        const val STYLE_ENTER_PIN = 0
        const val STYLE_CREATE_PIN = 1
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: PinPresenter

    private var mBiometricManager: BiometricManager? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun providePinPresenter() = PinPresenter(
        intent?.getBooleanExtra(Constant.Extra.EXTRA_CREATE_PIN, false),
        intent?.getBooleanExtra(Constant.Extra.EXTRA_CHANGE_PIN, false),
        intent?.getBooleanExtra(Constant.Extra.EXTRA_CONFIRM_PIN, false)
    )

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)
        setListeners()
    }

    private fun setListeners() {
        pinLockView.setPinLockListener(this)
        tvPinLogOut.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            tvPinLogOut.id -> mPresenter.logoutClicked()
        }
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setScreenStyle(style: Int) {
        pinLockView.pinLength = 6

        if (style == STYLE_ENTER_PIN) {
            indicatorDotsWhite.pinLength = 6
            pinLockView.attachIndicatorDots(indicatorDotsWhite)
            llPinContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary))
            pinLockView.textColor = ContextCompat.getColor(this, android.R.color.white)

            tvPinTitle.visibility = View.GONE
            ivPinLogo.visibility = View.VISIBLE
            indicatorDotsWhite.visibility = View.VISIBLE
            indicatorDots.visibility = View.GONE
            tvPinLogOut.visibility = View.VISIBLE
        } else {
            indicatorDots.pinLength = 6
            pinLockView.attachIndicatorDots(indicatorDots)
            llPinContainer.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
            pinLockView.textColor = ContextCompat.getColor(this, android.R.color.black)

            tvPinTitle.visibility = View.VISIBLE
            ivPinLogo.visibility = View.GONE
            indicatorDotsWhite.visibility = View.GONE
            indicatorDots.visibility = View.VISIBLE
            tvPinLogOut.visibility = View.GONE
        }
    }

    override fun showTitle(@StringRes title: Int) {
        tvPinTitle.text = getString(title)
    }

    override fun resetPin() {
        pinLockView.resetPinLockView()
    }

    override fun showHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showVaultAuthScreen() {
        val intent = Intent(this, VaultAuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showFingerprintSetUpScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.FINGERPRINT_SET_UP)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showErrorMessage(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun finishScreenWithResult(resultCode: Int) {
        setResult(resultCode)
        finish()
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, supportFragmentManager)
    }

    override fun showAuthScreen() {
        NotificationsManager.clearNotifications(this)
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.AUTH)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // Pin Lock listeners callbacks

    override fun onComplete(pin: String?) {
        Log.i(LOG_TAG, "Pin complete: $pin")
        mPresenter.onPinComplete(pin)
    }

    override fun onEmpty() {
        Log.i(LOG_TAG, "Pin empty")
    }

    override fun onPinChange(pinLength: Int, intermediatePin: String?) {
        Log.i(LOG_TAG, "Pin changed, new length $pinLength with intermediate pin $intermediatePin")
    }

    // Dialogs

    override fun onPositiveBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPresenter.onAlertDialogPositiveButtonClicked(tag)
    }

    override fun onNegativeBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPresenter.onAlertDialogNegativeButtonClicked(tag)
    }

    override fun onNeutralBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // add logic if needed
    }

    override fun onCancel(tag: String?, dialogInterface: DialogInterface) {
        // add logic if needed
    }

    override fun showLogOutDialog() {
        AlertDialogFragment.Builder(false)
            .setCancelable(true)
            .setTitle(R.string.title_log_out_dialog)
            .setMessage(R.string.msg_log_out_dialog)
            .setNegativeBtnText(R.string.text_btn_cancel)
            .setPositiveBtnText(R.string.text_btn_log_out)
            .create()
            .show(supportFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.LOG_OUT)
    }

    override fun showCommonPinPatternDialog() {
        AlertDialogFragment.Builder(false)
            .setCancelable(false)
            .setTitle(R.string.title_common_pin_pattern_dialog)
            .setMessage(R.string.msg_common_pin_pattern_dialog)
            .setNegativeBtnText(R.string.text_btn_continue)
            .setPositiveBtnText(R.string.text_btn_change_pin)
            .create()
            .show(supportFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.COMMON_PIN_PATTERN)
    }

    // Biometric

    override fun showBiometricDialog(show: Boolean) {
        mBiometricManager?.dismissDialog()

        if (show) {
            mBiometricManager = BiometricManager.BiometricBuilder(this)
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setDescription(getString(R.string.biometric_description))
                .setNegativeButtonText(getString(R.string.text_btn_cancel).toUpperCase())
                .build()
            mBiometricManager?.authenticate(this)
        }
    }

    override fun onSdkVersionNotSupported() {
        Toast.makeText(applicationContext, getString(R.string.biometric_error_sdk_not_supported), Toast.LENGTH_LONG)
            .show()
    }

    override fun onBiometricAuthenticationNotSupported() {
        Toast.makeText(
            applicationContext,
            getString(R.string.biometric_error_hardware_not_supported),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBiometricAuthenticationNotAvailable() {
        Toast.makeText(
            applicationContext,
            getString(R.string.biometric_error_fingerprint_not_available),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBiometricAuthenticationPermissionNotGranted() {
        Toast.makeText(
            applicationContext,
            getString(R.string.biometric_error_permission_not_granted),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBiometricAuthenticationInternalError(error: String?) {
        Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationFailed() {
        // Toast.makeText(applicationContext, getString(R.string.biometric_failure), Toast.LENGTH_LONG).show();
    }

    override fun onAuthenticationCancelled() {
        // Toast.makeText(applicationContext, getString(R.string.biometric_cancelled), Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationSuccessful() {
        mPresenter.biometricAuthenticationSuccessful()
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        // Toast.makeText(applicationContext, helpString, Toast.LENGTH_LONG).show();
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        // Toast.makeText(applicationContext, errString, Toast.LENGTH_LONG).show();
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
