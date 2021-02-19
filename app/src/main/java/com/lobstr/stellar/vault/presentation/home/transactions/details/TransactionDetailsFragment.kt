package com.lobstr.stellar.vault.presentation.home.transactions.details

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentTransactionDetailsBinding
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.CONFIRM_TRANSACTION
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.DENY_TRANSACTION
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.AccountAdapter
import com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details.OperationDetailsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list.OperationListFragment
import com.lobstr.stellar.vault.presentation.home.transactions.submit_error.ErrorFragment
import com.lobstr.stellar.vault.presentation.home.transactions.submit_success.SuccessFragment
import com.lobstr.stellar.vault.presentation.tangem.dialog.TangemDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_TRANSACTION_ITEM
import com.lobstr.stellar.vault.presentation.util.Constant.Extra.EXTRA_TRANSACTION_ITEM
import com.lobstr.stellar.vault.presentation.util.Constant.Extra.EXTRA_TRANSACTION_STATUS
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class TransactionDetailsFragment : BaseFragment(), TransactionDetailsView, View.OnClickListener,
    AlertDialogFragment.OnDefaultAlertDialogListener, TangemDialogFragment.OnTangemDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = TransactionDetailsFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentTransactionDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<TransactionDetailsPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter {
        presenterProvider.get().apply {
            transactionItem = arguments?.getParcelable(BUNDLE_TRANSACTION_ITEM)!!
        }
    }

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
        _binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        binding.btnConfirm.setOnClickListener(this)
        binding.btnDeny.setOnClickListener(this)

        childFragmentManager.addOnBackStackChangedListener {
            mPresenter.backStackChanged(childFragmentManager.backStackEntryCount)
        }
    }

    override fun onBackPressed(): Boolean {
        // handle operations container backStack.
        if (childFragmentManager.backStackEntryCount > 1) {
            childFragmentManager.popBackStack()
            return true
        }

        // When operations container backStack has one item - handle backStack through base container.
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
            binding.btnConfirm.id -> mPresenter.btnConfirmClicked()
            binding.btnDeny.id -> mPresenter.btnDenyClicked()
        }
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initSignersRecycledView() {
        binding.rvSigners.layoutManager = LinearLayoutManager(activity)
        binding.rvSigners.itemAnimator = null
        binding.rvSigners.isNestedScrollingEnabled = false
        binding.rvSigners.adapter = AccountAdapter(
            AccountAdapter.ACCOUNT_WITH_STATUS,
            { /*Implement logic if needed.*/ },
            { mPresenter.signedAccountItemLongClicked(it) })
    }

    override fun notifySignersAdapter(accounts: List<Account>) {
        (binding.rvSigners.adapter as AccountAdapter).setAccountList(accounts)
    }

    override fun showSignersProgress(show: Boolean) {
        binding.pbSigners.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showSignersContainer(show: Boolean) {
        binding.llSignersContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showSignersCount(count: String?) {
        binding.tvSignersCount.text = count
    }

    override fun showOperationList(transactionItem: TransactionItem) {
        // Reset backStack after resetup operations.
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val bundle = Bundle()
        bundle.putParcelable(BUNDLE_TRANSACTION_ITEM, transactionItem)
        val fragment = childFragmentManager.fragmentFactory.instantiate(
            requireContext().classLoader,
            OperationListFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            fragment,
            R.id.flContainer
        )
    }

    override fun showOperationDetailsScreen(transactionItem: TransactionItem, position: Int) {
        // Reset backStack after re-setup operations.
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val bundle = Bundle()
        bundle.putParcelable(BUNDLE_TRANSACTION_ITEM, transactionItem)
        bundle.putInt(Constant.Bundle.BUNDLE_OPERATION_POSITION, position)
        val fragment = childFragmentManager.fragmentFactory.instantiate(
            requireContext().classLoader,
            OperationDetailsFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            fragment,
            R.id.flContainer
        )
    }

    override fun setupTransactionInfo(map: Map<String, String?>) {
        for ((key, value) in map) {
            val root =
                layoutInflater.inflate(R.layout.layout_additional_value, view as ViewGroup, false)
            val fieldName = root.findViewById<TextView>(R.id.tvFieldName)
            val fieldValue = root.findViewById<TextView>(R.id.tvFieldValue)
            fieldName.text = key
            fieldValue.text = value

            // Show divider only fo first value.
            if (binding.llAdditionalInfo.childCount == 0) {
                val divider = root.findViewById<View>(R.id.divider)
                divider.visibility = View.VISIBLE
            }

            binding.llAdditionalInfo.addView(root)
        }
    }

    override fun setActionBtnVisibility(isConfirmVisible: Boolean, isDenyVisible: Boolean) {
        binding.btnConfirm.visibility = if (isConfirmVisible) View.VISIBLE else View.GONE
        binding.btnDeny.visibility = if (isDenyVisible) View.VISIBLE else View.GONE
    }

    override fun showActionContainer(show: Boolean) {
        binding.llActionContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setTransactionValid(valid: Boolean) {
        binding.btnConfirm.isEnabled = valid
        binding.tvErrorDescription.visibility = if (valid) View.GONE else View.VISIBLE
    }

    override fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun successDenyTransaction(transactionItem: TransactionItem) {
        showMessage(getString(R.string.msg_transaction_denied))

        // Notify target about changes.
        val intent = Intent()
        intent.putExtra(EXTRA_TRANSACTION_ITEM, transactionItem)
        intent.putExtra(EXTRA_TRANSACTION_STATUS, Constant.Transaction.CANCELLED)
        activity?.setResult(Activity.RESULT_OK, intent)

        // Close screen.
        (activity as? ContainerActivity)?.finish() ?: activity?.onBackPressed()
    }

    override fun successConfirmTransaction(
        envelopeXdr: String,
        transactionSuccessStatus: Byte,
        transactionItem: TransactionItem
    ) {
        // Notify target about changes.
        val intent = Intent()
        intent.putExtra(EXTRA_TRANSACTION_ITEM, transactionItem)
        intent.putExtra(EXTRA_TRANSACTION_STATUS, Constant.Transaction.SIGNED)
        activity?.setResult(Activity.RESULT_OK, intent)

        // Mark Check Rate Us state.
        LVApplication.checkRateUsDialogState = Constant.RateUsSessionState.CHECK

        // Close screen.
        parentFragment?.childFragmentManager?.popBackStack()

        // Show success screen.
        val bundle = Bundle()
        bundle.putString(Constant.Bundle.BUNDLE_ENVELOPE_XDR, envelopeXdr)
        bundle.putByte(
            Constant.Bundle.BUNDLE_TRANSACTION_CONFIRMATION_SUCCESS_STATUS,
            transactionSuccessStatus
        )
        val fragment = requireParentFragment().childFragmentManager.fragmentFactory.instantiate(
            requireContext().classLoader,
            SuccessFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            fragment,
            R.id.flContainer,
            false
        )
    }

    override fun errorConfirmTransaction(errorMessage: String) {
        // Notify target about changes.
        activity?.setResult(Activity.RESULT_OK)

        // Close screen.
        parentFragment?.childFragmentManager?.popBackStack()

        // Show error screen.
        val bundle = Bundle()
        bundle.putString(Constant.Bundle.BUNDLE_ERROR_MESSAGE, errorMessage)
        val fragment = requireParentFragment().childFragmentManager.fragmentFactory.instantiate(
            requireContext().classLoader,
            ErrorFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            fragment,
            R.id.flContainer
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
        // Add logic if needed.
    }

    override fun onNeutralBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun onCancel(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
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
