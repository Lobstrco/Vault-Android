package com.lobstr.stellar.vault.presentation.container.activity

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.container.fragment.ContainerFragment
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Extra.EXTRA_NAVIGATION_FR
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.DASHBOARD
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.IMPORT_XDR
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SETTINGS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SIGNER_INFO
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.TRANSACTION_DETAILS
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionConfirmationSuccessStatus.SUCCESS
import com.lobstr.stellar.vault.presentation.util.Constant.Util.UNDEFINED_VALUE
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import moxy.ktx.moxyPresenter

/**
 * Used for show separate activity with fragments container.
 * Associated with [com.lobstr.stellar.vault.presentation.home.HomeActivity].
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

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * Pass EXTRA_NAVIGATION_FR for navigate fragments container.
     * @see Constant.Navigation
     * @see ContainerFragment
     *
     * For additional data use other Extras and pass it in fragments container
     * Example:
     * @see Constant.Bundle.BUNDLE_TRANSACTION_ITEM
     */
    private val mContainerPresenter by moxyPresenter { ContainerPresenter(
        intent?.getIntExtra(EXTRA_NAVIGATION_FR, DASHBOARD)!!,
        intent?.getParcelableExtra(Constant.Extra.EXTRA_TRANSACTION_ITEM),
        intent?.getStringExtra(Constant.Extra.EXTRA_ENVELOPE_XDR),
        intent?.getByteExtra(Constant.Extra.EXTRA_TRANSACTION_CONFIRMATION_SUCCESS_STATUS, SUCCESS),
        intent?.getStringExtra(Constant.Extra.EXTRA_ERROR_MESSAGE),
        intent?.getIntExtra(Constant.Extra.EXTRA_CONFIG, UNDEFINED_VALUE) ?: UNDEFINED_VALUE
    ) }

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

    override fun setupToolbar(
        @ColorRes toolbarColor: Int, @DrawableRes upArrow: Int,
        @ColorRes upArrowColor: Int, @ColorRes titleColor: Int
    ) {
        setActionBarBackground(toolbarColor)
        setHomeAsUpIndicator(upArrow, upArrowColor)
        setActionBarTitleColor(titleColor)
        changeActionBarIconVisibility(true)
    }

    override fun showTransactionDetails(transactionItem: TransactionItem) {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, TRANSACTION_DETAILS)
        bundle.putParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM, transactionItem)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            this.classLoader,
            ContainerFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showImportXdrFr() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, IMPORT_XDR)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            this.classLoader,
            ContainerFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showSignerInfoFr() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, SIGNER_INFO)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            this.classLoader,
            ContainerFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showDashBoardFr() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, DASHBOARD)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            this.classLoader,
            ContainerFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showSettingsFr() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, SETTINGS)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            this.classLoader,
            ContainerFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showTransactionsFr() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, TRANSACTION_DETAILS)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            this.classLoader,
            ContainerFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showMnemonicsFr() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, Constant.Navigation.MNEMONICS)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            this.classLoader,
            ContainerFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showSuccessFr(envelopeXdr: String, transactionSuccessStatus: Byte) {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, Constant.Navigation.SUCCESS)
        bundle.putString(Constant.Bundle.BUNDLE_ENVELOPE_XDR, envelopeXdr)
        bundle.putByte(
            Constant.Bundle.BUNDLE_TRANSACTION_CONFIRMATION_SUCCESS_STATUS,
            transactionSuccessStatus
        )
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            this.classLoader,
            ContainerFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showErrorFr(errorMessage: String) {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, Constant.Navigation.ERROR)
        bundle.putString(Constant.Bundle.BUNDLE_ERROR_MESSAGE, errorMessage)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            this.classLoader,
            ContainerFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showSignedAccountsFr() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, Constant.Navigation.SIGNED_ACCOUNTS)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            this.classLoader,
            ContainerFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showConfigFr(config: Int) {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, Constant.Navigation.CONFIG)
        bundle.putInt(Constant.Bundle.BUNDLE_CONFIG, config)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            this.classLoader,
            ContainerFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
