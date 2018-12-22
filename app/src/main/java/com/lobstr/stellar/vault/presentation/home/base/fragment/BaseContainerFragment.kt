package com.lobstr.stellar.vault.presentation.home.base.fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatFragment
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.home.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.home.container.activity.ContainerActivity


abstract class BaseContainerFragment : BaseMvpAppCompatFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                when {
                    childFragmentManager.backStackEntryCount == 1 -> activity?.finish()
                    else -> childFragmentManager.popBackStack()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setListeners() {
        childFragmentManager.addOnBackStackChangedListener {
            checkBackStack()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        setHasOptionsMenu(isVisibleToUser)

        if (isAdded) {
            val fragment = childFragmentManager.findFragmentById(R.id.fl_container)
            fragment?.userVisibleHint = isVisibleToUser
        }

        if (!isVisibleToUser) {
            return
        }

        checkBackStack()
    }

    private fun checkBackStack() {
        if (!isAdded) {
            return
        }

        if (childFragmentManager.backStackEntryCount == 0) {
            (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(false)
            return
        }

        if (childFragmentManager.backStackEntryCount == 1) {
            when (activity) {
                is HomeActivity -> (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(false)
                is ContainerActivity -> (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(true)
            }
        } else {
            (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(true)
        }
    }
}