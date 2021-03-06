package com.lobstr.stellar.vault.presentation.home

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.ActivityHomeBinding
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.home.HomeViewPagerAdapter.Position.DASHBOARD
import com.lobstr.stellar.vault.presentation.home.HomeViewPagerAdapter.Position.SETTINGS
import com.lobstr.stellar.vault.presentation.home.HomeViewPagerAdapter.Position.TRANSACTIONS
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

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

    private lateinit var binding: ActivityHomeBinding

    @Inject
    lateinit var homePresenterProvider: Provider<HomeActivityPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mHomePresenter by moxyPresenter { homePresenterProvider.get() }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setListeners()

        onBackPressedDispatcher.addCallback(this) {
            try {
                checkBackPress(supportFragmentManager.fragments[binding.vpHome.currentItem])
            } catch (exc: IndexOutOfBoundsException) {
                super.onBackPressed()
            }
        }
    }

    override fun getContentView(): View {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun setListeners() {
        binding.bnvHomeTabs.setOnNavigationItemSelectedListener(this)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupToolbar(upArrow: Int, upArrowColor: Int) {
        setActionBarIcon(upArrow, upArrowColor)
        changeActionBarIconVisibility(false)
    }

    override fun setupViewPager() {
        binding.vpHome.offscreenPageLimit = 4
        binding.vpHome.isUserInputEnabled = false
        binding.vpHome.adapter = HomeViewPagerAdapter(supportFragmentManager, lifecycle)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.isChecked) {
            resetBackStack()
        }

        when (item.itemId) {
            R.id.action_dashboard -> binding.vpHome.setCurrentItem(DASHBOARD, false)

            R.id.action_transactions -> binding.vpHome.setCurrentItem(TRANSACTIONS, false)

            R.id.action_settings -> binding.vpHome.setCurrentItem(SETTINGS, false)
        }

        return true
    }

    override fun initBottomNavigationView() {
        binding.bnvHomeTabs.itemIconTintList = null
    }

    override fun setSelectedBottomNavigationItem(@IdRes itemId: Int) {
        binding.bnvHomeTabs.selectedItemId = itemId
    }

    override fun resetBackStack() {
        try {
            val currentContainer = supportFragmentManager.fragments[binding.vpHome.currentItem]

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

    override fun suggestRateUsDialog() {
        AlertDialogFragment.Builder(false)
            .setSpecificDialog(AlertDialogFragment.DialogIdentifier.SUGGEST_RATE_US)
            .setCancelable(true)
            .setMessage(R.string.msg_suggest_rate_us)
            .setNegativeBtnText(R.string.text_btn_cancel_suggestion_rate_us)
            .setPositiveBtnText(R.string.text_btn_suggest_rate_us)
            .create()
            .show(supportFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.SUGGEST_RATE_US)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
