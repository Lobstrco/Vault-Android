package com.lobstr.stellar.vault.presentation.auth

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.container.fragment.ContainerFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

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
        changeActionBarIconVisibility(false)
    }

    override fun showAuthFragment() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, Constant.Navigation.AUTH)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(this.classLoader, ContainerFragment::class.qualifiedName!!)
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showBiometricSetUpFragment() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, Constant.Navigation.BIOMETRIC_SET_UP)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(this.classLoader, ContainerFragment::class.qualifiedName!!)
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
