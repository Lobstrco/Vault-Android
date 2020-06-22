package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list.adapter.OperationDetailsAdapter
import com.lobstr.stellar.vault.presentation.util.Constant
import kotlinx.android.synthetic.main.fragment_operation_details.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class OperationDetailsFragment : BaseFragment(),
    OperationDetailsView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = OperationDetailsFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: OperationDetailsPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideOperationDetailsPresenter() =
        OperationDetailsPresenter(
            arguments?.getParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM)!!,
            arguments?.getInt(Constant.Bundle.BUNDLE_OPERATION_POSITION)!!
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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_operation_details, container, false) else mView
        return mView
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initRecycledView(fields: MutableList<OperationField>) {
        rvOperationDetails.layoutManager = LinearLayoutManager(activity)
        rvOperationDetails.itemAnimator = null
        rvOperationDetails.isNestedScrollingEnabled = false
        rvOperationDetails.adapter = OperationDetailsAdapter(fields)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
