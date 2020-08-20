package com.lobstr.stellar.vault.presentation.auth

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.container.fragment.ContainerFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import moxy.ktx.moxyPresenter

class AuthActivity : BaseActivity(), AuthView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = AuthActivity::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mAuthPresenter by moxyPresenter { AuthPresenter() }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun getLayoutResource(): Int {
        return R.layout.activity_auth
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setActionBarIconVisibility(visible: Boolean) {
        changeActionBarIconVisibility(visible)
    }

    override fun setupToolbarColor(@ColorRes color: Int) {
        setActionBarBackground(color)
    }

    override fun setupToolbarUpArrow(@DrawableRes arrow: Int, @ColorRes color: Int) {
        setHomeAsUpIndicator(arrow, color)
    }

    override fun setupToolbarTitleColor(@ColorRes color: Int) {
        setActionBarTitleColor(color)
    }

    /**
     * Used for setup toolbar from child fragments.
     */
    override fun updateToolbar(
        @ColorRes toolbarColor: Int?,
        @DrawableRes upArrow: Int?,
        @ColorRes upArrowColor: Int?,
        @ColorRes titleColor: Int?
    ) {
        mAuthPresenter.updateToolbar(toolbarColor, upArrow, upArrowColor, titleColor)
    }

    override fun showAuthFragment() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, Constant.Navigation.AUTH)
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
