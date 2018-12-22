package com.lobstr.stellar.vault.presentation.home.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.home.base.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_dash_board.*

class DashboardFragment : BaseFragment(), DashboardView, SwipeRefreshLayout.OnRefreshListener {

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
        srlDashboardUpdate.setOnRefreshListener(this)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onRefresh() {
        mPresenter.refreshCalled()
    }

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun showDashboardInfo(count: Int) {
        tvDashboardTransactionCount.text = count.toString()
    }

    override fun showProgress() {
        srlDashboardUpdate.isRefreshing = true
    }

    override fun hideProgress() {
        srlDashboardUpdate.isRefreshing = false
    }

    override fun showErrorMessage(message: String) {
        if (message.isEmpty()) {
            return
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
