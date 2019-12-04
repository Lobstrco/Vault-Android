package com.lobstr.stellar.vault.presentation.auth.biometric


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricListener
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricManager
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity
import kotlinx.android.synthetic.main.fragment_biometric_set_up.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class BiometricSetUpFragment : BaseFragment(), BiometricSetUpView, BiometricListener, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = BiometricSetUpFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: BiometricSetUpPresenter

    private var mView: View? = null

    private var mBiometricManager: BiometricManager? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideBiometricSetUpPresenter() = BiometricSetUpPresenter()

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = if (mView == null) inflater.inflate(R.layout.fragment_biometric_set_up, container, false) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        btnTurOn.setOnClickListener(this)
        btnSkip.setOnClickListener(this)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnTurOn -> mPresenter.turnOnClicked()
            R.id.btnSkip -> mPresenter.skipClicked()
        }
    }

    override fun showVaultAuthScreen() {
        val intent = Intent(activity, VaultAuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // Biometric.

    override fun showBiometricInfoDialog(titleRes: Int, messageRes: Int) {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(titleRes)
            .setMessage(messageRes)
            .setPositiveBtnText(R.string.text_btn_ok)
            .create()
            .show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.BIOMETRIC_INFO_DIALOG)
    }

    override fun showBiometricDialog(show: Boolean) {
        mBiometricManager?.cancelAuthentication()

        if (show) {
            mBiometricManager = BiometricManager.BiometricBuilder(context!!, this)
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.text_btn_cancel).toUpperCase())
                .build()
            mBiometricManager?.authenticate(this)
        }
    }

    override fun onBiometricAuthenticationPermissionNotGranted() {
        Toast.makeText(
            context,
            getString(R.string.biometric_error_permission_not_granted),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBiometricAuthenticationInternalError(error: String?) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationFailed() {
        // Show message if needed.
    }

    override fun onAuthenticationSuccessful() {
        mPresenter.biometricAuthenticationSuccessful()
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
