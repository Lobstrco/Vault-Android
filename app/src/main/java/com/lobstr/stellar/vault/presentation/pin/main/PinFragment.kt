package com.lobstr.stellar.vault.presentation.pin.main

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import com.andrognito.pinlockview.PinLockListener
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentPinBinding
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.auth.biometric.BiometricSetUpFragment
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.VibrateType.TYPE_ONE
import com.lobstr.stellar.vault.presentation.util.VibratorUtil
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricListener
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricManager
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class PinFragment : BaseFragment(), PinFrView, PinLockListener, BiometricListener,
    AlertDialogFragment.OnDefaultAlertDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        const val STYLE_ENTER_PIN = 0
        const val STYLE_CREATE_PIN = 1
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentPinBinding? = null
    private val binding get() = _binding!!
    private var backPressedCallback: OnBackPressedCallback? = null

    @Inject
    lateinit var presenterProvider: Provider<PinFrPresenter>

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
        _binding = FragmentPinBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Set navigation buttons color.
     * @param light when true - gray, else - white.
     */
    override fun setupNavigationBar(@ColorRes color: Int, light: Boolean) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            requireActivity().window?.let {
                it.navigationBarColor = ContextCompat.getColor(requireContext(), color)
                WindowCompat.getInsetsController(it, it.decorView).isAppearanceLightNavigationBars = light
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMenuProvider()
        setListeners()
    }

    private fun addMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.pin, menu)

                val itemNeedHelp = menu.findItem(R.id.action_need_help)

                itemNeedHelp?.actionView?.setSafeOnClickListener {
                    onMenuItemSelected(itemNeedHelp)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_need_help -> mPresenter.needHelpClicked()
                    else -> return false
                }
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun setListeners() {
        backPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            mPresenter.onBackPressed()
        }
        binding.apply {
            pinLockView.setPinLockListener(this@PinFragment)
            tvPinLogOut.setSafeOnClickListener { mPresenter.logoutClicked() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback?.remove()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupToolbar(
        hasMenu: Boolean,
        toolbarColor: Int,
        upArrow: Int,
        upArrowColor: Int
    ) {
        (activity as? BaseActivity)?.apply {
            setActionBarBackground(toolbarColor)
            setActionBarIcon(upArrow, upArrowColor)
        }
    }

    override fun showHomeAsUp(show: Boolean) {
        (activity as? BaseActivity)?.changeActionBarIconVisibility(show)
    }

    override fun setScreenStyle(style: Int) {
        binding.apply {
            pinLockView.pinLength = 6

            if (style == STYLE_ENTER_PIN) {
                // Dark style.
                indicatorDotsWhite.pinLength = 6
                pinLockView.attachIndicatorDots(binding.indicatorDotsWhite)
                (activity as? PinActivity)?.window?.decorView?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_primary
                    )
                )
                pinLockView.textColor =
                    ContextCompat.getColor(requireContext(), android.R.color.white)

                tvPinTitle.isVisible = false
                ivPinLogo.isVisible = true
                indicatorDotsWhite.isVisible = true
                indicatorDots.isVisible = false
                tvPinLogOut.isVisible = true
            } else {
                // White style.
                indicatorDots.pinLength = 6
                pinLockView.attachIndicatorDots(indicatorDots)
                // NOTE By default AppTheme has android:windowBackground = white.
                pinLockView.textColor =
                    ContextCompat.getColor(requireContext(), android.R.color.black)

                tvPinTitle.isVisible = true
                ivPinLogo.isVisible = false
                indicatorDotsWhite.isVisible = false
                indicatorDots.isVisible = true
                tvPinLogOut.isVisible = false
            }
        }
    }

    override fun showTitle(@StringRes title: Int) {
        binding.tvPinTitle.text = getString(title)
    }

    override fun resetPin() {
        binding.pinLockView.resetPinLockView()
    }

    override fun showHomeScreen() {
        startActivity(Intent(requireContext(), HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    override fun showVaultAuthScreen() {
        startActivity(Intent(requireContext(), VaultAuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    override fun showBiometricSetUpScreen(pinMode: Byte) {
        FragmentTransactionManager.displayFragment(
            requireActivity().supportFragmentManager,
            childFragmentManager.fragmentFactory.instantiate(
                requireContext().classLoader,
                BiometricSetUpFragment::class.qualifiedName!!
            ).apply {
                arguments = bundleOf(Constant.Bundle.BUNDLE_PIN_MODE to pinMode)
            },
            R.id.flContainer,
            false
        )
    }

    override fun showErrorMessage(@StringRes message: Int) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun setResult(resultCode: Int) {
        activity?.setResult(resultCode)
    }

    override fun finishScreen(resultCode: Int) {
        // NOTE Skip pin appearance check for prevent pin screen duplication after finish.
        LVApplication.checkPinAppearance = false
        activity?.apply {
            setResult(resultCode)
            finish()
        }
    }

    override fun finishApp() {
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun showAuthScreen() {
        startActivity(Intent(requireContext(), AuthActivity::class.java).apply {
            putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.AUTH)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    // Pin Lock listeners callbacks.

    override fun onComplete(pin: String) {
        VibratorUtil.vibrate(requireContext(), TYPE_ONE)
        Timber.i("Pin complete: $pin")
        mPresenter.onPinComplete(pin)
    }

    override fun onEmpty() {
        VibratorUtil.vibrate(requireContext(), TYPE_ONE)
        Timber.i("Pin empty")
    }

    override fun onPinChange(pinLength: Int, intermediatePin: String) {
        VibratorUtil.vibrate(requireContext(), TYPE_ONE)
        Timber.i("Pin changed, new length $pinLength with intermediate pin $intermediatePin")
    }

    // Dialogs.

    override fun onPositiveBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPresenter.onAlertDialogPositiveButtonClicked(tag)
    }

    override fun onNegativeBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPresenter.onAlertDialogNegativeButtonClicked(tag)
    }

    override fun onNeutralBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun onCancel(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun showLogOutDialog() {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(R.string.log_out_mnemonics_title)
            .setMessage(R.string.log_out_mnemonics_description)
            .setNegativeBtnText(R.string.cancel_action)
            .setPositiveBtnText(R.string.log_out_title)
            .create()
            .show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.LOG_OUT)
    }

    override fun showCommonPinPatternDialog() {
        AlertDialogFragment.Builder(true)
            .setCancelable(false)
            .setTitle(R.string.common_pin_pattern_title)
            .setMessage(R.string.common_pin_pattern_description)
            .setNegativeBtnText(R.string.continue_action)
            .setPositiveBtnText(R.string.pin_change_title)
            .create()
            .show(
                childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.COMMON_PIN_PATTERN
            )
    }

    override fun sendMail(mail: String, subject: String, body: String?) {
        try {
            AppUtil.sendEmail(requireContext(), arrayOf(mail), subject, body)
        } catch (exc: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                getString(R.string.mail_msg_client_not_found),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Biometric.

    override fun showBiometricDialog() {
        if (mBiometricManager == null) {
            mBiometricManager = BiometricManager.BiometricBuilder(requireContext(), this)
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.cancel_action).uppercase())
                .build()
        }
        mBiometricManager?.authenticate(this)
    }

    override fun onBiometricAuthenticationPermissionNotGranted() {
        Toast.makeText(
            requireContext(),
            getString(R.string.biometric_error_permission_not_granted),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBiometricAuthenticationInternalError(error: String?) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
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