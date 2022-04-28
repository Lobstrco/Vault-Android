package com.lobstr.stellar.vault.presentation.auth.biometric


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentBiometricSetUpBinding
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricListener
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricManager
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class BiometricSetUpFragment : BaseFragment(), BiometricSetUpView, BiometricListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = BiometricSetUpFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentBiometricSetUpBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<BiometricSetUpPresenter>

    private var mBiometricManager: BiometricManager? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter {
        presenterProvider.get().apply {
            pinMode = arguments?.getByte(Constant.Bundle.BUNDLE_PIN_MODE) ?: Constant.PinMode.ENTER
        }
    }

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
        _binding = FragmentBiometricSetUpBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        binding.btnTurOn.setSafeOnClickListener { mPresenter.turnOnClicked() }
        binding.btnSkip.setSafeOnClickListener { mPresenter.skipClicked() }
    }

    override fun onBackPressed(): Boolean {
        mPresenter.onBackPressed()
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setWindowBackground() {
        (activity as? PinActivity)?.window?.decorView?.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.white
            )
        )
    }

    /**
     * Set navigation buttons color.
     * @param light when true - gray, else - white.
     */
    override fun setupNavigationBar(@ColorRes color: Int, light: Boolean) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            requireActivity().window?.let {
                it.navigationBarColor = ContextCompat.getColor(requireContext(), color)
                WindowCompat.getInsetsController(it, it.decorView)?.isAppearanceLightNavigationBars = light
            }
        }
    }

    override fun setupToolbar(
        showHomeAsUp: Boolean,
        toolbarColor: Int,
        upArrow: Int,
        upArrowColor: Int
    ) {
        (activity as? BaseActivity)?.changeActionBarIconVisibility(showHomeAsUp)
        (activity as? BaseActivity)?.setActionBarBackground(toolbarColor)
        (activity as? BaseActivity)?.setActionBarIcon(upArrow, upArrowColor)
    }

    override fun showVaultAuthScreen() {
        val intent = Intent(activity, VaultAuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun finishScreen() {
        activity?.finish()
    }

    override fun finishApp() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
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
            .show(
                childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.BIOMETRIC_INFO_DIALOG
            )
    }

    override fun showBiometricDialog() {
        mBiometricManager?.cancelAuthentication() // Cancel biometric authentication for avoiding dialog duplications.
        if (mBiometricManager == null) {
            mBiometricManager = BiometricManager.BiometricBuilder(requireContext(), this)
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.text_btn_cancel).uppercase())
                .build()
        }
        mBiometricManager?.authenticate(this)
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
