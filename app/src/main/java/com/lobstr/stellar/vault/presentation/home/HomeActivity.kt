package com.lobstr.stellar.vault.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.home.HomeViewPagerAdapter.Position.DASHBOARD
import com.lobstr.stellar.vault.presentation.home.HomeViewPagerAdapter.Position.SETTINGS
import com.lobstr.stellar.vault.presentation.home.HomeViewPagerAdapter.Position.TRANSACTIONS
import com.lobstr.stellar.vault.presentation.util.Constant
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
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

    @Inject
    lateinit var homeDaggerPresenter: Lazy<HomeActivityPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mHomePresenter by moxyPresenter { homeDaggerPresenter.get() }

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

    override fun setupToolbar() {
        changeActionBarIconVisibility(false)
    }

    override fun setupViewPager() {
        vpHome.offscreenPageLimit = 4
        vpHome.isUserInputEnabled = false
        vpHome.adapter = HomeViewPagerAdapter(supportFragmentManager, lifecycle)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.isChecked) {
            resetBackStack()
        }

        when (item.itemId) {
            R.id.action_dashboard -> vpHome.setCurrentItem(DASHBOARD, false)

            R.id.action_transactions -> vpHome.setCurrentItem(TRANSACTIONS, false)

            R.id.action_settings -> vpHome.setCurrentItem(SETTINGS, false)
        }

        return true
    }

    override fun initBottomNavigationView() {
        bnvHomeTabs.itemIconTintList = null
        bnvHomeTabs.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
    }

    override fun showAuthScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.AUTH)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun setSelectedBottomNavigationItem(@IdRes itemId: Int) {
        bnvHomeTabs.selectedItemId = itemId
    }

    override fun onBackPressed() {
        try {
            checkBackPress(supportFragmentManager.fragments[vpHome.currentItem])
        } catch (exc: IndexOutOfBoundsException) {
            super.onBackPressed()
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

    override fun checkRateUsDialog() {
        mvpDelegate.onAttach()
        mHomePresenter.checkRateUsDialog()
    }

    override fun showRateUsDialog() {
        AlertDialogFragment.Builder(false)
            .setCancelable(true)
            .setSpecificDialog(AlertDialogFragment.DialogIdentifier.RATE_US, null)
            .setTitle(R.string.title_rate_us_dialog)
            .setMessage(R.string.msg_rate_us_dialog)
            .setNegativeBtnText(R.string.text_btn_rate_never)
            .setNeutralBtnText(R.string.text_btn_rate_later)
            .setPositiveBtnText(R.string.text_btn_rate_us)
            .create()
            .show(supportFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.RATE_US)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
