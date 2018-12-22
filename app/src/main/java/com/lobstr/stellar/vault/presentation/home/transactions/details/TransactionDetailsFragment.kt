package com.lobstr.stellar.vault.presentation.home.transactions.details


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_TRANSACTION_ITEM
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import kotlinx.android.synthetic.main.fragment_transaction_details.*

class TransactionDetailsFragment : BaseFragment(), TransactionDetailsView, View.OnClickListener {

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

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
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

    override fun succesDenyTransaction(transactionItem: TransactionItem) {
        //TODO
        showMessage("Transaction denied")
    }

    override fun succesConfirmTransaction(xdr: String) {
        //TODO
        showMessage("Transaction Confirmed")
    }

    override fun notifyAboutNeedAdditionalSignatures(xdr: String) {
        // TODO
        AppUtil.copyToClipboard(context, xdr)
        showMessage("Transaction need additional signature")
    }


    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
