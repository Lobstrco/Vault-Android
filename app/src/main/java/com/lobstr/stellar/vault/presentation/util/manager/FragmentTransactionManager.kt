package com.fusechain.digitalbits.util.manager

import androidx.annotation.AnimRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


object FragmentTransactionManager {

    fun displayFragment(fragmentManager: FragmentManager, fragment: Fragment, view: Int, mustAddToBackStack: Boolean) {
        if (mustAddToBackStack) {
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(view, fragment, fragment.javaClass.simpleName)
                    .commitAllowingStateLoss()
        } else {
            fragmentManager.beginTransaction()
                    .replace(view, fragment, fragment.javaClass.simpleName)
                    .commitAllowingStateLoss()
        }
    }

    fun displayFragmentWithTransactionAnim(fragmentManager: FragmentManager, fragment: Fragment,
                                           view: Int, @AnimRes start: Int, @AnimRes exit: Int, mustAddToBackStack: Boolean) {
        if (mustAddToBackStack) {
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .setCustomAnimations(start, exit)
                    .replace(view, fragment, fragment.javaClass.simpleName)
                    .commitAllowingStateLoss()
        } else {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(start, exit)
                    .replace(view, fragment, fragment.javaClass.simpleName)
                    .commitAllowingStateLoss()
        }
    }
}