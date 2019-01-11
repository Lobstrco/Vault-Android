package com.lobstr.stellar.vault.presentation.auth

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.fusechain.digitalbits.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.container.fragment.ContainerFragment
import com.lobstr.stellar.vault.presentation.util.Constant

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

    @InjectPresenter
    lateinit var mAuthPresenter: AuthPresenter

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideAuthActivityPresenter() = AuthPresenter(
        intent?.getIntExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.AUTH)!!
    )

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

    override fun setupToolbar(@ColorRes toolbarColor: Int, @DrawableRes upArrow: Int, @ColorRes upArrowColor: Int) {
        setActionBarBackground(toolbarColor)
        setHomeAsUpIndicator(upArrow, upArrowColor)
        setActionBarTitleColor(upArrowColor)
    }

    override fun showAuthFragment() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, Constant.Navigation.AUTH)

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            Fragment.instantiate(this, ContainerFragment::class.java.name, bundle),
            R.id.fl_container,
            true
        )
    }

    override fun showFingerprintSetUpFragment() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, Constant.Navigation.FINGERPRINT_SET_UP)

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
