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
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.transactions.details.TransactionDetailsFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import kotlinx.android.synthetic.main.fragment_import_xdr.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter


class ImportXdrFragment : BaseFragment(), ImportXdrView, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ImportXdrFragment::class.simpleName
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
                    // Implement logic if needed.
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // Implement logic if needed.
                }

                override fun afterTextChanged(s: Editable) {
                    mPresenter.xdrChanged(s.trim().length)
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }

        when (requestCode) {
            Constant.Code.TRANSACTION_DETAILS_FRAGMENT -> {
                when (data?.getIntExtra(Constant.Extra.EXTRA_TRANSACTION_STATUS, -1)) {
                    Constant.Transaction.SIGNED -> parentFragment?.childFragmentManager?.popBackStack()
                }
            }
        }
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v?.id) {
            btnNext.id -> {
                AppUtil.closeKeyboard(activity)
                mPresenter.nextClicked(etImportXdr.text.toString().trim())
            }
        }
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun showTransactionDetails(transactionItem: TransactionItem) {
        val bundle = Bundle()
        bundle.putParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM, transactionItem)
        val fragment = requireParentFragment().childFragmentManager.fragmentFactory.instantiate(requireContext().classLoader, TransactionDetailsFragment::class.qualifiedName!!)
        fragment.arguments = bundle

        fragment.setTargetFragment(this, Constant.Code.TRANSACTION_DETAILS_FRAGMENT)

        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            fragment,
            R.id.fl_container
        )
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