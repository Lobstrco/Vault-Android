package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lobstr.stellar.vault.databinding.FragmentSignedAccountsBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.AccountAdapter
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.AccountAdapter.Companion.ACCOUNT_EXTENDED
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account.EditAccountDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class SignedAccountsFragment : BaseFragment(), SignedAccountsView, EditAccountDialogFragment.OnEditAccountDialogListener,
    SwipeRefreshLayout.OnRefreshListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = SignedAccountsFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentSignedAccountsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<SignedAccountsPresenter>

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
        _binding = FragmentSignedAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        binding.srlSignedAccounts.setOnRefreshListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onRefresh() {
        mPresenter.onRefreshCalled()
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initRecycledView() {
        binding.rvSignedAccounts.layoutManager = LinearLayoutManager(activity)
        binding.rvSignedAccounts.itemAnimator = null
        binding.rvSignedAccounts.adapter = AccountAdapter(ACCOUNT_EXTENDED,
            { mPresenter.signedAccountItemClicked(it) },
            { mPresenter.signedAccountItemLongClicked(it) })
    }

    override fun showProgress(show: Boolean) {
        binding.srlSignedAccounts.isRefreshing = show
    }

    override fun showEmptyState(show: Boolean) {
        binding.tvSignedAccountsEmptyState.isVisible = show
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun notifyAdapter(accounts: List<Account>) {
        (binding.rvSignedAccounts.adapter as? AccountAdapter)?.setAccountList(accounts)
    }

    override fun scrollListToPosition(position: Int) {
        binding.rvSignedAccounts.scrollToPosition(position)
    }

    override fun showEditAccountDialog(address: String) {
        EditAccountDialogFragment().apply {
            arguments = bundleOf(
                Constant.Bundle.BUNDLE_PUBLIC_KEY to address,
                Constant.Bundle.BUNDLE_MANAGE_ACCOUNT_NAME to true
            )
        }.show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.EDIT_ACCOUNT)
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text)
    }

    override fun onSetAccountNickNameClicked(publicKey: String) {
        AlertDialogFragment.Builder(true)
            .setSpecificDialog(AlertDialogFragment.DialogIdentifier.ACCOUNT_NAME, Bundle().apply {
                putString(Constant.Bundle.BUNDLE_PUBLIC_KEY, publicKey)
            })
            .create()
            .show(
                childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.ACCOUNT_NAME
            )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
