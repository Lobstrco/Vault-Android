package com.lobstr.stellar.vault.presentation.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.lobstr.stellar.vault.presentation.home.container.fragment.ContainerFragment
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_NAVIGATION_FR
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.DASHBOARD
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SETTINGS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.TRANSACTIONS


class HomeViewPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        const val VIEW_PAGER_COUNT = 3
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

    override fun getItem(position: Int): Fragment? {
        val bundle = Bundle()

        when (position) {
            DASHBOARD -> {
                bundle.putInt(BUNDLE_NAVIGATION_FR, DASHBOARD)
            }

            TRANSACTIONS -> {
                bundle.putInt(BUNDLE_NAVIGATION_FR, TRANSACTIONS)
            }

            SETTINGS -> {
                bundle.putInt(BUNDLE_NAVIGATION_FR, SETTINGS)
            }
        }

        return Fragment.instantiate(context, ContainerFragment::class.java.name, bundle)
    }


    override fun getCount() = VIEW_PAGER_COUNT

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}