package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.transactions.details.adapter.OnOperationClicked
import com.lobstr.stellar.vault.presentation.home.transactions.details.adapter.TransactionOperationAdapter
import com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details.OperationDetailsFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import kotlinx.android.synthetic.main.fragment_operation_list.*
import moxy.ktx.moxyPresenter

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

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { OperationListPresenter(
        arguments?.getParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM)!!
    ) }

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
        rvTransactionOperations.isNestedScrollingEnabled = false
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

        val fragment = requireParentFragment().childFragmentManager.fragmentFactory.instantiate(requireContext().classLoader, OperationDetailsFragment::class.qualifiedName!!)
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
