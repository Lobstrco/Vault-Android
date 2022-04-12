package com.lobstr.stellar.vault.presentation.home.transactions.import_xdr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentImportXdrBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.transactions.details.TransactionDetailsFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class ImportXdrFragment : BaseFragment(), ImportXdrView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ImportXdrFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentImportXdrBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<ImportXdrPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { presenterProvider.get() }

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
        _binding = FragmentImportXdrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        lifecycle.addObserver(binding.clipboardView)
        binding.clipboardView.setClickListener {
            binding.etImportXdr.setText(it)
        }
        binding.btnNext.setSafeOnClickListener {
            AppUtil.closeKeyboard(activity)
            mPresenter.nextClicked(binding.etImportXdr.text.toString().trim())
        }
        binding.etImportXdr.doAfterTextChanged { editable ->
            val text = editable?.trim()?.toString()
            val clipBoardData = binding.clipboardView.data
            clipBoardData?.let { binding.clipboardView.updateInUseData(text) }
            binding.clipboardView.isVisible = if(clipBoardData.isNullOrEmpty()) false else clipBoardData != text
            mPresenter.xdrChanged(editable?.trim()?.length ?: 0)
        }
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

    override fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showTransactionDetails(transactionItem: TransactionItem) {
        val bundle = Bundle()
        bundle.putParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM, transactionItem)
        val fragment = requireParentFragment().childFragmentManager.fragmentFactory.instantiate(requireContext().classLoader, TransactionDetailsFragment::class.qualifiedName!!)
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            fragment,
            R.id.flContainer
        )
    }

    override fun setSubmitEnabled(enabled: Boolean) {
        binding.btnNext.isEnabled = enabled
    }

    override fun showFormError(show: Boolean, error: String?) {
        binding.tvError.text = error
        binding.etImportXdr.setBackgroundResource(if (show) R.drawable.shape_input_error_edit_text else R.drawable.shape_input_edit_text)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}