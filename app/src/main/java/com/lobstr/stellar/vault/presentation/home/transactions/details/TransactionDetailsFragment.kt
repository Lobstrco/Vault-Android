package com.lobstr.stellar.vault.presentation.home.transactions.details

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.CONFIRM_TRANSACTION
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.DENY_TRANSACTION
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.AccountAdapter
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.OnAccountItemListener
import com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details.OperationDetailsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list.OperationListFragment
import com.lobstr.stellar.vault.presentation.home.transactions.submit_error.ErrorFragment
import com.lobstr.stellar.vault.presentation.home.transactions.submit_success.SuccessFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_TRANSACTION_ITEM
import com.lobstr.stellar.vault.presentation.util.Constant.Extra.EXTRA_TRANSACTION_ITEM
import com.lobstr.stellar.vault.presentation.util.Constant.Extra.EXTRA_TRANSACTION_STATUS
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import kotlinx.android.synthetic.main.fragment_transaction_details.*


class TransactionDetailsFragment : BaseFragment(), TransactionDetailsView, View.OnClickListener,
    AlertDialogFragment.OnDefaultAlertDialogListener, OnAccountItemListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = TransactionDetailsFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: TransactionDetailsPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideTransactionDetailsPresenter() = TransactionDetailsPresenter(
        arguments?.getParcelable(BUNDLE_TRANSACTION_ITEM)!!
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
        mView = if (mView == null) inflater.inflate(
            R.layout.fragment_transaction_details,
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
        btnConfirm.setOnClickListener(this)
        btnDeny.setOnClickListener(this)
    }

    override fun onBackPressed(): Boolean {
        // handle operations container backStack.
        if (childFragmentManager.backStackEntryCount > 1) {
            childFragmentManager.popBackStack()
            return true
        }

        // when operations container backStack has one item - handle backStack through base container
        return super.onBackPressed()
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnConfirm -> mPresenter.btnConfirmClicked()
            R.id.btnDeny -> mPresenter.btnDenyClicked()
        }
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initSignersRecycledView() {
        rvSigners.layoutManager = LinearLayoutManager(activity)
        rvSigners.itemAnimator = null
        rvSigners.isNestedScrollingEnabled = false
        rvSigners.adapter = AccountAdapter(AccountAdapter.ACCOUNT_WITH_STATUS, this)
    }

    override fun notifySignersAdapter(accounts: List<Account>) {
        (rvSigners.adapter as AccountAdapter).setAccountList(accounts)
    }

    override fun showSignersProgress(show: Boolean) {
        pbSigners.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showSignersContainer(show: Boolean) {
        llSignersContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onAccountItemClick(account: Account) {
        // implement logic if needed
    }

    override fun onAccountItemLongClick(account: Account) {
        AppUtil.copyToClipboard(context, account.address)
    }

    override fun showOperationList(transactionItem: TransactionItem) {
        // reset backStack after resetup operations
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val bundle = Bundle()
        bundle.putParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM, transactionItem)
        val fragment = childFragmentManager.fragmentFactory.instantiate(
            context!!.classLoader,
            OperationListFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showOperationDetailsScreen(transactionItem: TransactionItem, position: Int) {
        // reset backStack after resetup operations
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val bundle = Bundle()
        bundle.putParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM, transactionItem)
        bundle.putInt(Constant.Bundle.BUNDLE_OPERATION_POSITION, position)
        val fragment = childFragmentManager.fragmentFactory.instantiate(
            context!!.classLoader,
            OperationDetailsFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun setActionBtnVisibility(isConfirmVisible: Boolean, isDenyVisible: Boolean) {
        btnConfirm.visibility = if (isConfirmVisible) View.VISIBLE else View.GONE
        btnDeny.visibility = if (isDenyVisible) View.VISIBLE else View.GONE
    }

    override fun setTransactionValid(valid: Boolean) {
        btnConfirm.isEnabled = valid
        tvErrorDescription.visibility = if (valid) View.GONE else View.VISIBLE
    }

    override fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun successDenyTransaction(transactionItem: TransactionItem) {
        showMessage(getString(R.string.msg_transaction_denied))

        // notify target about changes
        val intent = Intent()
        intent.putExtra(EXTRA_TRANSACTION_ITEM, transactionItem)
        intent.putExtra(EXTRA_TRANSACTION_STATUS, Constant.Transaction.CANCELLED)
        activity?.setResult(Activity.RESULT_OK, intent)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

        // close screen
        activity?.onBackPressed()
    }

    override fun successConfirmTransaction(
        envelopeXdr: String,
        needAdditionalSignatures: Boolean,
        transactionItem: TransactionItem
    ) {
        // notify target about changes
        val intent = Intent()
        intent.putExtra(EXTRA_TRANSACTION_ITEM, transactionItem)
        intent.putExtra(EXTRA_TRANSACTION_STATUS, Constant.Transaction.SIGNED)
        activity?.setResult(Activity.RESULT_OK, intent)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

        // close screen
        parentFragment?.childFragmentManager?.popBackStack()

        // show success screen
        val bundle = Bundle()
        bundle.putString(Constant.Bundle.BUNDLE_ENVELOPE_XDR, envelopeXdr)
        bundle.putBoolean(
            Constant.Bundle.BUNDLE_NEED_ADDITIONAL_SIGNATURES,
            needAdditionalSignatures
        )
        val fragment = parentFragment!!.childFragmentManager.fragmentFactory.instantiate(
            context!!.classLoader,
            SuccessFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun errorConfirmTransaction(errorMessage: String) {
        // notify target about changes
        activity?.setResult(Activity.RESULT_OK)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)

        // close screen
        parentFragment?.childFragmentManager?.popBackStack()

        // show error screen
        val bundle = Bundle()
        bundle.putString(Constant.Bundle.BUNDLE_ERROR_MESSAGE, errorMessage)
        val fragment = parentFragment!!.childFragmentManager.fragmentFactory.instantiate(
            context!!.classLoader,
            ErrorFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showConfirmTransactionDialog() {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(R.string.title_transaction_action_dialog)
            .setMessage(R.string.msg_confirm_transaction_dialog)
            .setNegativeBtnText(R.string.text_btn_cancel)
            .setPositiveBtnText(R.string.text_btn_yes)
            .create()
            .show(childFragmentManager, CONFIRM_TRANSACTION)
    }

    override fun showDenyTransactionDialog() {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(R.string.title_transaction_action_dialog)
            .setMessage(R.string.msg_deny_transaction_dialog)
            .setNegativeBtnText(R.string.text_btn_cancel)
            .setPositiveBtnText(R.string.text_btn_yes)
            .create()
            .show(childFragmentManager, DENY_TRANSACTION)
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

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
