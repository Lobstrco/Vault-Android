package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.OnSignedAcoountItemClicked
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.SignedAccountAdapter
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account.EditAccountDialogFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import kotlinx.android.synthetic.main.fragment_signed_accounts.*

class SignedAccountsFragment : BaseFragment(), SignedAccountsView, SwipeRefreshLayout.OnRefreshListener,
    OnSignedAcoountItemClicked {

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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_signed_accounts, container, false) else mView
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
            (rvSignedAccounts?.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition() ?: 0
        )
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onRefresh() {
        mPresenter.onRefreshCalled()
    }

    override fun onSignedAccountItemClick(account: Account) {
        mPresenter.onSignedAccountItemClicked(account)
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initRecycledView() {
        rvSignedAccounts.layoutManager = LinearLayoutManager(activity)
        rvSignedAccounts.itemAnimator = null
        rvSignedAccounts.adapter = SignedAccountAdapter(this)
    }

    override fun showProgress() {
        srlSignedAccounts.isRefreshing = true
    }

    override fun hideProgress() {
        srlSignedAccounts.isRefreshing = false
    }

    override fun showEmptyState() {
        tvSignedAccountsEmptyState.visibility = View.VISIBLE
    }

    override fun hideEmptyState() {
        tvSignedAccountsEmptyState.visibility = View.GONE
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun notifyAdapter(accounts: List<Account>) {
        (rvSignedAccounts.adapter as SignedAccountAdapter).setAccountList(accounts)

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

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
