package com.lobstr.stellar.vault.presentation.home.settings

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentSettingsBinding
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.BIOMETRIC_INFO_DIALOG
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager
import com.lobstr.stellar.vault.presentation.home.account_name.manage.ManageAccountsNamesFragment
import com.lobstr.stellar.vault.presentation.home.settings.license.LicenseFragment
import com.lobstr.stellar.vault.presentation.home.settings.show_public_key.ShowPublicKeyDialogFragment
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.SignedAccountsFragment
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.util.*
import com.lobstr.stellar.vault.presentation.util.Constant.Util.UNDEFINED_VALUE
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class SettingsFragment : BaseFragment(), SettingsView, CompoundButton.OnCheckedChangeListener,
    AlertDialogFragment.OnDefaultAlertDialogListener {

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
        binding.llPublicKey.setSafeOnClickListener { mPresenter.publicKeyClicked() }
        binding.tvSettingsSigners.setSafeOnClickListener { mPresenter.signersClicked() }
        binding.tvSettingsMnemonics.setSafeOnClickListener { mPresenter.mnemonicsClicked() }
        binding.tvSettingsChangePin.setSafeOnClickListener { mPresenter.changePinClicked() }
        binding.llSettingsSpamProtection.setSafeOnClickListener { mPresenter.spamProtectionClicked() }
        binding.tvSettingsHelp.setSafeOnClickListener { mPresenter.helpClicked() }
        binding.swSettingsBiometric.setOnCheckedChangeListener(this)
        binding.swSettingsNotifications.setOnCheckedChangeListener(this)
        binding.llSettingsSignerCardInfoContainer.setSafeOnClickListener { mPresenter.signerCardClicked() }
        binding.llSettingsTrConfirmation.setSafeOnClickListener { mPresenter.trConfirmationClicked() }
        binding.tvSettingsManageNicknames.setSafeOnClickListener { mPresenter.manageNicknamesClicked() }
        binding.tvSettingsLicense.setSafeOnClickListener { mPresenter.licenseClicked() }
        binding.tvSettingsRateUs.setSafeOnClickListener { mPresenter.rateUsClicked() }
        binding.tvSettingsContactSupport.setSafeOnClickListener { mPresenter.contactSupportClicked() }
        binding.tvLogOut.setSafeOnClickListener { mPresenter.logOutClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

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
        binding.tvSettingsSigners.text = AppUtil.getQuantityString(R.plurals.text_settings_signers, signersCount, signersCount).run {
            val startPosition = indexOf(signersCount.toString())
            val endPosition = startPosition + signersCount.toString().length
            if (startPosition != Constant.Util.UNDEFINED_VALUE) {
                this
                    .applyColor(ContextCompat.getColor(
                        requireContext(),
                        R.color.color_primary
                    ), startPosition, endPosition)
                    .applySize(1.2f, startPosition, endPosition)
                    .applyStyle(Typeface.BOLD, startPosition, endPosition)
            } else {
                this
            }
        }
    }

    override fun showSuccessMessage(@StringRes message: Int) {
        Toast.makeText(context, getString(message), Toast.LENGTH_SHORT).show()
    }

    override fun showAuthScreen() {
        NotificationsManager.clearNotifications(requireContext())
        startActivity(Intent(context, AuthActivity::class.java).apply {
            putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.AUTH)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    override fun showPublicKeyDialog(publicKey: String) {
        ShowPublicKeyDialogFragment().apply {
            arguments = bundleOf(
                Constant.Bundle.BUNDLE_PUBLIC_KEY to publicKey
            )
        }.show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.PUBLIC_KEY)
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
        startActivity(Intent(context, ContainerActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.MNEMONICS)
        })
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

    override fun showManageNicknamesScreen() {
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(
                requireContext().classLoader,
                ManageAccountsNamesFragment::class.qualifiedName!!
            ),
            R.id.flContainer
        )
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
