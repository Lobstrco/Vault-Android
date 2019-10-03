package com.lobstr.stellar.vault.presentation.home.dashboard


import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.AccountAdapter
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.OnAccountItemListener
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import kotlinx.android.synthetic.main.fragment_dash_board.*

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

    @InjectPresenter
    lateinit var mPresenter: DashboardPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideDashBoardPresenter() = DashboardPresenter()

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
        }
    }

    override fun initSignedAccountsRecycledView() {
        rvSignedAccounts.layoutManager = LinearLayoutManager(activity)
        rvSignedAccounts.itemAnimator = null
        rvSignedAccounts.isNestedScrollingEnabled = false
        rvSignedAccounts.adapter = AccountAdapter(AccountAdapter.ACCOUNT, this)
    }

    override fun notifySignedAccountsAdapter(accounts: List<Account>) {
        (rvSignedAccounts.adapter as AccountAdapter).setAccountList(accounts)
    }

    override fun onAccountItemClick(account: Account) {
        // implement logic if need
    }

    override fun onAccountItemLongClick(account: Account) {
        mPresenter.copySignedAccountClicked(account)
    }

    override fun showPublicKey(publicKey: String) {
        // set user icon
        Glide.with(context!!)
            .load(
                Constant.Social.USER_ICON_LINK
                    .plus(publicKey)
                    .plus(".png")
            )
            .placeholder(R.drawable.ic_person)
            .into(ivIdentity)

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

        spannedText.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this.context!!, R.color.color_primary)),
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

        tvDashboardSignersCount.text = spannedText

        tvDashboardSignersCount.visibility = View.VISIBLE
    }

    override fun showDashboardInfo(count: Int) {
        tvDashboardTransactionCount.text = count.toString()
        pbDashboardTransactions.visibility = View.GONE
    }

    override fun showSignersScreen() {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.SIGNED_ACCOUNTS)
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

    override fun copyData(publicKey: String) {
        AppUtil.copyToClipboard(context, publicKey)
    }

    override fun showSignersProgress(show: Boolean) {
        pbDashboardSigners.visibility = if (show) View.VISIBLE else View.GONE
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
