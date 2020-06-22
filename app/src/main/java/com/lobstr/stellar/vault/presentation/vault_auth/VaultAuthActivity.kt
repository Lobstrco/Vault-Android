package com.lobstr.stellar.vault.presentation.vault_auth

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

class VaultAuthActivity : BaseActivity(), VaultAuthView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = VaultAuthActivity::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mVaultAuthPresenter: VaultAuthPresenter

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideVaultAuthPresenter() = VaultAuthPresenter()

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun getLayoutResource(): Int {
        return R.layout.activity_vault_auth
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

    override fun showAuthTokenFragment() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, Constant.Navigation.VAULT_AUTH)
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
