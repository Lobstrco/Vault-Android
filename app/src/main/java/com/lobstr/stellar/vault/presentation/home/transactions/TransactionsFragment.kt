package com.lobstr.stellar.vault.presentation.home.transactions


import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.view.MenuProvider
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentTransactionsBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.transactions.adapter.TransactionAdapter
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.Status
import com.lobstr.stellar.vault.presentation.util.InsetsMargin
import com.lobstr.stellar.vault.presentation.util.InsetsPadding
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class TransactionsFragment : BaseFragment(), TransactionsView, SwipeRefreshLayout.OnRefreshListener,
    AlertDialogFragment.OnDefaultAlertDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private val mRegisterForTransactionResult =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            handleTransactionResult(result)
        }

    fun handleTransactionResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            mvpDelegate.onAttach()
            mPresenter.handleTransactionResult()
        }
    }

    @Inject
    lateinit var presenterProvider: Provider<TransactionsPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter {
        presenterProvider.get().apply {
            status = arguments?.getString(Constant.Bundle.BUNDLE_TRANSACTIONS_STATUS) ?: Status.PENDING
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
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    override fun handleInsets(
        view: View?,
        typeMask: Int,
        insetsPadding: InsetsPadding?,
        insetsMargin: InsetsMargin?
    ) {
        when (parentFragment) {
            is TransactionsContainerFragment -> {
                // Skip insets.
            }
            else -> super.handleInsets(view, typeMask, insetsPadding, insetsMargin)
        }
    }

    private fun setListeners() {
        binding.srlTransactions.setOnRefreshListener(this)
        binding.fabAddTransaction.setSafeOnClickListener {
            mPresenter.addTransactionClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onRefresh() {
        mPresenter.refreshCalled()
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun setEmptyState(title: String) {
        binding.tvTransactionEmptyState.text = title
    }

    override fun showFab(show: Boolean) {
        binding.apply {
            fabAddTransaction.isVisible = show
            if (show) {
                fabAddTransaction.doOnPreDraw {
                    rvTransactions.updatePadding(
                        bottom = fabAddTransaction.height
                                + fabAddTransaction.marginTop
                                + fabAddTransaction.marginBottom
                                - AppUtil.convertDpToPixels(requireContext(), 8f)
                    )
                }
            } else {
                rvTransactions.updatePadding(
                    bottom = AppUtil.convertDpToPixels(requireContext(), 4f)
                )
            }
        }
    }

    override fun initRecycledView() {
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
            adapter = TransactionAdapter({
                mPresenter.transactionItemClicked(it)
            },{
                mPresenter.transactionItemLongClicked(it)
            })
            addOnScrollListener(RecyclerTransactionsScrollingListener())
        }
    }

    override fun showTransactionDetails(transactionItem: TransactionItem) {
        val launcher = when (val parent = parentFragment) {
            is TransactionsContainerFragment -> parent.mCommonRegisterForTransactionResult
            else -> mRegisterForTransactionResult
        }
        launcher.launch(
            Intent(
                context,
                ContainerActivity::class.java
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra(
                    Constant.Extra.EXTRA_NAVIGATION_FR,
                    Constant.Navigation.TRANSACTION_DETAILS
                )
                putExtra(Constant.Extra.EXTRA_TRANSACTION_ITEM, transactionItem)
            })
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showTransactionList(
        items: List<TransactionItem>,
        needShowProgress: Boolean
    ) {
        (binding.rvTransactions.adapter as? TransactionAdapter)?.setTransactionList(items, needShowProgress)
    }

    override fun scrollListToPosition(position: Int) {
        binding.rvTransactions.scrollToPosition(position)
    }

    override fun showOptionsMenu(show: Boolean) {
        if (show) {
            requireActivity().removeMenuProvider(mMenuProvider)
            requireActivity().addMenuProvider(mMenuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
        } else {
            requireActivity().removeMenuProvider(mMenuProvider)
        }
    }

    override fun showPullToRefresh(show: Boolean) {
        binding.srlTransactions.isRefreshing = show
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun showEmptyState(show: Boolean) {
        binding.tvTransactionEmptyState.isVisible = show
    }

    override fun showImportXdrScreen() {
        val launcher = when (val parent = parentFragment) {
            is TransactionsContainerFragment -> parent.mCommonRegisterForTransactionResult
            else -> mRegisterForTransactionResult
        }
        launcher.launch(Intent(context, ContainerActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.IMPORT_XDR)
        })
    }

    override fun showClearTransactionsDialog(
        tag: String,
        message: String,
        positiveBtnText: String,
        neutralBtnText: String?,
        negativeBtnText: String
    ) {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(R.string.remove_transactions_title)
            .setMessage(message)
            .setPositiveBtnText(positiveBtnText)
            .setNeutralBtnText(neutralBtnText)
            .setNegativeBtnText(negativeBtnText)
            .create()
            .show(childFragmentManager, tag)
    }

    override fun showDeclineTransactionDialog() {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(R.string.confirmation_title)
            .setMessage(R.string.transaction_confirmation_decline_description)
            .setNegativeBtnText(R.string.cancel_action)
            .setPositiveBtnText(R.string.yes_action)
            .create()
            .show(childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.DECLINE_TRANSACTION
            )
    }

    override fun onPositiveBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPresenter.onAlertDialogPositiveButtonClicked(tag)
    }

    override fun onNegativeBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPresenter.onAlertDialogNegativeButtonClicked(tag)
    }

    override fun onNeutralBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun onCancel(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    private inner class RecyclerTransactionsScrollingListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager: LinearLayoutManager =
                recyclerView.layoutManager as LinearLayoutManager
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            mPresenter.onListScrolled(
                totalItemCount,
                firstVisibleItemPosition,
                lastVisibleItemPosition
            )
        }
    }

    private val mMenuProvider by lazy {
        object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.transactions, menu)

                val itemClear = menu.findItem(R.id.action_clear)

                itemClear?.actionView?.setSafeOnClickListener {
                    onMenuItemSelected(itemClear)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_clear -> mPresenter.clearClicked()
                    else -> return false
                }
                return true
            }
        }
    }
}
