package com.lobstr.stellar.vault.presentation.home.dashboard


import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.AccountAdapter
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.OnAccountItemListener
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account.EditAccountDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dash_board.*
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : BaseFragment(), DashboardView, View.OnClickListener,
    OnAccountItemListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = DashboardFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @Inject
    lateinit var daggerPresenter: Lazy<DashboardPresenter>

    private var mView: View? = null

    private var mRefreshAnimation: ObjectAnimator? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { daggerPresenter.get() }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        mPresenter.userVisibleHintCalled(menuVisible)
    }

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView =
            if (mView == null) inflater.inflate(
                R.layout.fragment_dash_board,
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
        tvDashboardShowList.setOnClickListener(this)
        tvDashboardCopyPublicKey.setOnClickListener(this)
        tvDashboardTransactionCount.setOnClickListener(this)
        tvDashboardSignersCount.setOnClickListener(this)
        tvEmptyStateAction.setOnClickListener(this)
        tvAddAccount.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard, menu)
        val refreshView = menu.findItem(R.id.action_refresh).actionView

        mRefreshAnimation = ObjectAnimator.ofFloat(refreshView, View.ROTATION, 0.0f, 360.0f)
        mRefreshAnimation?.duration = 800
        mRefreshAnimation?.repeatCount = 1
        mRefreshAnimation?.interpolator = LinearInterpolator()

        refreshView?.setOnClickListener {
            mPresenter.refreshClicked()
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                mPresenter.refreshClicked()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v?.id) {
            tvDashboardTransactionCount.id -> mPresenter.transactionCountClicked()
            tvDashboardShowList.id -> mPresenter.showTransactionListClicked()
            tvDashboardCopyPublicKey.id -> mPresenter.copyKeyClicked()
            tvDashboardSignersCount.id -> mPresenter.signersCountClicked()
            tvAddAccount.id -> mPresenter.addAccountClicked()
            tvEmptyStateAction.id -> mPresenter.addAccountClicked()
        }
    }

    override fun initSignedAccountsRecycledView() {
        rvSignedAccounts.layoutManager = LinearLayoutManager(activity)
        rvSignedAccounts.itemAnimator = null
        rvSignedAccounts.isNestedScrollingEnabled = false
        rvSignedAccounts.adapter = AccountAdapter(AccountAdapter.ACCOUNT, this)
    }

    override fun notifySignedAccountsAdapter(accounts: List<Account>) {
        (rvSignedAccounts.adapter as? AccountAdapter)?.setAccountList(accounts)
    }

    override fun onAccountItemClick(account: Account) {
        mPresenter.signedAccountItemClicked(account)
    }

    override fun onAccountItemLongClick(account: Account) {
        mPresenter.signedAccountItemLongClicked(account)
    }

    override fun showVaultInfo(hasTangem: Boolean, identityIconUrl: String, publicKey: String?) {
        ivSignerCard.visibility = if(hasTangem) View.VISIBLE else View.GONE
        flIdentityContainer.visibility = if(hasTangem) View.GONE else View.VISIBLE

        if(!hasTangem) {
            // Set user identity icon.
            Glide.with(requireContext())
                .load(identityIconUrl)
                .placeholder(R.drawable.ic_person)
                .into(ivIdentity)
        }

        tvDashboardPublicKey.text = publicKey
    }

    override fun showSignersCount(count: Int) {

        val message = String.format(
            getString(if (count == 1) R.string.text_settings_signer else R.string.text_settings_signers),
            count
        )
        val spannedText = SpannableString(message)
        val startPosition = message.indexOf(count.toString())
        val endPosition = startPosition + count.toString().length

        if (startPosition != Constant.Util.UNDEFINED_VALUE) {
            spannedText.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        this.requireContext(),
                        R.color.color_primary
                    )
                ),
                startPosition,
                endPosition,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannedText.setSpan(
                RelativeSizeSpan(1.5f),
                startPosition,
                endPosition,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannedText.setSpan(
                StyleSpan(Typeface.BOLD),
                startPosition,
                endPosition,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        tvDashboardSignersCount.text = spannedText
    }

    override fun showDashboardInfo(count: Int?) {
        // Nullable value mean undefined value.
        if (count != null) {
            tvDashboardTransactionCount.text = count.toString()
        }
        pbDashboardTransactions.visibility = View.GONE
        llDashboardTransactionToSignContainer.visibility = View.VISIBLE
    }

    override fun showSignersScreen() {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.SIGNED_ACCOUNTS)
        startActivity(intent)
    }

    override fun showSignerInfoScreen() {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.SIGNER_INFO)
        startActivity(intent)
    }

    override fun showErrorMessage(message: String) {
        if (message.isEmpty()) {
            return
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToTransactionList() {
        (activity as? HomeActivity)?.setSelectedBottomNavigationItem(R.id.action_transactions)
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

    override fun showSignersProgress(show: Boolean) {
        pbDashboardSigners.visibility = if (show) View.VISIBLE else View.GONE
        tvAddAccount.visibility = if (show) View.GONE else View.VISIBLE
        tvDashboardSignersCount.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun showSignersEmptyState(show: Boolean) {
        cvDashboardSignersInfo.visibility = if (show) View.GONE else View.VISIBLE
        cvDashboardSignersEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        svTransactions.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun showRefreshAnimation(show: Boolean) {
        if (show) {
            mRefreshAnimation?.start()
        } else {
            mRefreshAnimation?.end()
            mRefreshAnimation?.cancel()
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
