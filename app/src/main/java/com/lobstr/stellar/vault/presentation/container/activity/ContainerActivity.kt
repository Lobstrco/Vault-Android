package com.lobstr.stellar.vault.presentation.container.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.fusechain.digitalbits.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.container.fragment.ContainerFragment
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Extra.EXTRA_NAVIGATION_FR
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.DASHBOARD
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SETTINGS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.TRANSACTION_DETAILS

/**
 * Used for show separate activity with fragments container
 * @see ContainerFragment
 */
class ContainerActivity : BaseActivity(), ContainerView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ContainerActivity::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mContainerPresenter: ContainerPresenter

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * Pass EXTRA_NAVIGATION_FR for navigate fragments container
     * @see Constant.Navigation
     * @see ContainerFragment
     *
     * For additional data use other Extras and pass it in fragments container
     * Example:
     * @see Constant.Bundle.BUNDLE_TRANSACTION_ITEM
     */
    @ProvidePresenter
    fun provideContainerPresenter() = ContainerPresenter(
        intent?.getIntExtra(EXTRA_NAVIGATION_FR, DASHBOARD)!!,
        intent?.getParcelableExtra(Constant.Bundle.BUNDLE_TRANSACTION_ITEM)

    )

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun getLayoutResource() = R.layout.activity_container

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun showTransactionDetails(transactionItem: TransactionItem) {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, TRANSACTION_DETAILS)
        bundle.putParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM, transactionItem)

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            Fragment.instantiate(this, ContainerFragment::class.java.name, bundle),
            R.id.fl_container,
            true
        )
    }

    override fun showDashBoardFr() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, DASHBOARD)

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            Fragment.instantiate(this, ContainerFragment::class.java.name, bundle),
            R.id.fl_container,
            true
        )
    }

    override fun showSettingsFr() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, SETTINGS)

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            Fragment.instantiate(this, ContainerFragment::class.java.name, bundle),
            R.id.fl_container,
            true
        )
    }

    override fun showTransactionsFr() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, TRANSACTION_DETAILS)

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            Fragment.instantiate(this, ContainerFragment::class.java.name, bundle),
            R.id.fl_container,
            true
        )
    }

    override fun showMnemonicsFr() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, Constant.Navigation.MNEMONICS)

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            Fragment.instantiate(this, ContainerFragment::class.java.name, bundle),
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
