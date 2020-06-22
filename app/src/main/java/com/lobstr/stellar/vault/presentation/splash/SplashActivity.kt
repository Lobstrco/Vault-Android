package com.lobstr.stellar.vault.presentation.splash

import android.content.DialogInterface
import android.content.Intent
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatActivity
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.PinMode.ENTER
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class SplashActivity : BaseMvpAppCompatActivity(), SplashView,
    AlertDialogFragment.OnDefaultAlertDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = SplashActivity::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: SplashPresenter

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideSplashPresenter() = SplashPresenter()

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun showAuthScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showPinScreen() {
        startActivity(Intent(this, PinActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Constant.Extra.EXTRA_PIN_MODE, ENTER)
        })
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

    override fun showFlavorDialog(flavor: String) {
        AlertDialogFragment.Builder(false)
            .setCancelable(false)
            .setMessage(String.format(getString(R.string.msg_flavor_dialog), flavor))
            .setPositiveBtnText(R.string.text_btn_ok)
            .create()
            .show(
                supportFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.INFO
            )
    }

    override fun onPositiveBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPresenter.onAlertDialogPositiveButtonClicked(tag)
    }

    override fun onNegativeBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun onNeutralBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun onCancel(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
