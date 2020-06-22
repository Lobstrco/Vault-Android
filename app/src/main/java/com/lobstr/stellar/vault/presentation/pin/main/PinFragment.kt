package com.lobstr.stellar.vault.presentation.pin.main

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.andrognito.pinlockview.PinLockListener
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.auth.biometric.BiometricSetUpFragment
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricListener
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricManager
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity
import kotlinx.android.synthetic.main.fragment_pin.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class PinFragment : BaseFragment(), PinFrView, PinLockListener, BiometricListener,
    View.OnClickListener, AlertDialogFragment.OnDefaultAlertDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = PinFragment::class.simpleName
        const val STYLE_ENTER_PIN = 0
        const val STYLE_CREATE_PIN = 1
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: PinFrPresenter

    private var mBiometricManager: BiometricManager? = null

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun providePinFrPresenter() =
        PinFrPresenter(
            arguments?.getByte(Constant.Bundle.BUNDLE_PIN_MODE) ?: Constant.PinMode.ENTER
        )

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
        mView = if (mView == null) inflater.inflate(
            R.layout.fragment_pin,
            container,
            false
        ) else mView
        return mView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.pin, menu)

        val itemNeedHelp = menu.findItem(R.id.action_need_help)

        itemNeedHelp?.actionView?.setOnClickListener {
            onOptionsItemSelected(itemNeedHelp)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_need_help -> {
                mPresenter.needHelpClicked()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Set navigation buttons color.
     * @param light when true - gray, else - white.
     */
    override fun windowLightNavigationBar(light: Boolean) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            activity?.window?.decorView?.systemUiVisibility =
                if (light) View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR else 0
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun setupToolbar(
        showHomeAsUp: Boolean,
        hasMenu: Boolean,
        toolbarColor: Int,
        upArrow: Int,
        upArrowColor: Int
    ) {
        (activity as? BaseActivity)?.changeActionBarIconVisibility(showHomeAsUp)
        setHasOptionsMenu(hasMenu)
        (activity as? BaseActivity)?.setActionBarBackground(toolbarColor)
        (activity as? BaseActivity)?.setHomeAsUpIndicator(upArrow, upArrowColor)
    }

    override fun setScreenStyle(style: Int) {
        pinLockView.pinLength = 6

        if (style == STYLE_ENTER_PIN) {
            indicatorDotsWhite.pinLength = 6
            pinLockView.attachIndicatorDots(indicatorDotsWhite)
            (activity as? PinActivity)?.window?.decorView?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_primary
                )
            )
            pinLockView.textColor = ContextCompat.getColor(requireContext(), android.R.color.white)

            tvPinTitle.visibility = View.GONE
            ivPinLogo.visibility = View.VISIBLE
            indicatorDotsWhite.visibility = View.VISIBLE
            indicatorDots.visibility = View.GONE
            tvPinLogOut.visibility = View.VISIBLE
        } else {
            indicatorDots.pinLength = 6
            pinLockView.attachIndicatorDots(indicatorDots)
            (activity as? PinActivity)?.window?.decorView?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.white
                )
            )
            pinLockView.textColor = ContextCompat.getColor(requireContext(), android.R.color.black)

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
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showVaultAuthScreen() {
        val intent = Intent(requireContext(), VaultAuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showBiometricSetUpScreen(pinMode: Byte) {
        val bundle = Bundle()
        bundle.putByte(Constant.Bundle.BUNDLE_PIN_MODE, pinMode)
        val fragment = childFragmentManager.fragmentFactory.instantiate(
            requireContext().classLoader,
            BiometricSetUpFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            requireActivity().supportFragmentManager,
            fragment,
            R.id.fl_container,
            false
        )
    }

    override fun showErrorMessage(@StringRes message: Int) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun finishScreenWithResult(resultCode: Int) {
        activity?.setResult(resultCode)
        activity?.finish()
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun showAuthScreen() {
        NotificationsManager.clearNotifications(requireContext())
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.AUTH)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // Pin Lock listeners callbacks.

    override fun onComplete(pin: String?) {
        AppUtil.vibrate(requireContext(), longArrayOf(0, 8, 0, 0))
        Log.i(PinActivity.LOG_TAG, "Pin complete: $pin")
        mPresenter.onPinComplete(pin)
    }

    override fun onEmpty() {
        AppUtil.vibrate(requireContext(), longArrayOf(0, 8, 0, 0))
        Log.i(PinActivity.LOG_TAG, "Pin empty")
    }

    override fun onPinChange(pinLength: Int, intermediatePin: String?) {
        AppUtil.vibrate(requireContext(), longArrayOf(0, 8, 0, 0))
        Log.i(
            PinActivity.LOG_TAG,
            "Pin changed, new length $pinLength with intermediate pin $intermediatePin"
        )
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
            .setTitle(R.string.title_log_out_mnemonics_dialog)
            .setMessage(R.string.msg_log_out_mnemonics_dialog)
            .setNegativeBtnText(R.string.text_btn_cancel)
            .setPositiveBtnText(R.string.text_btn_log_out)
            .create()
            .show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.LOG_OUT)
    }

    override fun showCommonPinPatternDialog() {
        AlertDialogFragment.Builder(true)
            .setCancelable(false)
            .setTitle(R.string.title_common_pin_pattern_dialog)
            .setMessage(R.string.msg_common_pin_pattern_dialog)
            .setNegativeBtnText(R.string.text_btn_continue)
            .setPositiveBtnText(R.string.text_btn_change_pin)
            .create()
            .show(
                childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.COMMON_PIN_PATTERN
            )
    }

    override fun sendMail(mail: String, subject: String, body: String?) {
        try {
            AppUtil.sendEmail(requireContext(), mail, subject, body)
        } catch (exc: ActivityNotFoundException) {
            Toast.makeText(requireContext(),getString(R.string.msg_mail_client_not_found), Toast.LENGTH_SHORT).show()
        }
    }

    // Biometric.

    override fun showBiometricDialog(show: Boolean) {
        mBiometricManager?.cancelAuthentication()

        if (show) {
            mBiometricManager = BiometricManager.BiometricBuilder(requireContext(), this)
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.text_btn_cancel).toUpperCase())
                .build()
            mBiometricManager?.authenticate(this)
        }
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