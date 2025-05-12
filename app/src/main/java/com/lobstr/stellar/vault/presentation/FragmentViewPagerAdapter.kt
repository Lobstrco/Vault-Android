package com.lobstr.stellar.vault.presentation

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

@SuppressLint("WrongConstant")
class FragmentViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {
    private val mFragments = mutableListOf<Fragment>()
    private val mFragmentTitles = mutableListOf<String?>()

    val fragments: List<Fragment>
        get() = mFragments

    fun addFragments(fragments: List<Fragment>, titles: List<String?>) {
        mFragments.addAll(fragments)
        mFragmentTitles.addAll(titles)
    }

    fun addFragments(fragments: List<Fragment>) {
        mFragments.addAll(fragments)
    }

    fun addTitles(titles: List<String?>) {
        mFragmentTitles.addAll(titles)
    }

    fun addFragment(fragment: Fragment, title: String?) {
        mFragments.add(fragment)
        mFragmentTitles.add(title)
    }

    fun addFragment(fragment: Fragment) {
        mFragments.add(fragment)
    }

    fun addTitle(title: String?) {
        mFragmentTitles.add(title)
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = mFragments[position]
        fragment.setMenuVisibility(false)
        return fragment
    }

    override fun getItemCount(): Int {
        return mFragments.size
    }

    fun getPageTitle(position: Int): CharSequence? {
        return if (position < mFragmentTitles.size) mFragmentTitles[position] else null
    }
}