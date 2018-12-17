package com.lobstr.stellar.vault.presentation.pin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import com.andrognito.pinlockview.PinLockListener
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import kotlinx.android.synthetic.main.activity_pin.*

class PinActivity : BaseMvpAppCompatActivity(), PinView, PinLockListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = PinActivity::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: PinPresenter

    private var mProgressDialog: AlertDialogFragment? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun providePinPresenter() = PinPresenter(intent?.getStringExtra(Constant.Extra.EXTRA_SECRET_KEY))

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
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun attachIndicatorDots() {
        pinLockView.attachIndicatorDots(indicatorDots)
    }

    override fun resetPin() {
        pinLockView.resetPinLockView()
    }

    override fun showHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
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

    override fun showProgressDialog() {
        mProgressDialog = ProgressManager.show(this, false)
    }

    override fun hideProgressDialog() {
        ProgressManager.dismiss(mProgressDialog)
    }

    override fun showDescriptionMessage(@StringRes message: Int) {
        pinDescription.text = getString(message)
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

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
