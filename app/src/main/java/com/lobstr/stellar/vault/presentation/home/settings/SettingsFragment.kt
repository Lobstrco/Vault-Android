package com.lobstr.stellar.vault.presentation.home.settings

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
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.lobstr.stellar.vault.R
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
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import kotlinx.android.synthetic.main.fragment_settings.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.util.*

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

    @InjectPresenter
    lateinit var mPresenter: SettingsPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideSettingsPresenter() = SettingsPresenter()

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
        mView = if (mView == null) inflater.inflate(
            R.layout.fragment_settings,
            container,
            false
        ) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        llPublicKey.setOnClickListener(this)
        tvSettingsSigners.setOnClickListener(this)
        tvSettingsMnemonics.setOnClickListener(this)
        tvSettingsChangePin.setOnClickListener(this)
        llSettingsSpamProtection.setOnClickListener(this)
        tvSettingsHelp.setOnClickListener(this)
        swSettingsBiometric.setOnCheckedChangeListener(this)
        swSettingsNotifications.setOnCheckedChangeListener(this)
        llSettingsSignerCardInfoContainer.setOnClickListener(this)
        llSettingsTrConfirmation.setOnClickListener(this)
        tvSettingsLicense.setOnClickListener(this)
        tvSettingsRateUs.setOnClickListener(this)
        tvSettingsContactSupport.setOnClickListener(this)
        tvLogOut.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPresenter.handleOnActivityResult(requestCode, resultCode, data)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v?.id) {
            llPublicKey.id -> mPresenter.publicKeyClicked()
            tvSettingsSigners.id -> mPresenter.signersClicked()
            tvSettingsMnemonics.id -> mPresenter.mnemonicsClicked()
            tvSettingsChangePin.id -> mPresenter.changePinClicked()
            llSettingsSpamProtection.id -> mPresenter.spamProtectionClicked()
            tvSettingsHelp.id -> mPresenter.helpClicked()
            llSettingsSignerCardInfoContainer.id -> mPresenter.signerCardClicked()
            llSettingsTrConfirmation.id -> mPresenter.trConfirmationClicked()
            tvSettingsLicense.id -> mPresenter.licenseClicked()
            tvSettingsRateUs.id -> mPresenter.rateUsClicked()
            tvSettingsContactSupport.id -> mPresenter.contactSupportClicked()
            tvLogOut.id -> mPresenter.logOutClicked()
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
        llBiometricContainer.visibility = if (isBiometricSupported) View.VISIBLE else View.GONE
        tvSettingsVersion.text = buildVersion

        llSettingsMnemonicsContainer.visibility =
            if (isRecoveryCodeAvailable) View.VISIBLE else View.GONE
        llSettingsSignerCardInfoContainer.visibility =
            if (isSignerCardInfoAvailable) View.VISIBLE else View.GONE
        llSettingsTrConfirmationContainer.visibility =
            if (isTransactionConfirmationAvailable) View.VISIBLE else View.GONE
        llSettingsChangePinContainer.visibility =
            if (isChangePinAvailable) View.VISIBLE else View.GONE
    }

    override fun setupSignersCount(signersCount: Int) {
        val message = String.format(
            getString(if (signersCount == 1) R.string.text_settings_signer else R.string.text_settings_signers),
            signersCount
        )
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

        tvSettingsSigners.text = spannedText
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
            R.id.fl_container
        )
    }

    override fun showMnemonicsScreen() {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.MNEMONICS)
        startActivity(intent)
    }

    override fun showConfirmPinCodeScreen() {
        startActivityForResult(Intent(context, PinActivity::class.java).apply {
            putExtra(Constant.Extra.EXTRA_PIN_MODE, Constant.PinMode.CONFIRM)
        }, Constant.Code.CONFIRM_PIN_FOR_MNEMONIC)
    }

    override fun showChangePinScreen() {
        startActivityForResult(Intent(context, PinActivity::class.java).apply {
            putExtra(Constant.Extra.EXTRA_PIN_MODE, Constant.PinMode.CHANGE)
        }, Constant.Code.CHANGE_PIN)
    }

    override fun showHelpScreen(userId: String?) {
        SupportManager.showZendeskHelpCenter(requireContext(), userId = userId)
    }

    override fun setBiometricChecked(checked: Boolean) {
        swSettingsBiometric.setOnCheckedChangeListener(null)
        swSettingsBiometric.isChecked = checked
        swSettingsBiometric.setOnCheckedChangeListener(this)
    }

    override fun setSpamProtection(config: String?) {
        tvSpamProtectionConfig.text = config
    }

    override fun setNotificationsChecked(checked: Boolean) {
        swSettingsNotifications.setOnCheckedChangeListener(null)
        swSettingsNotifications.isChecked = checked
        swSettingsNotifications.setOnCheckedChangeListener(this)
    }

    override fun setTrConfirmation(config: String?) {
        tvTrConfirmationConfig.text = config
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
            R.id.fl_container
        )
    }

    override fun showWebPage(storeUrl: String) {
        AppUtil.openWebPage(context, storeUrl)
    }

    override fun sendMail(mail: String, subject: String, body: String?) {
        try {
            AppUtil.sendEmail(requireContext(), mail, subject, body)
        } catch (exc: ActivityNotFoundException) {
            showMessage(getString(R.string.msg_mail_client_not_found))
        }
    }

    override fun showConfigScreen(config: Int) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.CONFIG)
        intent.putExtra(Constant.Extra.EXTRA_CONFIG, config)
        startActivityForResult(intent, config)
    }

    override fun setupPolicyYear(id: Int) {
        tvCurrentPolicyDate.text =
            String.format(getString(id), Calendar.getInstance().get(Calendar.YEAR))
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
