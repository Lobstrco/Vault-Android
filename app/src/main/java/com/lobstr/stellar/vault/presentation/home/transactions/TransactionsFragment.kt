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
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.home.transactions.adapter.OnTransactionItemClicked
import com.lobstr.stellar.vault.presentation.home.transactions.adapter.TransactionAdapter
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Code.IMPORT_XDR_FRAGMENT
import com.lobstr.stellar.vault.presentation.util.Constant.Code.TRANSACTION_DETAILS_FRAGMENT
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_transactions.*
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class TransactionsFragment : BaseFragment(), TransactionsView, SwipeRefreshLayout.OnRefreshListener,
    OnTransactionItemClicked, View.OnClickListener,
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

    @Inject
    lateinit var daggerPresenter: Lazy<TransactionsPresenter>

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { daggerPresenter.get() }

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
            R.layout.fragment_transactions,
            container,
            false
        ) else mView
        return mView
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
        srlTransactions.setOnRefreshListener(this)
        fabAddTransaction.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPresenter.handleOnActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save RecycleView position for restore it after.
        mPresenter.onSaveInstanceState(
            (rvTransactions?.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                ?: 0
        )
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v?.id) {
            fabAddTransaction.id -> mPresenter.addTransactionClicked()
        }
    }

    override fun onTransactionItemClick(transactionItem: TransactionItem) {
        mPresenter.transactionItemClicked(transactionItem)
    }

    override fun onRefresh() {
        mPresenter.refreshCalled()
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initRecycledView() {
        rvTransactions.layoutManager = LinearLayoutManager(context)
        rvTransactions.itemAnimator = null
        rvTransactions.adapter = TransactionAdapter(this)
        rvTransactions.addOnScrollListener(RecyclerTransactionsScrollingListener())
    }

    override fun showTransactionDetails(transactionItem: TransactionItem) {
        val intent = Intent(context, ContainerActivity::class.java)
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
        (rvTransactions.adapter as? TransactionAdapter)?.setTransactionList(items, needShowProgress)
        mPresenter.attemptRestoreRvPosition()
    }

    override fun scrollListToPosition(position: Int) {
        rvTransactions.scrollToPosition(position)
    }

    override fun showOptionsMenu(show: Boolean) {
        super.saveOptionsMenuVisibility(show)
    }

    override fun showPullToRefresh(show: Boolean) {
        srlTransactions.isRefreshing = show
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun showEmptyState(show: Boolean) {
        tvTransactionEmptyState.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showImportXdrScreen() {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.IMPORT_XDR)
        startActivityForResult(intent, IMPORT_XDR_FRAGMENT)
    }

    override fun checkRateUsDialog() {
        (activity as? HomeActivity)?.checkRateUsDialog()
    }

    override fun showClearInvalidTrDialog() {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(R.string.title_clear_invalid_tr_dialog)
            .setMessage(R.string.msg_clear_invalid_tr_dialog)
            .setNegativeBtnText(R.string.text_btn_cancel)
            .setPositiveBtnText(R.string.text_btn_ok)
            .create()
            .show(
                childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.CLEAR_INVALID_TR
            )
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
