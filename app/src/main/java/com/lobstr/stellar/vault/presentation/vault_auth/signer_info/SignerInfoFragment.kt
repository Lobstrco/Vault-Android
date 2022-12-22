package com.lobstr.stellar.vault.presentation.vault_auth.signer_info


import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentSignerInfoBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.home.settings.show_public_key.ShowPublicKeyDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.LobstrWallet.DEEP_LINK_MULTISIG_SETUP
import com.lobstr.stellar.vault.presentation.util.Constant.LobstrWallet.PACKAGE_NAME
import com.lobstr.stellar.vault.presentation.util.Constant.Social.STORE_URL
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class SignerInfoFragment : BaseFragment(), SignerInfoView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = SignerInfoFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentSignerInfoBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<SignerInfoPresenter>

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
        _binding = FragmentSignerInfoBinding.inflate(inflater, container, false)
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
                    R.id.action_info -> mPresenter.infoClicked()
                    else -> return false
                }
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun setListeners() {
        binding.apply {
            btnDownloadLobstrApp.setSafeOnClickListener { mPresenter.downloadLobstrAppClicked() }
            btnOpenLobstrApp.setSafeOnClickListener { mPresenter.openLobstrAppClicked() }
            btnCopyUserPk.setSafeOnClickListener { mPresenter.copyUserPublicKeyClicked() }
            btnShowQr.setSafeOnClickListener { mPresenter.showQrClicked() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun checkExistenceLobstrApp() {
        val packageManager = requireActivity().packageManager
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = packageManager.getApplicationInfo(PACKAGE_NAME, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        binding.apply {
            cvLobstrWalletInfo.isVisible = applicationInfo != null
            cvLobstrWalletInstall.isVisible = applicationInfo == null
        }

        // Start check of Lobstr Wallet app install.
        mPresenter.startCheckExistenceLobstrAppWithInterval(applicationInfo == null)
    }

    override fun setupUserPublicKey(userPublicKey: String?) {
        binding.tvUserPublicKey.text = userPublicKey
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text)
    }

    override fun showHelpScreen(userId: String?) {
        SupportManager.showZendeskHelpCenter(requireContext(), userId = userId)
    }

    override fun showPublicKeyDialog(publicKey: String) {
        ShowPublicKeyDialogFragment().apply {
            arguments = bundleOf(Constant.Bundle.BUNDLE_PUBLIC_KEY to publicKey)
        }.show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.PUBLIC_KEY)
    }

    override fun downloadLobstrApp() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(STORE_URL.plus(PACKAGE_NAME))
            )
        )
    }

    override fun openLobstrMultisigSetupScreen() {
        try {
            // Try open lobstr multisignature setup screen by deep link.
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DEEP_LINK_MULTISIG_SETUP)))
        } catch (exc: ActivityNotFoundException) {
            // Otherwise try open Lobstr App.
            openLobstrApp()
        }
    }

    override fun openLobstrApp() {
        try {
            startActivity(requireActivity().packageManager.getLaunchIntentForPackage(PACKAGE_NAME))
        } catch (exc: Exception) {
            Toast.makeText(context, R.string.msg_no_app_found, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun finishScreen() {
        activity?.finish()
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
