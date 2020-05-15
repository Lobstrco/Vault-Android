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
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricListener
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricManager
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity
import kotlinx.android.synthetic.main.activity_pin.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class PinActivity : BaseActivity(), PinView, PinLockListener,
    BiometricListener, View.OnClickListener, AlertDialogFragment.OnDefaultAlertDialogListener {

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
    lateinit var mPinPresenter: PinPresenter

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

    override fun getLayoutResource(): Int {
        return R.layout.activity_pin
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyleForEnterPinIfNeeded()
        super.onCreate(savedInstanceState)
        setListeners()
    }

    private fun setStyleForEnterPinIfNeeded() {
        // Set transparent and light navigation bar for dark pin theme.
        if (!intent?.getBooleanExtra(Constant.Extra.EXTRA_CREATE_PIN, false)!! &&
            !intent?.getBooleanExtra(Constant.Extra.EXTRA_CHANGE_PIN, false)!! &&
            !intent?.getBooleanExtra(Constant.Extra.EXTRA_CONFIRM_PIN, false)!!
        ) {
            setTheme(R.style.PinDarkTheme)
        }
    }

    private fun setListeners() {
        pinLockView.setPinLockListener(this)
        tvPinLogOut.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            tvPinLogOut.id -> mPinPresenter.logoutClicked()
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
            content.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary))
            pinLockView.textColor = ContextCompat.getColor(this, android.R.color.white)

            tvPinTitle.visibility = View.GONE
            ivPinLogo.visibility = View.VISIBLE
            indicatorDotsWhite.visibility = View.VISIBLE
            indicatorDots.visibility = View.GONE
            tvPinLogOut.visibility = View.VISIBLE
        } else {
            indicatorDots.pinLength = 6
            pinLockView.attachIndicatorDots(indicatorDots)
            content.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
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

    override fun showBiometricSetUpScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.BIOMETRIC_SET_UP)
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

    // Pin Lock listeners callbacks.

    override fun onComplete(pin: String?) {
        AppUtil.vibrate(this, longArrayOf(0, 8, 0, 0))
        Log.i(LOG_TAG, "Pin complete: $pin")
        mPinPresenter.onPinComplete(pin)
    }

    override fun onEmpty() {
        AppUtil.vibrate(this, longArrayOf(0, 8, 0, 0))
        Log.i(LOG_TAG, "Pin empty")
    }

    override fun onPinChange(pinLength: Int, intermediatePin: String?) {
        AppUtil.vibrate(this, longArrayOf(0, 8, 0, 0))
        Log.i(LOG_TAG, "Pin changed, new length $pinLength with intermediate pin $intermediatePin")
    }

    // Dialogs.

    override fun onPositiveBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPinPresenter.onAlertDialogPositiveButtonClicked(tag)
    }

    override fun onNegativeBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPinPresenter.onAlertDialogNegativeButtonClicked(tag)
    }

    override fun onNeutralBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun onCancel(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
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
            .show(
                supportFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.COMMON_PIN_PATTERN
            )
    }

    // Biometric.

    override fun showBiometricDialog(show: Boolean) {
        mBiometricManager?.cancelAuthentication()

        if (show) {
            mBiometricManager = BiometricManager.BiometricBuilder(this, this)
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.text_btn_cancel).toUpperCase())
                .build()
            mBiometricManager?.authenticate(this)
        }
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
        // Show message if needed.
    }

    override fun onAuthenticationSuccessful() {
        mPinPresenter.biometricAuthenticationSuccessful()
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        // Show message if needed.
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
