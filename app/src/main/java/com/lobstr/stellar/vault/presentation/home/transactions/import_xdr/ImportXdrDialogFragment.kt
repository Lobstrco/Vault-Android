package com.lobstr.stellar.vault.presentation.home.transactions.import_xdr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseBottomSheetDialog
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.transactions.TransactionsFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import kotlinx.android.synthetic.main.fragment_import_xdr.*


class ImportXdrDialogFragment : BaseBottomSheetDialog(), ImportXdrView, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ImportXdrDialogFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: ImportXdrPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideImportXdrPresenter() = ImportXdrPresenter()

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = if (mView == null) inflater.inflate(R.layout.fragment_import_xdr, container, false) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        btnNext.setOnClickListener(this)

        etImportXdr.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    // implement logic if needed
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // implement logic if needed
                }

                override fun afterTextChanged(s: Editable) {
                    mPresenter.xdrChanged(s.length)
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        getMvpDelegate().onAttach()

        // dismiss dialog and pass handle onActivityResult to parent fragment (TransactionsFragment)
        dismiss()
        (parentFragment as? TransactionsFragment)?.onActivityResult(requestCode, resultCode, data)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnNext -> {
                AppUtil.closeKeyboard(activity)
                mPresenter.nextClicked(etImportXdr.text.toString())
            }
        }
    }

    override fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, activity!!.supportFragmentManager)
    }

    override fun showTransactionDetails(transactionItem: TransactionItem) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.TRANSACTION_DETAILS)
        intent.putExtra(Constant.Extra.EXTRA_TRANSACTION_ITEM, transactionItem)
        startActivityForResult(intent, Constant.Code.TRANSACTION_DETAILS_FRAGMENT)
    }

    override fun setSubmitEnabled(enabled: Boolean) {
        btnNext.isEnabled = enabled
    }

    override fun showFormError(show: Boolean, error: String?) {
        tvError.text = error
        etImportXdr.setBackgroundResource(if (show) R.drawable.shape_input_error_edit_text else R.drawable.shape_input_edit_text)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}