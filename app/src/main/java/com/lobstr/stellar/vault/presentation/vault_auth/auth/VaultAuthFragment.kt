package com.lobstr.stellar.vault.presentation.vault_auth.auth

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.isInvisible
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentVaultAuthBinding
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
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class VaultAuthFragment : BaseFragment(), VaultAuthFrView,
    AlertDialogFragment.OnDefaultAlertDialogListener, TangemDialogFragment.OnTangemDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = VaultAuthFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentVaultAuthBinding? = null
    private val binding get() = _binding!!
    private var backPressedCallback: OnBackPressedCallback? = null

    @Inject
    lateinit var presenterProvider: Provider<VaultAuthFrPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { presenterProvider.get() }

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
        _binding = FragmentVaultAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMenuProvider()
        setListeners()
    }

    private fun addMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.auth_token, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    // NOTE add some items in menu if needed.
                    else -> return false
                }
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun setListeners() {
        backPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            mPresenter.logOutClicked()
        }

        binding.btnAuth.setSafeOnClickListener { mPresenter.authClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback?.remove()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupInfo(
        showIdentityLogo: Boolean,
        showTangemLogo: Boolean,
        identityIconUrl: String,
        title: String?,
        description: String?,
        descriptionMain: String,
        button: String
    ) {
        binding.ivBgIdentity.isVisible = showIdentityLogo
        binding.ivTangem.isVisible = showTangemLogo
        binding.tvDescription.isVisible = !description.isNullOrEmpty()
        if (showIdentityLogo) {
            // Set user identity icon.
            Glide.with(requireContext())
                .load(identityIconUrl)
                .placeholder(R.drawable.ic_person)
                .into(binding.ivIdentity)
        }

        binding.tvTitle.text = title
        binding.tvDescription.text = description
        binding.tvDescriptionMain.text = descriptionMain
        binding.btnAuth.text = button
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
        (activity as? VaultAuthActivity)?.window?.decorView
            ?.findViewById<View>(android.R.id.content)?.isInvisible = !show
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
