package com.lobstr.stellar.vault.presentation.container.activity

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.os.bundleOf
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.ActivityContainerBinding
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.container.fragment.ContainerFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Extra.EXTRA_NAVIGATION_FR
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.DASHBOARD
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionConfirmationSuccessStatus.SUCCESS
import com.lobstr.stellar.vault.presentation.util.Constant.Util.UNDEFINED_VALUE
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.parcelable
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
    private val mContainerPresenter by moxyPresenter {
        ContainerPresenter(
            intent?.getIntExtra(EXTRA_NAVIGATION_FR, DASHBOARD)!!,
            intent?.parcelable(Constant.Extra.EXTRA_TRANSACTION_ITEM),
            intent?.getStringExtra(Constant.Extra.EXTRA_ENVELOPE_XDR),
            intent?.getByteExtra(
                Constant.Extra.EXTRA_TRANSACTION_CONFIRMATION_SUCCESS_STATUS,
                SUCCESS
            ),
            intent?.getParcelableExtra(Constant.Extra.EXTRA_ERROR),
            intent?.getIntExtra(Constant.Extra.EXTRA_CONFIG, UNDEFINED_VALUE) ?: UNDEFINED_VALUE
        )
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun getContentView(): View = ActivityContainerBinding.inflate(layoutInflater).root

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupToolbar(
        @ColorRes toolbarColor: Int, @DrawableRes upArrow: Int,
        @ColorRes upArrowColor: Int, @ColorRes titleColor: Int
    ) {
        setActionBarBackground(toolbarColor)
        setActionBarIcon(upArrow, upArrowColor)
        setActionBarTitleColor(titleColor)
        changeActionBarIconVisibility(true)
    }

    override fun showContainerFr(vararg args: Pair<String, Any?>) {
        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            supportFragmentManager.fragmentFactory.instantiate(
                this.classLoader,
                ContainerFragment::class.qualifiedName!!
            ).apply {
                arguments = bundleOf(*args)
            },
            R.id.flContainer
        )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
