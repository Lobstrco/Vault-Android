package com.lobstr.stellar.vault.presentation.vault_auth

import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.ActivityVaultAuthBinding
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.container.fragment.ContainerFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import moxy.ktx.moxyPresenter

class VaultAuthActivity : BaseActivity(), VaultAuthView {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mVaultAuthPresenter by moxyPresenter { VaultAuthPresenter() }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun getContentView(): View = ActivityVaultAuthBinding.inflate(layoutInflater).root

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupToolbar(@ColorRes toolbarColor: Int, @DrawableRes upArrow: Int, @ColorRes upArrowColor: Int) {
        setActionBarBackground(toolbarColor)
        setActionBarIcon(upArrow, upArrowColor)
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
