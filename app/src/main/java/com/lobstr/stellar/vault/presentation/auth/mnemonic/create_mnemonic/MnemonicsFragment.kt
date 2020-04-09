package com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic


import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic.ConfirmMnemonicsFragment
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.faq.FaqFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import kotlinx.android.synthetic.main.fragment_mnemonics.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

/**
 * Used for show or generate mnemonics
 */
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

    @InjectPresenter
    lateinit var mPresenter: MnemonicsPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideMnemonicsPresenter() =
        MnemonicsPresenter(
            arguments?.getBoolean(Constant.Bundle.BUNDLE_GENERATE_MNEMONICS) ?: false
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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_mnemonics, container, false) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        btnNext.setOnClickListener(this)
        btnClipToBoard.setOnClickListener(this)
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

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnNext -> mPresenter.nextClicked()
            R.id.btnClipToBoard -> mPresenter.clipToBordClicked()
        }
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun setActionLayerVisibility(isVisible: Boolean) {
        llActionLayer.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setupMnemonics(mnemonicItems: List<MnemonicItem>) {
        tvMnemonicsInstruction.text = String.format(getString(R.string.text_tv_mnemonics_instruction), mnemonicItems.size)
        mnemonicContainerView.mMnemonicList = mnemonicItems
        mnemonicContainerView.setupMnemonics()
    }

    override fun showConfirmationScreen(mnemonics: ArrayList<MnemonicItem>) {
        // Pass created mnemonics to confirmation screen.
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constant.Bundle.BUNDLE_MNEMONICS_ARRAY, mnemonics)
        val fragment = parentFragment!!.childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, ConfirmMnemonicsFragment::class.qualifiedName!!)
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text)
    }

    override fun showHelpScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            parentFragment!!.childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, FaqFragment::class.qualifiedName!!),
            R.id.fl_container
        )
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
