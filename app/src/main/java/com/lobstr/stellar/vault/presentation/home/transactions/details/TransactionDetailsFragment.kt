package com.lobstr.stellar.vault.presentation.home.transactions.details

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
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
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account.EditAccountDialogFragment
import com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details.OperationDetailsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list.OperationListFragment
import com.lobstr.stellar.vault.presentation.home.transactions.submit_error.ErrorFragment
import com.lobstr.stellar.vault.presentation.home.transactions.submit_success.SuccessFragment
import com.lobstr.stellar.vault.presentation.tangem.dialog.TangemDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_OPERATION
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_OPERATIONS_LIST
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_OPERATION_TITLE
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_TRANSACTION_ITEM
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_TRANSACTION_SOURCE_ACCOUNT
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_TRANSACTION_TITLE
import com.lobstr.stellar.vault.presentation.util.Constant.Extra.EXTRA_TRANSACTION_ITEM
import com.lobstr.stellar.vault.presentation.util.Constant.Extra.EXTRA_TRANSACTION_STATUS
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class TransactionDetailsFragment : BaseFragment(), TransactionDetailsView,
    AlertDialogFragment.OnDefaultAlertDialogListener, EditAccountDialogFragment.OnEditAccountDialogListener,
    TangemDialogFragment.OnTangemDialogListener {

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
        binding.btnConfirm.setSafeOnClickListener { mPresenter.btnConfirmClicked() }
        binding.btnDeny.setSafeOnClickListener { mPresenter.btnDenyClicked() }

        childFragmentManager.addOnBackStackChangedListener {
            mPresenter.backStackChanged(childFragmentManager.backStackEntryCount)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.transaction_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_copy_xdr -> mPresenter.copyXdrClicked()
            R.id.action_copy_signed_xdr -> mPresenter.copySignedXdrClicked()
            R.id.action_view_transaction_details -> mPresenter.viewTransactionDetailsClicked()
        }

        return super.onOptionsItemSelected(item)
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

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initSignersRecycledView() {
        binding.rvSigners.layoutManager = LinearLayoutManager(activity)
        binding.rvSigners.itemAnimator = null
        binding.rvSigners.isNestedScrollingEnabled = false
        binding.rvSigners.adapter = AccountAdapter(
            AccountAdapter.ACCOUNT_WITH_STATUS,
            { mPresenter.signedAccountItemClicked(it) },
            { mPresenter.signedAccountItemLongClicked(it) })
    }

    override fun notifySignersAdapter(accounts: List<Account>) {
        (binding.rvSigners.adapter as AccountAdapter).setAccountList(accounts)
    }

    override fun showSignersProgress(show: Boolean) {
        binding.pbSigners.isVisible = show
    }

    override fun showSignersContainer(show: Boolean) {
        binding.llSignersContainer.isVisible = show
    }

    override fun showSignersCount(count: String?) {
        binding.tvSignersCount.text = count
    }

    override fun initOperationList(title: Int, operations: List<Int>) {
        if (childFragmentManager.findFragmentById(R.id.flContainer) == null) {
            FragmentTransactionManager.displayFragment(
                childFragmentManager,
                childFragmentManager.fragmentFactory.instantiate(
                    requireContext().classLoader,
                    OperationListFragment::class.qualifiedName!!
                ).apply {
                    arguments = bundleOf(
                        BUNDLE_TRANSACTION_TITLE to title,
                        BUNDLE_OPERATIONS_LIST to operations
                    )
                },
                R.id.flContainer
            )
        }
    }

    override fun initOperationDetailsScreen(
        title: Int,
        operation: Operation,
        transactionSourceAccount: String
    ) {
        if (childFragmentManager.findFragmentById(R.id.flContainer) == null) {
            showOperationDetailsScreen(
                title,
                operation,
                transactionSourceAccount
            )
        }
    }

    override fun showOperationDetailsScreen(
        title: Int,
        operation: Operation,
        transactionSourceAccount: String
    ) {
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            childFragmentManager.fragmentFactory.instantiate(
                requireContext().classLoader,
                OperationDetailsFragment::class.qualifiedName!!
            ).apply {
                arguments = bundleOf(
                    BUNDLE_OPERATION_TITLE to title,
                    BUNDLE_OPERATION to operation,
                    BUNDLE_TRANSACTION_SOURCE_ACCOUNT to transactionSourceAccount
                )
            },
            R.id.flContainer
        )
    }

    override fun operationDetailsClicked(position: Int) {
        mPresenter.operationDetailsClicked(position)
    }

    override fun setupTransactionInfo(fields: List<OperationField>) {
        binding.llAdditionalInfo.removeAllViews()
        for ((key, value, tag) in fields) {
            val root =
                layoutInflater.inflate(R.layout.adapter_item_operation_details, view as ViewGroup, false)
            val fieldName = root.findViewById<TextView>(R.id.tvFieldName)
            val fieldValue = root.findViewById<TextView>(R.id.tvFieldValue)

            val isPublicKeyField = AppUtil.isValidAccount(tag as? String)

            // Set selectable foreground for public key fields.
            (root.findViewById<FrameLayout>(R.id.flContent))?.foreground = if(isPublicKeyField) {
                val outValue = TypedValue()
                if (requireContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)) {
                    ContextCompat.getDrawable(requireContext(), outValue.resourceId)
                } else {
                    null
                }
            } else {
                null
            }

            fieldValue.isSingleLine = value?.contains("\n") != true
            fieldValue.ellipsize = when (key) {
                AppUtil.getString(R.string.text_tv_transaction_memo) -> TextUtils.TruncateAt.END
                else -> {
                    when {
                        // Case for the Account Name.
                        isPublicKeyField && !AppUtil.isValidAccount(value) -> TextUtils.TruncateAt.END
                        else -> TextUtils.TruncateAt.MIDDLE
                    }
                }
            }

            fieldName.text = key
            fieldValue.text = if (AppUtil.isValidAccount(value)) {
                AppUtil.ellipsizeStrInMiddle(value, Constant.Util.PK_TRUNCATE_COUNT)
            } else {
                value
            }

            // Set Click Listener only for public key values.
            if(isPublicKeyField) {
                root.setSafeOnClickListener {
                    mPresenter.additionalInfoValueClicked(key, value, tag)
                }
            }

            binding.llAdditionalInfo.addView(root)
        }
    }

    override fun setActionBtnVisibility(isConfirmVisible: Boolean, isDenyVisible: Boolean) {
        binding.btnConfirm.isVisible = isConfirmVisible
        binding.btnDeny.isVisible = isDenyVisible
    }

    override fun showActionContainer(show: Boolean) {
        binding.llActionContainer.isVisible = show
    }

    override fun setTransactionValid(valid: Boolean) {
        binding.btnConfirm.isEnabled = valid
        binding.tvErrorDescription.isVisible = !valid
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
        setFragmentResult(Constant.RequestKey.TRANSACTION_DETAILS_FRAGMENT, Bundle().apply {
            putParcelable(EXTRA_TRANSACTION_ITEM, transactionItem)
            putInt(EXTRA_TRANSACTION_STATUS, Constant.Transaction.CANCELLED)
        })

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
        setFragmentResult(Constant.RequestKey.TRANSACTION_DETAILS_FRAGMENT, Bundle().apply {
            putParcelable(EXTRA_TRANSACTION_ITEM, transactionItem)
            putInt(EXTRA_TRANSACTION_STATUS, Constant.Transaction.SIGNED)
        })

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

    override fun errorConfirmTransaction(errorMessage: String, envelopeXdr: String) {
        // Notify target about changes.
        activity?.setResult(Activity.RESULT_OK)
        setFragmentResult(Constant.RequestKey.TRANSACTION_DETAILS_FRAGMENT, Bundle.EMPTY)

        // Close screen.
        parentFragment?.childFragmentManager?.popBackStack()

        // Show error screen.
        val bundle = Bundle()
        bundle.putString(Constant.Bundle.BUNDLE_ERROR_MESSAGE, errorMessage)
        bundle.putString(Constant.Bundle.BUNDLE_ENVELOPE_XDR, envelopeXdr)
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

    override fun showWebPage(url: String) {
        AppUtil.openWebPage(context, url)
    }

    override fun showEditAccountDialog(address: String) {
        EditAccountDialogFragment().apply {
            arguments = bundleOf(
                Constant.Bundle.BUNDLE_PUBLIC_KEY to address,
                Constant.Bundle.BUNDLE_MANAGE_ACCOUNT_NAME to true
            )
        }.show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.EDIT_ACCOUNT)
    }

    override fun onSetAccountNickNameClicked(publicKey: String) {
        AlertDialogFragment.Builder(true)
            .setSpecificDialog(AlertDialogFragment.DialogIdentifier.ACCOUNT_NAME, Bundle().apply {
                putString(Constant.Bundle.BUNDLE_PUBLIC_KEY, publicKey)
            })
            .create()
            .show(
                childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.ACCOUNT_NAME
            )
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
