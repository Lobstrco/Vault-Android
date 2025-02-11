package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentOperationListBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.home.transactions.details.TransactionDetailsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.details.adapter.TransactionOperationAdapter
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.CustomDividerItemDecoration
import com.lobstr.stellar.vault.presentation.util.InsetsMargin
import com.lobstr.stellar.vault.presentation.util.InsetsPadding
import moxy.ktx.moxyPresenter

class OperationListFragment : BaseFragment(), OperationListView {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentOperationListBinding? = null
    private val binding get() = _binding!!

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter {
        OperationListPresenter(
            requireArguments().getInt(Constant.Bundle.BUNDLE_TRANSACTION_TITLE, R.string.toolbar_transaction_details_title),
            requireArguments().getIntegerArrayList(Constant.Bundle.BUNDLE_OPERATIONS_LIST)!!
        )
    }

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
        _binding = FragmentOperationListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun handleInsets(
        view: View?,
        typeMask: Int,
        insetsPadding: InsetsPadding?,
        insetsMargin: InsetsMargin?
    ) {
        // Ignore insets (Inner Fragment).
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initRecycledView(operations: List<Int>) {
        binding.apply {
            rvTransactionOperations.layoutManager = LinearLayoutManager(context)
            rvTransactionOperations.itemAnimator = null
            rvTransactionOperations.isNestedScrollingEnabled = false
            rvTransactionOperations.addItemDecoration(
                CustomDividerItemDecoration(
                    ContextCompat.getDrawable(requireContext(), R.drawable.divider_left_offset)!!.apply {
                        alpha = 51 // Alpha 0.2.
                    })
            )
            rvTransactionOperations.adapter = TransactionOperationAdapter(operations) { position ->
                mPresenter.operationItemClicked(position)
            }
        }
    }

    override fun showOperationDetailsScreen(position: Int) {
        (parentFragment as? TransactionDetailsFragment)?.operationDetailsClicked(position)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
