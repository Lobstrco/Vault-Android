package com.lobstr.stellar.vault.presentation.home

import androidx.core.os.bundleOf
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

    private val mFragments = mutableListOf<Fragment>()

    // ===========================================================
    // Constructors
    // ===========================================================

    init {
        prepareHomeFragments()
    }

    private fun prepareHomeFragments() {
        (0 until VIEW_PAGER_COUNT).forEach { position ->
            mFragments.add(ContainerFragment().apply {
                arguments = bundleOf(
                    BUNDLE_NAVIGATION_FR to when (position) {
                        Position.DASHBOARD -> DASHBOARD
                        Position.TRANSACTIONS -> TRANSACTIONS
                        Position.SETTINGS -> SETTINGS
                        else -> DASHBOARD
                    }
                )
                // Used for setup default user visibility hint.
                setMenuVisibility(false)
            })
        }
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    override fun createFragment(position: Int): Fragment = mFragments[position]

    override fun containsItem(itemId: Long): Boolean =
        mFragments.any { it.hashCode().toLong() == itemId }

    override fun getItemCount() = mFragments.size

    override fun getItemId(position: Int): Long = mFragments[position].hashCode().toLong()

    // ===========================================================
    // Methods
    // ===========================================================

    fun getFragment(position: Int) = mFragments.getOrNull(position)

    fun update() {
        mFragments.clear()
        prepareHomeFragments()
        notifyDataSetChanged()
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}