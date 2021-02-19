package com.lobstr.stellar.vault.presentation.home.transactions


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
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
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Code.IMPORT_XDR_FRAGMENT
import com.lobstr.stellar.vault.presentation.util.Constant.Code.TRANSACTION_DETAILS_FRAGMENT
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class TransactionsFragment : BaseFragment(), TransactionsView, SwipeRefreshLayout.OnRefreshListener,
    View.OnClickListener,
    AlertDialogFragment.OnDefaultAlertDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = TransactionsFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<TransactionsPresenter>

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
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.transactions, menu)

        val itemClear = menu.findItem(R.id.action_clear)

        itemClear?.actionView?.setOnClickListener {
            onOptionsItemSelected(itemClear)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clear -> {
                mPresenter.clearClicked()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        binding.srlTransactions.setOnRefreshListener(this)
        binding.fabAddTransaction.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPresenter.handleOnActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save RecycleView position for restore it after.
        mPresenter.onSaveInstanceState(
            (binding.rvTransactions.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                ?: 0
        )
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
            binding.fabAddTransaction.id -> mPresenter.addTransactionClicked()
        }
    }

    override fun onRefresh() {
        mPresenter.refreshCalled()
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initRecycledView() {
        binding.rvTransactions.layoutManager = LinearLayoutManager(context)
        binding.rvTransactions.itemAnimator = null
        binding.rvTransactions.adapter = TransactionAdapter {
            mPresenter.transactionItemClicked(it)
        }
        binding.rvTransactions.addOnScrollListener(RecyclerTransactionsScrollingListener())
    }

    override fun showTransactionDetails(transactionItem: TransactionItem) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.TRANSACTION_DETAILS)
        intent.putExtra(Constant.Extra.EXTRA_TRANSACTION_ITEM, transactionItem)
        startActivityForResult(intent, TRANSACTION_DETAILS_FRAGMENT)
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showTransactionList(
        items: List<TransactionItem>,
        needShowProgress: Boolean
    ) {
        (binding.rvTransactions.adapter as? TransactionAdapter)?.setTransactionList(items, needShowProgress)
        mPresenter.attemptRestoreRvPosition()
    }

    override fun scrollListToPosition(position: Int) {
        binding.rvTransactions.scrollToPosition(position)
    }

    override fun showOptionsMenu(show: Boolean) {
        super.saveOptionsMenuVisibility(show)
    }

    override fun showPullToRefresh(show: Boolean) {
        binding.srlTransactions.isRefreshing = show
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun showEmptyState(show: Boolean) {
        binding.tvTransactionEmptyState.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showImportXdrScreen() {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.IMPORT_XDR)
        startActivityForResult(intent, IMPORT_XDR_FRAGMENT)
    }

    override fun showClearTransactionsDialog() {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(R.string.title_clear_transactions_dialog)
            .setMessage(R.string.msg_clear_transactions_dialog)
            .setPositiveBtnText(R.string.text_btn_remove_invalid)
            .setNeutralBtnText(R.string.text_btn_cancel)
            .setNegativeBtnText(R.string.text_btn_remove_all)
            .create()
            .show(
                childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.CLEAR_TRANSACTIONS
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
}
