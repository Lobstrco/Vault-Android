package com.lobstr.stellar.vault.presentation.home.dashboard.account

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentAccountsBinding
import com.lobstr.stellar.vault.presentation.BaseBottomSheetDialog
import com.lobstr.stellar.vault.presentation.entities.account.AccountDialogItem
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.home.dashboard.account.adapter.AccountsDialogAdapter
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class AccountsDialogFragment : BaseBottomSheetDialog(), AccountsView {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<AccountsPresenter>

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                BottomSheetBehavior.from(sheet).apply {
                    skipCollapsed = true
                    state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        binding.tvAccountsAdd.setSafeOnClickListener {
            mPresenter.addNewAccountClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun showAddAccountButton(visible: Boolean) {
        binding.tvAccountsAdd.isVisible = visible
    }

    override fun initList(list: List<AccountDialogItem>) {
        binding.rvAccounts.apply {
            isNestedScrollingEnabled = true
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AccountsDialogAdapter(list) { mPresenter.onItemClicked(it) }
        }
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun showErrorMessage(message: String?) {
        if (!message.isNullOrEmpty()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun notifyHomeActivity() {
        (requireActivity() as HomeActivity).accountWasChanged()
    }

    override fun dismissDialog() {
        dismiss()
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}