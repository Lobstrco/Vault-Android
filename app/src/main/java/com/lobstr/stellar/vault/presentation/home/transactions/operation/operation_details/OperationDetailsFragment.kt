package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.vault.databinding.FragmentOperationDetailsBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.home.transactions.details.TransactionDetailsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.operation.asset_info.AssetInfoDialogFragment
import com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list.adapter.OperationDetailsAdapter
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.parcelable
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class OperationDetailsFragment : BaseFragment(),
    OperationDetailsView {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    @Inject
    lateinit var presenterProvider: Provider<OperationDetailsPresenter>

    private var _binding: FragmentOperationDetailsBinding? = null
    private val binding get() = _binding!!

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { presenterProvider.get().apply {
        title = requireArguments().getInt(Constant.Bundle.BUNDLE_OPERATION_TITLE, Constant.Util.UNDEFINED_VALUE)
        operation = requireArguments().parcelable(Constant.Bundle.BUNDLE_OPERATION)!!
        transactionSourceAccount = requireArguments().getString(Constant.Bundle.BUNDLE_TRANSACTION_SOURCE_ACCOUNT)!!
    } }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOperationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initRecycledView(fields: MutableList<OperationField>) {
        binding.rvOperationDetails.layoutManager = LinearLayoutManager(activity)
        binding.rvOperationDetails.itemAnimator = null
        binding.rvOperationDetails.isNestedScrollingEnabled = false
        binding.rvOperationDetails.adapter = OperationDetailsAdapter(fields, mPresenter::operationItemClicked)
    }

    override fun notifyAdapter() {
        (binding.rvOperationDetails.adapter as? OperationDetailsAdapter)?.notifyDataSetChanged()
    }

    override fun showEditAccountDialog(address: String) {
        (parentFragment as? TransactionDetailsFragment)?.showEditAccountDialog(address)
    }

    override fun showAssetInfoDialog(code: String, issuer: String?) {
        AssetInfoDialogFragment().apply {
            arguments = Bundle().apply {
                putString(Constant.Bundle.BUNDLE_ASSET_CODE, code)
                putString(Constant.Bundle.BUNDLE_ASSET_ISSUER, issuer)
            }
        }.show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.ASSET_INFO)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
