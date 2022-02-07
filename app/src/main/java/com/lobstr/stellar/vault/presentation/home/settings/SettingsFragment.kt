package com.lobstr.stellar.vault.presentation.home.settings

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentSettingsBinding
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.BIOMETRIC_INFO_DIALOG
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.PUBLIC_KEY
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager
import com.lobstr.stellar.vault.presentation.home.settings.license.LicenseFragment
import com.lobstr.stellar.vault.presentation.home.settings.show_public_key.ShowPublicKeyDialogFragment
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.SignedAccountsFragment
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Util.UNDEFINED_VALUE
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class SettingsFragment : BaseFragment(), SettingsView, View.OnClickListener,
    CompoundButton.OnCheckedChangeListener, AlertDialogFragment.OnDefaultAlertDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = SettingsFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<SettingsPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { presenterProvider.get() }

    private val mRegisterForConfirmPinCodeResult =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                mvpDelegate.onAttach()
                mPresenter.handleConfirmPinResult()
            }
        }

    private val mRegisterForChangePinResult =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                mvpDelegate.onAttach()
                mPresenter.handleChangePinResult()
            }
        }

    private val mRegisterForConfigScreenResult =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                mvpDelegate.onAttach()
                mPresenter.handleConfigResult(
                    result.data?.getIntExtra(
                        Constant.Extra.EXTRA_CONFIG,
                        UNDEFINED_VALUE
                    ) ?: UNDEFINED_VALUE
                )
            }
        }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        mPresenter.userVisibleHintCalled(menuVisible)
    }

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        binding.llPublicKey.setOnClickListener(this)
        binding.tvSettingsSigners.setOnClickListener(this)
        binding.tvSettingsMnemonics.setOnClickListener(this)
        binding.tvSettingsChangePin.setOnClickListener(this)
        binding.llSettingsSpamProtection.setOnClickListener(this)
        binding.tvSettingsHelp.setOnClickListener(this)
        binding.swSettingsBiometric.setOnCheckedChangeListener(this)
        binding.swSettingsNotifications.setOnCheckedChangeListener(this)
        binding.llSettingsSignerCardInfoContainer.setOnClickListener(this)
        binding.llSettingsTrConfirmation.setOnClickListener(this)
        binding.tvSettingsLicense.setOnClickListener(this)
        binding.tvSettingsRateUs.setOnClickListener(this)
        binding.tvSettingsContactSupport.setOnClickListener(this)
        binding.tvLogOut.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.llPublicKey.id -> mPresenter.publicKeyClicked()
            binding.tvSettingsSigners.id -> mPresenter.signersClicked()
            binding.tvSettingsMnemonics.id -> mPresenter.mnemonicsClicked()
            binding.tvSettingsChangePin.id -> mPresenter.changePinClicked()
            binding.llSettingsSpamProtection.id -> mPresenter.spamProtectionClicked()
            binding.tvSettingsHelp.id -> mPresenter.helpClicked()
            binding.llSettingsSignerCardInfoContainer.id -> mPresenter.signerCardClicked()
            binding.llSettingsTrConfirmation.id -> mPresenter.trConfirmationClicked()
            binding.tvSettingsLicense.id -> mPresenter.licenseClicked()
            binding.tvSettingsRateUs.id -> mPresenter.rateUsClicked()
            binding.tvSettingsContactSupport.id -> mPresenter.contactSupportClicked()
            binding.tvLogOut.id -> mPresenter.logOutClicked()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.swSettingsBiometric -> mPresenter.biometricSwitched(isChecked)
            R.id.swSettingsNotifications -> mPresenter.notificationsSwitched(isChecked)
        }
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun setupSettingsData(
        buildVersion: String,
        isBiometricSupported: Boolean,
        isRecoveryCodeAvailable: Boolean,
        isSignerCardInfoAvailable: Boolean,
        isTransactionConfirmationAvailable: Boolean,
        isChangePinAvailable: Boolean
    ) {
        binding.llBiometricContainer.isVisible = isBiometricSupported
        binding.tvSettingsVersion.text = buildVersion

        binding.llSettingsMnemonicsContainer.isVisible = isRecoveryCodeAvailable
        binding.llSettingsSignerCardInfoContainer.isVisible = isSignerCardInfoAvailable
        binding.llSettingsTrConfirmationContainer.isVisible = isTransactionConfirmationAvailable
        binding.llSettingsChangePinContainer.isVisible = isChangePinAvailable
    }

    override fun setupSignersCount(signersCount: Int) {
        val message = getString(if (signersCount == 1) R.string.text_settings_signer else R.string.text_settings_signers, signersCount)
        val spannedText = SpannableString(message)
        val startPosition = message.indexOf(signersCount.toString())
        val endPosition = startPosition + signersCount.toString().length

        if (startPosition != Constant.Util.UNDEFINED_VALUE) {
            spannedText.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        this.requireContext(),
                        R.color.color_primary
                    )
                ),
                startPosition,
                endPosition,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannedText.setSpan(
                RelativeSizeSpan(1.2f),
                startPosition,
                endPosition,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannedText.setSpan(
                StyleSpan(Typeface.BOLD),
                startPosition,
                endPosition,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.tvSettingsSigners.text = spannedText
    }

    override fun showSuccessMessage(@StringRes message: Int) {
        Toast.makeText(context, getString(message), Toast.LENGTH_SHORT).show()
    }

    override fun showAuthScreen() {
        NotificationsManager.clearNotifications(requireContext())
        val intent = Intent(context, AuthActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.AUTH)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showPublicKeyDialog(publicKey: String) {
        val bundle = Bundle()
        bundle.putString(Constant.Bundle.BUNDLE_PUBLIC_KEY, publicKey)

        val dialog = ShowPublicKeyDialogFragment()
        dialog.arguments = bundle
        dialog.show(childFragmentManager, PUBLIC_KEY)
    }

    override fun showSignersScreen() {
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(
                requireContext().classLoader,
                SignedAccountsFragment::class.qualifiedName!!
            ),
            R.id.flContainer
        )
    }

    override fun showMnemonicsScreen() {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.MNEMONICS)
        startActivity(intent)
    }

    override fun showConfirmPinCodeScreen() {
        mRegisterForConfirmPinCodeResult.launch(Intent(context, PinActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(Constant.Extra.EXTRA_PIN_MODE, Constant.PinMode.CONFIRM)
        })
    }

    override fun showChangePinScreen() {
        mRegisterForChangePinResult.launch(Intent(context, PinActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(Constant.Extra.EXTRA_PIN_MODE, Constant.PinMode.CHANGE)
        })
    }

    override fun showHelpScreen(userId: String?) {
        SupportManager.showZendeskHelpCenter(requireContext(), userId = userId)
    }

    override fun setBiometricChecked(checked: Boolean) {
        binding.swSettingsBiometric.setOnCheckedChangeListener(null)
        binding.swSettingsBiometric.isChecked = checked
        binding.swSettingsBiometric.setOnCheckedChangeListener(this)
    }

    override fun setSpamProtection(config: String?) {
        binding.tvSpamProtectionConfig.text = config
    }

    override fun setNotificationsChecked(checked: Boolean) {
        binding.swSettingsNotifications.setOnCheckedChangeListener(null)
        binding.swSettingsNotifications.isChecked = checked
        binding.swSettingsNotifications.setOnCheckedChangeListener(this)
    }

    override fun setTrConfirmation(config: String?) {
        binding.tvTrConfirmationConfig.text = config
    }

    override fun showBiometricInfoDialog(titleRes: Int, messageRes: Int) {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(titleRes)
            .setMessage(messageRes)
            .setPositiveBtnText(R.string.text_btn_ok)
            .create()
            .show(childFragmentManager, BIOMETRIC_INFO_DIALOG)
    }

    override fun showLicenseScreen() {
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(
                requireContext().classLoader,
                LicenseFragment::class.qualifiedName!!
            ),
            R.id.flContainer
        )
    }

    override fun showWebPage(storeUrl: String) {
        AppUtil.openWebPage(context, storeUrl)
    }

    override fun sendMail(mail: String, subject: String, body: String?) {
        try {
            AppUtil.sendEmail(requireContext(), arrayOf(mail), subject, body)
        } catch (exc: ActivityNotFoundException) {
            showMessage(getString(R.string.msg_mail_client_not_found))
        }
    }

    override fun showConfigScreen(config: Int) {
        mRegisterForConfigScreenResult.launch(Intent(context, ContainerActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.CONFIG)
            putExtra(Constant.Extra.EXTRA_CONFIG, config)
        })
    }

    override fun setupPolicyYear(id: Int) {
        binding.tvCurrentPolicyDate.text = getString(id, Calendar.getInstance().get(Calendar.YEAR))
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

    override fun showLogOutDialog(title: String?, message: String?) {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(title)
            .setMessage(message)
            .setNegativeBtnText(R.string.text_btn_cancel)
            .setPositiveBtnText(R.string.text_btn_log_out)
            .create()
            .show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.LOG_OUT)
    }

    override fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
