package com.lobstr.stellar.vault.presentation.auth.tangem

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentTangemSetupBinding
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.tangem.TangemCreateWalletActivity
import com.lobstr.stellar.vault.presentation.tangem.dialog.TangemDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class TangemSetupFragment : BaseFragment(), TangemView, View.OnClickListener,
    TangemDialogFragment.OnTangemDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = TangemSetupFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentTangemSetupBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<TangemSetupPresenter>

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
        _binding = FragmentTangemSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        binding.btnScan.setOnClickListener(this)
        binding.btnLearnMore.setOnClickListener(this)
        binding.btnBuyNow.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tangem_setup, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_info -> mPresenter.infoClicked()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPresenter.handleOnActivityResult(requestCode, resultCode, data)
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
            binding.btnScan.id -> mPresenter.scanClicked()
            binding.btnLearnMore.id -> mPresenter.learnMoreClicked()
            binding.btnBuyNow.id -> mPresenter.buyNowClicked()
        }
    }

    override fun setupToolbar(color: Int) {
        (activity as? AuthActivity)?.updateToolbar(toolbarColor = color)
    }

    override fun showTangemScreen() {
        TangemDialogFragment().show(
            childFragmentManager,
            AlertDialogFragment.DialogFragmentIdentifier.TANGEM
        )
    }

    override fun showTangemCreateWalletScreen(tangemInfo: TangemInfo) {
        startActivityForResult(Intent(context, TangemCreateWalletActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(Constant.Extra.EXTRA_TANGEM_INFO, tangemInfo)
        }, Constant.Code.TANGEM_CREATE_WALLET)
    }

    override fun showVaultAuthScreen() {
        val intent = Intent(activity, VaultAuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showHelpScreen(articleId: Long) {
        SupportManager.showZendeskArticle(requireContext(), articleId)
    }

    override fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showNfcNotAvailable() {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(R.string.title_nfc_not_available_dialog)
            .setMessage(R.string.msg_nfc_not_available_dialog)
            .setPositiveBtnText(R.string.text_btn_ok)
            .create()
            .show(
                childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.INFO
            )
    }

    override fun showWebPage(url: String) {
        AppUtil.openWebPage(context, url)
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
