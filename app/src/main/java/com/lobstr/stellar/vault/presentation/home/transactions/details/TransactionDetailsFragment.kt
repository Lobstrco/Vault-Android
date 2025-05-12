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
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentTransactionDetailsBinding
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.CONFIRM_TRANSACTION
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.DECLINE_TRANSACTION
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.SEQUENCE_NUMBER_WARNING
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.error.Error
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.AccountAdapter
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account.EditAccountDialogFragment
import com.lobstr.stellar.vault.presentation.home.transactions.operation.asset_info.AssetInfoDialogFragment
import com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details.OperationDetailsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list.OperationListFragment
import com.lobstr.stellar.vault.presentation.home.transactions.submit_error.ErrorFragment
import com.lobstr.stellar.vault.presentation.home.transactions.submit_success.SuccessFragment
import com.lobstr.stellar.vault.presentation.tangem.dialog.TangemDialogFragment
import com.lobstr.stellar.vault.presentation.util.*
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

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentTransactionDetailsBinding? = null
    private val binding get() = _binding!!
    private var backPressedCallback: OnBackPressedCallback? = null

    @Inject
    lateinit var presenterProvider: Provider<TransactionDetailsPresenter>

    // Stores the set of menu item IDs that should currently be hidden.
    private var hiddenMenuItemIds: Set<Int> = emptySet()
    // Add more IDs here if their visibility needs to be managed.
    private val controllableMenuItemIds = setOf(
        R.id.action_copy_signed_xdr
    )

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter {
        presenterProvider.get().apply {
            transactionItem = arguments?.parcelable(BUNDLE_TRANSACTION_ITEM)!!
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
        requireActivity().addMenuProvider(mMenuProvider, viewLifecycleOwner)
        setListeners()
    }

    override fun handleInsets(
        view: View?,
        typeMask: Int,
        insetsPadding: InsetsPadding?,
        insetsMargin: InsetsMargin?
    ) {
        // Skip ime insets for details.
        super.handleInsets(
            view = view,
            typeMask = WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout(),
            insetsPadding = insetsPadding,
            insetsMargin = null
        )
    }

    private fun setListeners() {
        backPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // handle operations container backStack.
            if (childFragmentManager.backStackEntryCount > 1) {
                childFragmentManager.popBackStack()
            } else {
                // When operations container backStack has one item - handle backStack through base container.
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.apply {
            btnConfirm.setSafeOnClickListener { mPresenter.btnConfirmClicked() }
            btnDecline.setSafeOnClickListener { mPresenter.btnDeclineClicked() }
        }

        childFragmentManager.addOnBackStackChangedListener {
            mPresenter.backStackChanged(childFragmentManager.backStackEntryCount)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback?.remove()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun updateMenuItemsVisibility(hiddenItemIds: Set<Int>) {
        // Check if the set of hidden items has actually changed.
        if (this.hiddenMenuItemIds != hiddenItemIds) {
            this.hiddenMenuItemIds = hiddenItemIds
            requireActivity().invalidateOptionsMenu()
        }
    }

    override fun initSignersRecycledView() {
        binding.rvSigners.apply {
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = null
            isNestedScrollingEnabled = false
            addItemDecoration(
                CustomDividerItemDecoration(
                    ContextCompat.getDrawable(requireContext(), R.drawable.divider_left_offset)!!.apply {
                        alpha = 51 // Alpha 0.2.
                    })
            )
            adapter = AccountAdapter(
                AccountAdapter.ACCOUNT_WITH_STATUS,
                { mPresenter.signedAccountItemClicked(it) },
                { mPresenter.signedAccountItemLongClicked(it) })
        }
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

    override fun showSignersCount(countSummary: String?, countToSubmit: String?) {
        binding.apply {
            tvSignersCount.text = countSummary
            tvSignaturesCountToSubmit.text = countToSubmit
            tvSignaturesCountToSubmit.isVisible = !countToSubmit.isNullOrEmpty()
        }
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
        binding.svContent.apply {
            mPresenter.operationDetailsClicked(position, scrollX, scrollY)
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        binding.svContent.apply {
            post {
                scrollTo(x, y)
            }
        }
    }

    override fun setupTransactionInfo(fields: List<OperationField>) {
        binding.llAdditionalInfo.removeAllViews()
        for ((key, value, tag) in fields) {
            val root =
                layoutInflater.inflate(R.layout.adapter_item_operation_details, view as ViewGroup, false)
            val fieldName = root.findViewById<TextView>(R.id.tvFieldName)
            val fieldValue = root.findViewById<TextView>(R.id.tvFieldValue)

            val isPublicKeyTag by lazy { AppUtil.isValidAccount(tag as? String) }
            val isPublicKeyValue by lazy { AppUtil.isValidAccount(value) }
            val isAssetTag by lazy {  tag is Asset }

            val isSelectableField = isPublicKeyTag || isAssetTag

            (root.findViewById<FrameLayout>(R.id.flContent))?.foreground = if (isSelectableField) {
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
                AppUtil.getString(com.lobstr.stellar.tsmapper.R.string.transaction_memo) -> TextUtils.TruncateAt.END
                else -> {
                    when {
                        // Case for the Account Name.
                        isPublicKeyTag && !isPublicKeyValue -> TextUtils.TruncateAt.END
                        else -> TextUtils.TruncateAt.MIDDLE
                    }
                }
            }

            fieldName.text = key
            fieldValue.text = if (isPublicKeyValue) {
                AppUtil.ellipsizeStrInMiddle(value, Constant.Util.PK_TRUNCATE_COUNT)
            } else {
                value
            }

            if (isSelectableField) {
                root.setSafeOnClickListener {
                    mPresenter.additionalInfoValueClicked(key, value, tag)
                }
            }

            binding.llAdditionalInfo.addView(root)
        }
    }

    override fun setActionBtnState(
        isConfirmVisible: Boolean,
        isDeclineVisible: Boolean,
        isConfirmEnabled: Boolean,
        isDeclineEnabled: Boolean
    ) {
        binding.apply {
            llActionBtnContainer.isVisible = isConfirmVisible || isDeclineVisible
            actionDivider.isVisible = isConfirmVisible && isDeclineVisible
            btnConfirm.isVisible = isConfirmVisible
            btnDecline.isVisible = isDeclineVisible
            btnConfirm.isEnabled = isConfirmEnabled
            btnDecline.isEnabled = isDeclineEnabled
        }
    }

    override fun showActionContainer(show: Boolean) {
        binding.llActionContainer.isVisible = show
    }

    override fun showWarningLabel(text: String, color: Int) {
        binding.tvErrorDescription.apply {
            this.text = text
            setTextColor(ContextCompat.getColor(requireContext(), color))
            isVisible = true
        }
    }

    override fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun successDeclineTransaction(transactionItem: TransactionItem) {
        showMessage(getString(R.string.transaction_details_msg_declined))

        // Notify target about changes.
        activity?.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(EXTRA_TRANSACTION_ITEM, transactionItem)
            putExtra(EXTRA_TRANSACTION_STATUS, Constant.Transaction.CANCELLED)
        })
        setFragmentResult(Constant.RequestKey.TRANSACTION_DETAILS_FRAGMENT, bundleOf(
            EXTRA_TRANSACTION_ITEM to transactionItem,
            EXTRA_TRANSACTION_STATUS to Constant.Transaction.CANCELLED
        ))

        // Close screen.
        (activity as? ContainerActivity)?.finish() ?: activity?.onBackPressedDispatcher?.onBackPressed()
    }

    override fun successConfirmTransaction(
        hash: String,
        envelopeXdr: String,
        transactionSuccessStatus: Byte,
        transactionItem: TransactionItem
    ) {
        // Notify target about changes.
        activity?.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(EXTRA_TRANSACTION_ITEM, transactionItem)
            putExtra(EXTRA_TRANSACTION_STATUS, Constant.Transaction.SIGNED)
        })
        setFragmentResult(Constant.RequestKey.TRANSACTION_DETAILS_FRAGMENT, bundleOf(
            EXTRA_TRANSACTION_ITEM to transactionItem,
            EXTRA_TRANSACTION_STATUS to Constant.Transaction.SIGNED
        ))

        // Mark Check Rate Us state.
        LVApplication.checkRateUsDialogState = Constant.RateUsSessionState.CHECK

        // Close screen.
        parentFragment?.childFragmentManager?.popBackStack()

        // Show success screen.
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(
                requireContext().classLoader,
                SuccessFragment::class.qualifiedName!!
            ).apply {
                arguments = bundleOf(
                    Constant.Bundle.BUNDLE_TRANSACTION_HASH to hash,
                    Constant.Bundle.BUNDLE_ENVELOPE_XDR to envelopeXdr,
                    Constant.Bundle.BUNDLE_TRANSACTION_CONFIRMATION_SUCCESS_STATUS to transactionSuccessStatus
                )
            },
            R.id.flContainer,
            false
        )
    }

    override fun errorConfirmTransaction(error: Error) {
        // Notify target about changes.
        activity?.setResult(Activity.RESULT_OK)
        setFragmentResult(Constant.RequestKey.TRANSACTION_DETAILS_FRAGMENT, Bundle.EMPTY)

        // Close screen.
        parentFragment?.childFragmentManager?.popBackStack()

        // Show error screen.
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(
                requireContext().classLoader,
                ErrorFragment::class.qualifiedName!!
            ).apply {
                arguments = bundleOf(Constant.Bundle.BUNDLE_ERROR to error)
            },
            R.id.flContainer
        )
    }

    override fun showConfirmTransactionDialog(show: Boolean, message: String?) {
        if (show) {
            AlertDialogFragment.Builder(true)
                .setCancelable(true)
                .setTitle(R.string.confirmation_title)
                .setMessage(message)
                .setNegativeBtnText(R.string.cancel_action)
                .setPositiveBtnText(R.string.yes_action)
                .create()
                .showInstant(childFragmentManager, CONFIRM_TRANSACTION)
        } else {
            (childFragmentManager.findFragmentByTag(CONFIRM_TRANSACTION) as? AlertDialogFragment)?.dismissAllowingStateLoss()
        }
    }

    override fun showDeclineTransactionDialog() {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(R.string.confirmation_title)
            .setMessage(R.string.transaction_confirmation_decline_description)
            .setNegativeBtnText(R.string.cancel_action)
            .setPositiveBtnText(R.string.yes_action)
            .create()
            .show(childFragmentManager, DECLINE_TRANSACTION)
    }

    override fun showSequenceNumberWarningDialog(show: Boolean) {
        if (show) {
            AlertDialogFragment.Builder(true)
                .setCancelable(true)
                .setTitle(R.string.sequence_number_warning_title)
                .setMessage(R.string.sequence_number_warning_description)
                .setNegativeBtnText(R.string.cancel_action)
                .setPositiveBtnText(R.string.confirm_action)
                .create()
                .showInstant(childFragmentManager, SEQUENCE_NUMBER_WARNING)
        } else {
            (childFragmentManager.findFragmentByTag(SEQUENCE_NUMBER_WARNING) as? AlertDialogFragment)?.dismissAllowingStateLoss()
        }
    }

    override fun onPositiveBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPresenter.onAlertDialogPositiveButtonClicked(tag)
    }

    override fun onNegativeBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPresenter.onAlertDialogNegativeButtonClicked(tag)
        // Add logic if needed.
    }

    override fun onNeutralBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun onCancel(tag: String?, dialogInterface: DialogInterface) {
        mPresenter.onAlertDialogCanceled(tag)
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

    override fun showAssetInfoDialog(code: String, issuer: String?) {
        AssetInfoDialogFragment().apply {
            arguments = Bundle().apply {
                putString(Constant.Bundle.BUNDLE_ASSET_CODE, code)
                putString(Constant.Bundle.BUNDLE_ASSET_ISSUER, issuer)
            }
        }.show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.ASSET_INFO)
    }

    override fun onSetAccountNickNameClicked(publicKey: String) {
        AlertDialogFragment.Builder(true)
            .setSpecificDialog(AlertDialogFragment.DialogIdentifier.ACCOUNT_NAME, bundleOf(
                Constant.Bundle.BUNDLE_PUBLIC_KEY to publicKey
            ))
            .create()
            .show(
                childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.ACCOUNT_NAME
            )
    }

    override fun showTangemScreen(tangemInfo: TangemInfo) {
        TangemDialogFragment().apply {
            this.arguments = bundleOf(
                Constant.Extra.EXTRA_TANGEM_INFO to tangemInfo
            )
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

    private val mMenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.transaction_details, menu)
            applyMenuItemsVisibility(menu, hiddenMenuItemIds)
        }

        override fun onPrepareMenu(menu: Menu) {
            applyMenuItemsVisibility(menu, hiddenMenuItemIds)
            super.onPrepareMenu(menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            // Check if the item is currently hidden.
            if (menuItem.itemId in hiddenMenuItemIds) {
                return false
            }

            when (menuItem.itemId) {
                R.id.action_copy_xdr -> mPresenter.copyXdrClicked()
                R.id.action_copy_signed_xdr -> mPresenter.copySignedXdrClicked()
                R.id.action_view_transaction_details -> mPresenter.viewTransactionDetailsClicked()
                else -> return false
            }
            return true
        }

        private fun applyMenuItemsVisibility(menu: Menu, hiddenIds: Set<Int>) {
            for (itemId in controllableMenuItemIds) {
                val item = menu.findItem(itemId)
                item?.isVisible = itemId !in hiddenIds
            }
        }
    }
}
