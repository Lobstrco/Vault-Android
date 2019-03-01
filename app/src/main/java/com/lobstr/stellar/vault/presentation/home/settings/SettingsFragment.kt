package com.lobstr.stellar.vault.presentation.home.settings

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
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.fusechain.digitalbits.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.FINGERPRINT_INFO_DIALOG
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.PUBLIC_KEY
import com.lobstr.stellar.vault.presentation.faq.FaqFragment
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager
import com.lobstr.stellar.vault.presentation.home.settings.license.LicenseFragment
import com.lobstr.stellar.vault.presentation.home.settings.show_public_key.ShowPublicKeyDialogFragment
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.SignedAccountsFragment
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import kotlinx.android.synthetic.main.fragment_settings.*
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

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        mPresenter.userVisibleHintCalled(isVisibleToUser)
    }

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = if (mView == null) inflater.inflate(R.layout.fragment_settings, container, false) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        tvLogOut.setOnClickListener(this)
        llPublicKey.setOnClickListener(this)
        llSettingsSigners.setOnClickListener(this)
        llSettingsMnemonics.setOnClickListener(this)
        llSettingsChangePin.setOnClickListener(this)
        llSettingsHelp.setOnClickListener(this)
        swSettingsTouchId.setOnCheckedChangeListener(this)
        swSettingsNotifications.setOnCheckedChangeListener(this)
        llSettingsLicense.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        getMvpDelegate().onAttach()
        mPresenter.onActivityResult(requestCode, resultCode, data)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tvLogOut -> mPresenter.logOutClicked()
            R.id.llPublicKey -> mPresenter.publicKeyClicked()
            R.id.llSettingsSigners -> mPresenter.signersClicked()
            R.id.llSettingsMnemonics -> mPresenter.mnemonicsClicked()
            R.id.llSettingsChangePin -> mPresenter.changePinClicked()
            R.id.llSettingsHelp -> mPresenter.helpClicked()
            R.id.llSettingsLicense -> mPresenter.licenseClicked()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.swSettingsTouchId -> mPresenter.touchIdSwitched(isChecked)
            R.id.swSettingsNotifications -> mPresenter.notificationsSwitched(isChecked)
        }
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun setupSettingsData(
        buildVersion: String,
        isBiometricSupported: Boolean
    ) {
        llSettingsTouchId.visibility = if (isBiometricSupported) View.VISIBLE else View.GONE
        tvSettingsVersion.text = buildVersion
    }

    override fun setupSignersCount(signersCount: String) {
        val message = String.format(getString(R.string.text_settings_signers), signersCount)
        val spannedText = SpannableString(message)
        val startPosition = 11
        val endPosition = message.lastIndexOf(" ")

        spannedText.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this.context!!, R.color.color_primary)),
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

        tvSettingsSigners.text = spannedText
    }

    override fun showSuccessMessage(@StringRes message: Int) {
        Toast.makeText(context, getString(message), Toast.LENGTH_SHORT).show()
    }

    override fun showInfoFr() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            Fragment.instantiate(context, FaqFragment::class.java.name),
            R.id.fl_container,
            true
        )
    }

    override fun showAuthScreen() {
        NotificationsManager.clearNotifications(context!!)
        val intent = Intent(context, AuthActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.AUTH)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showSignersScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            Fragment.instantiate(context, SignedAccountsFragment::class.java.name),
            R.id.fl_container,
            true
        )
    }

    override fun showMnemonicsScreen() {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.MNEMONICS)
        startActivity(intent)
    }

    override fun showChangePinScreen() {
        val intent = Intent(context, PinActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_CHANGE_PIN, true)
        startActivityForResult(intent, Constant.Code.CHANGE_PIN)
    }

    override fun showHelpScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            Fragment.instantiate(context, FaqFragment::class.java.name),
            R.id.fl_container,
            true
        )
    }

    override fun setTouchIdChecked(checked: Boolean) {
        swSettingsTouchId.setOnCheckedChangeListener(null)
        swSettingsTouchId.isChecked = checked
        swSettingsTouchId.setOnCheckedChangeListener(this)
    }

    override fun setNotificationsChecked(checked: Boolean) {
        swSettingsNotifications.setOnCheckedChangeListener(null)
        swSettingsNotifications.isChecked = checked
        swSettingsNotifications.setOnCheckedChangeListener(this)
    }

    override fun showFingerprintInfoDialog(titleRes: Int, messageRes: Int) {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(getString(titleRes))
            .setMessage(getString(messageRes))
            .setPositiveBtnText(getString(R.string.text_btn_ok))
            .create()
            .show(childFragmentManager, FINGERPRINT_INFO_DIALOG)
    }

    override fun showPublicKeyDialog(publicKey: String) {
        val bundle = Bundle()
        bundle.putString(Constant.Bundle.BUNDLE_PUBLIC_KEY, publicKey)

        val dialog = ShowPublicKeyDialogFragment()
        dialog.arguments = bundle
        dialog.show(childFragmentManager, PUBLIC_KEY)
    }

    override fun showLicenseScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            Fragment.instantiate(context, LicenseFragment::class.java.name),
            R.id.fl_container,
            true
        )
    }

    override fun setupPolicyYear(id: Int) {
        tvCurrentPolicyDate.text = String.format(getString(id), Calendar.getInstance().get(Calendar.YEAR))
    }

    override fun onPositiveBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPresenter.onAlertDialogPositiveButtonClicked(tag)
    }

    override fun onNegativeBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // add logic if needed
    }

    override fun onNeutralBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // add logic if needed
    }

    override fun onCancel(tag: String?, dialogInterface: DialogInterface) {
        // add logic if needed
    }

    override fun showLogOutDialog() {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(getString(R.string.title_log_out_dialog))
            .setMessage(getString(R.string.msg_log_out_dialog))
            .setNegativeBtnText(getString(R.string.text_btn_cancel))
            .setPositiveBtnText(getString(R.string.text_btn_log_out))
            .create()
            .show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.LOG_OUT)
    }

    override fun showConfirmPinCodeScreen() {
        val intent = Intent(context, PinActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_CONFIRM_PIN, true)
        startActivityForResult(intent, Constant.Code.CONFIRM_PIN_FOR_MNEMONIC)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
