package com.lobstr.stellar.vault.presentation.base.fragment

import android.os.Bundle
import android.view.View
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatFragment
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity


abstract class BaseContainerFragment : BaseMvpAppCompatFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
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

        AppUtil.closeKeyboard(activity)

        if (childFragmentManager.backStackEntryCount == 0) {
            (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(false)
            return
        }

        if (childFragmentManager.backStackEntryCount == 1) {
            when (activity) {
                is AuthActivity -> (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(false)
                is VaultAuthActivity -> (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(false)
                is HomeActivity -> (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(false)
                is ContainerActivity -> (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(true)
            }
        } else {
            (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(true)
        }
    }
}