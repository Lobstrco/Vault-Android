package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.AccountAdapter
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.AccountAdapter.Companion.ACCOUNT_EXTENDED
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.OnAccountItemListener
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account.EditAccountDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import kotlinx.android.synthetic.main.fragment_signed_accounts.*
import moxy.presenter.InjectPresenter

class SignedAccountsFragment : BaseFragment(), SignedAccountsView,
    SwipeRefreshLayout.OnRefreshListener,
    OnAccountItemListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = SignedAccountsFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: SignedAccountsPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

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
        mView = if (mView == null) inflater.inflate(
            R.layout.fragment_signed_accounts,
            container,
            false
        ) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        srlSignedAccounts.setOnRefreshListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // save RecycleView position for restore it after
        mPresenter.onSaveInstanceState(
            (rvSignedAccounts?.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                ?: 0
        )
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onRefresh() {
        mPresenter.onRefreshCalled()
    }

    override fun onAccountItemClick(account: Account) {
        mPresenter.signedAccountItemClicked(account)
    }

    override fun onAccountItemLongClick(account: Account) {
        mPresenter.signedAccountItemLongClicked(account)
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initRecycledView() {
        rvSignedAccounts.layoutManager = LinearLayoutManager(activity)
        rvSignedAccounts.itemAnimator = null
        rvSignedAccounts.adapter = AccountAdapter(ACCOUNT_EXTENDED, this)
    }

    override fun showProgress(show: Boolean) {
        srlSignedAccounts.isRefreshing = show
    }

    override fun showEmptyState(show: Boolean) {
        tvSignedAccountsEmptyState.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun notifyAdapter(accounts: List<Account>) {
        (rvSignedAccounts.adapter as? AccountAdapter)?.setAccountList(accounts)

        mPresenter.attemptRestoreRvPosition()
    }

    override fun scrollListToPosition(position: Int) {
        rvSignedAccounts.scrollToPosition(position)
    }

    override fun showEditAccountDialog(address: String) {
        val bundle = Bundle()
        bundle.putString(Constant.Bundle.BUNDLE_PUBLIC_KEY, address)

        val dialog = EditAccountDialogFragment()
        dialog.arguments = bundle
        dialog.show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.EDIT_ACCOUNT)
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
