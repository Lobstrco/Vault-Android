package com.lobstr.stellar.vault.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.home.HomeViewPagerAdapter.Position.DASHBOARD
import com.lobstr.stellar.vault.presentation.home.HomeViewPagerAdapter.Position.SETTINGS
import com.lobstr.stellar.vault.presentation.home.HomeViewPagerAdapter.Position.TRANSACTIONS
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), HomeActivityView,
    BottomNavigationView.OnNavigationItemSelectedListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = HomeActivity::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mHomePresenter: HomeActivityPresenter

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideHomeActivityPresenter() = HomeActivityPresenter()

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        bnvHomeTabs.setOnNavigationItemSelectedListener(this)
    }

    override fun getLayoutResource() = R.layout.activity_home

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupViewPager() {
        vpHome.offscreenPageLimit = 4
        vpHome.adapter = HomeViewPagerAdapter(this, supportFragmentManager)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.isChecked) {
            resetBackStack()
        }

        when (item.itemId) {
            R.id.action_dashboard -> vpHome.currentItem = DASHBOARD

            R.id.action_transactions -> vpHome.currentItem = TRANSACTIONS

            R.id.action_settings -> vpHome.currentItem = SETTINGS
        }

        return true
    }

    override fun initBottomNavigationView() {
        bnvHomeTabs.itemIconTintList = null
        bnvHomeTabs.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
    }

    override fun setupToolBarTitle(title: Int) {
        setActionBarTitle(title)
    }

    override fun showAuthScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onBackPressed() {
        try {
            checkBackPress(supportFragmentManager.fragments[vpHome.currentItem])
        } catch (exc: IndexOutOfBoundsException) {
            finish()
        }
    }

    override fun resetBackStack() {
        try {
            val currentContainer = supportFragmentManager.fragments[vpHome.currentItem]

            if (currentContainer == null) {
                return
            } else {
                val backStackCount = currentContainer.childFragmentManager.backStackEntryCount

                if (backStackCount > 1) {
                    val childFragmentManager = currentContainer.childFragmentManager

                    childFragmentManager.popBackStack(
                        childFragmentManager.getBackStackEntryAt(1).id,
                        POP_BACK_STACK_INCLUSIVE
                    )
                }
            }
        } catch (exc: IndexOutOfBoundsException) {
            exc.printStackTrace()
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
