package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.fusechain.digitalbits.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.transactions.details.adapter.OnOperationClicked
import com.lobstr.stellar.vault.presentation.home.transactions.details.adapter.TransactionOperationAdapter
import com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details.OperationDetailsFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import kotlinx.android.synthetic.main.fragment_operation_list.*

class OperationListFragment : BaseFragment(),
    OperationListView, OnOperationClicked {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = OperationListFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: OperationListPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideOperationListPresenter() =
        OperationListPresenter(
            arguments?.getParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM)!!
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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_operation_list, container, false) else mView
        return mView
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

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

    override fun onOperationItemClick(position: Int) {
        mPresenter.operationItemClicked(position)
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
