package com.lobstr.stellar.vault.presentation.home.container.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.fusechain.digitalbits.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.base.fragment.BaseContainerFragment
import com.lobstr.stellar.vault.presentation.home.dashboard.DashboardFragment
import com.lobstr.stellar.vault.presentation.home.settings.SettingsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.TransactionsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.details.TransactionDetailsFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_NAVIGATION_FR

class ContainerFragment : BaseContainerFragment(),
    ContainerView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ContainerFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: ContainerPresenter

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideContainerPresenter() =
        ContainerPresenter(
            arguments?.getInt(BUNDLE_NAVIGATION_FR)!!,
            arguments?.getParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM)
        )

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
        return inflater.inflate(R.layout.fragment_container, container, false)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun showDashBoardFr() {
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            Fragment.instantiate(context, DashboardFragment::class.java.name),
            R.id.fl_container,
            true
        )
    }

    override fun showSettingsFr() {
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            Fragment.instantiate(context, SettingsFragment::class.java.name),
            R.id.fl_container,
            true
        )
    }

    override fun showInfoFr() {
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            Fragment.instantiate(context, TransactionsFragment::class.java.name),
            R.id.fl_container,
            true
        )
    }

    override fun showTransactionsFr() {
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            Fragment.instantiate(context, TransactionsFragment::class.java.name),
            R.id.fl_container,
            true
        )
    }

    override fun showTransactionDetails(target: Fragment?, transactionItem: TransactionItem) {
        val bundle = Bundle()
        bundle.putParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM, transactionItem)

        val fragment = Fragment.instantiate(context, TransactionDetailsFragment::class.java.name, bundle)

        if (target != null) {
            fragment.setTargetFragment(target, Constant.Code.TRANSACTION_DETAILS_FRAGMENT)
        }

        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            fragment,
            R.id.fl_container,
            true
        )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
