package com.lobstr.stellar.vault.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lobstr.stellar.vault.presentation.container.fragment.ContainerFragment
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_NAVIGATION_FR
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.DASHBOARD
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SETTINGS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.TRANSACTIONS


class HomeViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        const val VIEW_PAGER_COUNT = 3
    }

    object Position {
        const val DASHBOARD = 0
        const val TRANSACTIONS = 1
        const val SETTINGS = 2
    }

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()

        when (position) {
            Position.DASHBOARD -> {
                bundle.putInt(BUNDLE_NAVIGATION_FR, DASHBOARD)
            }

            Position.TRANSACTIONS -> {
                bundle.putInt(BUNDLE_NAVIGATION_FR, TRANSACTIONS)
            }

            Position.SETTINGS -> {
                bundle.putInt(BUNDLE_NAVIGATION_FR, SETTINGS)
            }
        }

        val fragment = ContainerFragment()
        fragment.arguments = bundle
        // Used for setup default user visibility hint.
        fragment.setMenuVisibility(false)
        return fragment
    }

    override fun getItemCount() = VIEW_PAGER_COUNT

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}