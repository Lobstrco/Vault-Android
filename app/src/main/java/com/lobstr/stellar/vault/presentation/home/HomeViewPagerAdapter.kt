package com.lobstr.stellar.vault.presentation.home

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.lobstr.stellar.vault.presentation.home.dashboard.container.DashBoardContainerFragment
import com.lobstr.stellar.vault.presentation.home.settings.container.SettingsContainerFragment
import com.lobstr.stellar.vault.presentation.home.transactions.container.TransactionsContainerFragment


class HomeViewPagerAdapter(private val mContext: Context, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        const val DASHBOARD = 0
        const val TRANSACTIONS = 1
        const val SETTINGS = 2

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

    override fun getItem(position: Int) =
            when (position) {
                DASHBOARD -> Fragment.instantiate(mContext, DashBoardContainerFragment::class.java.name)

                TRANSACTIONS -> Fragment.instantiate(mContext, TransactionsContainerFragment::class.java.name)

                SETTINGS -> Fragment.instantiate(mContext, SettingsContainerFragment::class.java.name)

                else -> null
            }

    override fun getCount() = VIEW_PAGER_COUNT

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}