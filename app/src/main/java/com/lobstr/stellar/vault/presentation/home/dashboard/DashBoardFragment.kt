package com.lobstr.stellar.vault.presentation.home.dashboard


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import kotlinx.android.synthetic.main.fragment_dash_board.*

class DashboardFragment : BaseFragment(), DashboardView, View.OnClickListener {

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

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        mPresenter.userVisibleHintCalled(isVisibleToUser)
    }

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView =
            if (mView == null) inflater.inflate(R.layout.fragment_dash_board, container, false) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        tvDashboardShowList.setOnClickListener(this)
        tvDashboardCopySigner.setOnClickListener(this)
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
            tvDashboardCopySigner.id -> mPresenter.copySignerClicked()
            tvDashboardSignersCount.id -> mPresenter.signersCountClicked()
        }
    }

    override fun showPublicKey(publicKey: String) {
        tvDashboardPublicKey.text = publicKey
    }

    override fun showSignersPublickKey(info: String) {
        tvDashboardSigners.text = info
        tvDashboardSigners.visibility = View.VISIBLE
        tvDashboardCopySigner.visibility = View.VISIBLE
        tvDashboardSignersCount.visibility = View.GONE
        tvDashboardSignerDescription.visibility = View.GONE
    }

    override fun showSignersCount(count: Int) {
        tvDashboardSignersCount.text = count.toString()
        tvDashboardSigners.visibility = View.GONE
        tvDashboardCopySigner.visibility = View.GONE
        tvDashboardSignersCount.visibility = View.VISIBLE
        tvDashboardSignerDescription.visibility = View.VISIBLE
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

    override fun hideSignersProgress() {
        pbDashboardSigners.visibility = View.GONE
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
