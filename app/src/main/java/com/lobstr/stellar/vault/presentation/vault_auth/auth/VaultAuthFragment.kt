package com.lobstr.stellar.vault.presentation.vault_auth.auth

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.tangem.dialog.TangemDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_pin.*
import kotlinx.android.synthetic.main.fragment_vault_auth.*
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class VaultAuthFragment : BaseFragment(),
    VaultAuthFrView, View.OnClickListener, AlertDialogFragment.OnDefaultAlertDialogListener,
    TangemDialogFragment.OnTangemDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = VaultAuthFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @Inject
    lateinit var daggerPresenter: Lazy<VaultAuthFrPresenter>

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { daggerPresenter.get() }

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
            R.layout.fragment_vault_auth,
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
        btnAuth.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.auth_token, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // NOTE add some items in menu if needed.
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed(): Boolean {
        mPresenter.logOutClicked()
        return true
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v?.id) {
            btnAuth.id -> mPresenter.authClicked()
        }
    }

    override fun setupInfo(
        showIdentityLogo: Boolean,
        showTangemLogo: Boolean,
        identityIconUrl: String,
        title: String?,
        description: String?,
        descriptionMain: String,
        button: String
    ) {
        ivBgIdentity.visibility = if (showIdentityLogo) View.VISIBLE else View.GONE
        ivTangem.visibility = if (showTangemLogo) View.VISIBLE else View.GONE
        tvDescription.visibility = if (description.isNullOrEmpty()) View.GONE else View.VISIBLE
        if (showIdentityLogo) {
            // Set user identity icon.
            Glide.with(requireContext())
                .load(identityIconUrl)
                .placeholder(R.drawable.ic_person)
                .into(ivIdentity)
        }

        tvTitle.text = title
        tvDescription.text = description
        tvDescriptionMain.text = descriptionMain
        btnAuth.text = button
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text)
    }

    override fun showTangemScreen(tangemInfo: TangemInfo) {
        TangemDialogFragment().apply {
            this.arguments =
                Bundle().apply { putParcelable(Constant.Extra.EXTRA_TANGEM_INFO, tangemInfo) }
        }.show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.TANGEM)
    }

    override fun showIdentityContent(show: Boolean) {
        (activity as? VaultAuthActivity)?.content?.visibility = if(show) View.VISIBLE else View.INVISIBLE
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showHomeScreen() {
        val intent = Intent(context, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showAuthScreen() {
        NotificationsManager.clearNotifications(requireContext())
        val intent = Intent(context, AuthActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.AUTH)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // Dialogs.

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

    override fun success(tangemInfo: TangemInfo?) {
        mPresenter.handleTangemInfo(tangemInfo)
    }

    override fun cancel() {
        // Implement logic if needed.
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
