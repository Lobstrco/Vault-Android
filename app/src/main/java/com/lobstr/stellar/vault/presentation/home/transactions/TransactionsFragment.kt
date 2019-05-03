package com.lobstr.stellar.vault.presentation.home.transactions


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
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
import kotlinx.android.synthetic.main.fragment_transactions.*

class TransactionsFragment : BaseFragment(), TransactionsView, SwipeRefreshLayout.OnRefreshListener,
    OnTransactionItemClicked, View.OnClickListener, AlertDialogFragment.OnDefaultAlertDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = TransactionsFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: TransactionsPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideTransactionsPresenter() = TransactionsPresenter()

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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_transactions, container, false) else mView
        return mView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.transactions, menu)

        val itemClear = menu?.findItem(R.id.action_clear)

        itemClear?.actionView?.setOnClickListener {
            onOptionsItemSelected(itemClear)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
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
        rvTransactions.addOnScrollListener(RecyclerAccessHistoryScrollingListener())
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

    override fun showTransactionList(items: MutableList<TransactionItem>) {
        (rvTransactions.adapter as TransactionAdapter).setTransactionList(items)
    }

    override fun scrollListToPosition(position: Int) {
        rvTransactions.scrollToPosition(position)
    }

    override fun showOptionsMenu(show: Boolean) {
        setMenuVisibility(show)
    }

    override fun showPullToRefresh(show: Boolean) {
        srlTransactions.isRefreshing = show
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun showEmptyState() {
        tvTransactionEmptyState.visibility = View.VISIBLE
    }

    override fun hideEmptyState() {
        tvTransactionEmptyState.visibility = View.GONE
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
            .setTitle(getString(R.string.title_clear_invalid_tr_dialog))
            .setMessage(getString(R.string.msg_clear_invalid_tr_dialog))
            .setNegativeBtnText(getString(R.string.text_btn_cancel))
            .setPositiveBtnText(getString(R.string.text_btn_ok))
            .create()
            .show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.CLEAR_INVALID_TR)
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

    private inner class RecyclerAccessHistoryScrollingListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            mPresenter.onListScrolled(visibleItemCount, totalItemCount, firstVisibleItemPosition, lastVisibleItemPosition)
        }
    }
}
