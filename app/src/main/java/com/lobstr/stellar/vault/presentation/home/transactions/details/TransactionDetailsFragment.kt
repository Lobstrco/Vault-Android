package com.lobstr.stellar.vault.presentation.home.transactions.details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.fusechain.digitalbits.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.transactions.details.adapter.OnOperationClicked
import com.lobstr.stellar.vault.presentation.home.transactions.details.adapter.TransactionOperationAdapter
import com.lobstr.stellar.vault.presentation.home.transactions.operation.OperationDetailsFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_TRANSACTION_ITEM
import com.lobstr.stellar.vault.presentation.util.Constant.Extra.EXTRA_TRANSACTION_ITEM
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import kotlinx.android.synthetic.main.fragment_transaction_details.*

class TransactionDetailsFragment : BaseFragment(), TransactionDetailsView, View.OnClickListener, OnOperationClicked {

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

    private var mProgressDialog: AlertDialogFragment? = null

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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_transaction_details, container, false) else mView
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

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnConfirm -> mPresenter.btnConfirmClicked()
            R.id.btnDeny -> mPresenter.btnDenyClicked()
        }
    }

    override fun onOperationItemClick(position: Int) {
        mPresenter.operationItemClicked(position)
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initRecycledView() {
        rvTransactionOperations.layoutManager = LinearLayoutManager(context)
        rvTransactionOperations.itemAnimator = null
        rvTransactionOperations.adapter = TransactionOperationAdapter(this)
    }

    override fun setOperationsToList(operationList: MutableList<Int>) {
        (rvTransactionOperations.adapter as TransactionOperationAdapter).setOperationList(operationList)
    }

    override fun setActionBtnVisibility(isConfirmVisible: Boolean, isDenyVisible: Boolean) {
        btnConfirm.visibility = if (isConfirmVisible) View.VISIBLE else View.GONE
        btnDeny.visibility = if (isDenyVisible) View.VISIBLE else View.GONE
    }

    override fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressDialog() {
        mProgressDialog = ProgressManager.show(activity as? AppCompatActivity, false)
    }

    override fun dismissProgressDialog() {
        ProgressManager.dismiss(mProgressDialog)
    }

    override fun successDenyTransaction(transactionItem: TransactionItem) {
        showMessage("Transaction denied")

        // notify target about changes
        val intent = Intent()
        intent.putExtra(EXTRA_TRANSACTION_ITEM, transactionItem)
        activity?.setResult(Activity.RESULT_OK, intent)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

    }

    override fun successConfirmTransaction(xdr: String, transactionItem: TransactionItem) {
        showMessage("Transaction Confirmed")

        // notify target about changes
        val intent = Intent()
        intent.putExtra(EXTRA_TRANSACTION_ITEM, transactionItem)
        activity?.setResult(Activity.RESULT_OK, intent)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }

    override fun notifyAboutNeedAdditionalSignatures(xdr: String) {
        // TODO
        AppUtil.copyToClipboard(context, xdr)
        showMessage("Transaction need additional signature")
    }

    override fun showOperationDetailsScreen(transactionItem: TransactionItem, position: Int) {
        val bundle = Bundle()
        bundle.putParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM, transactionItem)
        bundle.putInt(Constant.Bundle.BUNDLE_OPERATION_POSITION, position)

        val fragment = Fragment.instantiate(context, OperationDetailsFragment::class.java.name, bundle)
        fragment.setTargetFragment(this, Constant.Code.OPERATION_DETAILS_FRAGMENT)

        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            fragment,
            R.id.fl_container,
            true
        )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
