package com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic


import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentMnemonicsBinding
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic.ConfirmMnemonicsFragment
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

/**
 * Used for show or generate mnemonics
 */
@AndroidEntryPoint
class MnemonicsFragment : BaseFragment(),
    MnemonicsView, View.OnClickListener, AlertDialogFragment.OnDefaultAlertDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = MnemonicsFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentMnemonicsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<MnemonicsPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { presenterProvider.get().apply {
        generate = arguments?.getBoolean(Constant.Bundle.BUNDLE_GENERATE_MNEMONICS) ?: false
    } }

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
        _binding = FragmentMnemonicsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        binding.btnNext.setOnClickListener(this)
        binding.btnClipToBoard.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (activity is AuthActivity) {
            inflater.inflate(R.menu.mnemonics, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_info -> mPresenter.infoClicked()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed(): Boolean {
        // Handle back press only for mnemonics in authentication container.
        if (activity is AuthActivity) {
            mPresenter.backPressed()
            return true
        }

        return super.onBackPressed()
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
            binding.btnNext.id -> mPresenter.nextClicked()
            binding.btnClipToBoard.id -> mPresenter.clipToBordClicked()
        }
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun setActionLayerVisibility(isVisible: Boolean) {
        binding.llActionLayer.isVisible = isVisible
    }

    override fun setupMnemonics(mnemonicItems: List<MnemonicItem>) {
        binding.tvMnemonicsInstruction.text = String.format(getString(R.string.text_tv_mnemonics_instruction), mnemonicItems.size)
        binding.mnemonicContainerView.mMnemonicList = mnemonicItems
        binding.mnemonicContainerView.setupMnemonics()
    }

    override fun showConfirmationScreen(mnemonics: ArrayList<MnemonicItem>) {
        // Pass created mnemonics to confirmation screen.
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constant.Bundle.BUNDLE_MNEMONICS_ARRAY, mnemonics)
        val fragment = requireParentFragment().childFragmentManager.fragmentFactory.instantiate(requireContext().classLoader, ConfirmMnemonicsFragment::class.qualifiedName!!)
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            fragment,
            R.id.flContainer
        )
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text)
    }

    override fun showHelpScreen(articleId: Long, userId: String?) {
        SupportManager.showZendeskArticle(requireContext(), articleId, userId)
    }

    override fun showDenyAccountCreationDialog() {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(R.string.title_deny_account_creation_dialog)
            .setMessage(R.string.msg_deny_account_creation_dialog)
            .setNegativeBtnText(R.string.text_btn_cancel)
            .setPositiveBtnText(R.string.text_btn_ok)
            .create()
            .show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.DENY_ACCOUNT_CREATION)
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

    override fun showAuthFr() {
        // Show first fragment in container - AuthFragment.
        val containerFragmentManager = parentFragment?.childFragmentManager

        containerFragmentManager?.popBackStack(
            containerFragmentManager.getBackStackEntryAt(1).id,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
