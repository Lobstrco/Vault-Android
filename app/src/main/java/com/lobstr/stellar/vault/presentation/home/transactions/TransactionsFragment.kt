package com.lobstr.stellar.vault.presentation.home.transactions


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.IMPORT_XDR
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.transactions.adapter.OnTransactionItemClicked
import com.lobstr.stellar.vault.presentation.home.transactions.adapter.TransactionAdapter
import com.lobstr.stellar.vault.presentation.home.transactions.import_xdr.ImportXdrDialogFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Code.TRANSACTION_DETAILS_FRAGMENT
import kotlinx.android.synthetic.main.fragment_transactions.*

class TransactionsFragment : BaseFragment(), TransactionsView, SwipeRefreshLayout.OnRefreshListener,
    OnTransactionItemClicked, View.OnClickListener {

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
    lateinit var presenter: TransactionsPresenter

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

        presenter.handleOnActivityResult(requestCode, resultCode, data)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v?.id) {
            fabAddTransaction.id -> presenter.addTransactionClicked()
        }
    }

    override fun onTransactionItemClick(transactionItem: TransactionItem) {
        presenter.transactionItemClicked(transactionItem)
    }

    override fun onRefresh() {
        presenter.refreshCalled()
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initRecycledView() {
        rvTransactions.layoutManager = LinearLayoutManager(activity)
        rvTransactions.itemAnimator = null
        rvTransactions.adapter = TransactionAdapter(this)
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

    override fun showProgress() {
        srlTransactions.isRefreshing = true
    }

    override fun hideProgress() {
        srlTransactions.isRefreshing = false
    }

    override fun showEmptyState() {
        tvTransactionEmptyState.visibility = View.VISIBLE
    }

    override fun hideEmptyState() {
        tvTransactionEmptyState.visibility = View.GONE
    }

    override fun showImportXdrScreen() {
        val dialog = ImportXdrDialogFragment()
        dialog.show(childFragmentManager, IMPORT_XDR)
    }

    override fun showSuccessScreen(envelopeXdr: String, needAdditionalSignatures: Boolean) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.SUCCESS)
        intent.putExtra(Constant.Extra.EXTRA_ENVELOPE_XDR, envelopeXdr)
        intent.putExtra(Constant.Extra.EXTRA_NEED_ADDITIONAL_SIGNATURES, needAdditionalSignatures)
        startActivity(intent)
    }

    override fun showErrorScreen(errorMessage: String) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.ERROR)
        intent.putExtra(Constant.Extra.EXTRA_ERROR_MESSAGE, errorMessage)
        startActivity(intent)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
