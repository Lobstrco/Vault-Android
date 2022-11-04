package com.lobstr.stellar.vault.presentation.base.fragment

import android.os.Bundle
import android.view.View
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatFragment
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity


abstract class BaseContainerFragment : BaseMvpAppCompatFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        childFragmentManager.addOnBackStackChangedListener {
            checkBackStack()
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if (isAdded) {
            val fragment = childFragmentManager.findFragmentById(R.id.flContainer)
            fragment?.setMenuVisibility(menuVisible)
        }

        if (!menuVisible) {
            return
        }

        checkBackStack()
    }

    private fun checkBackStack() {
        if (!isAdded) {
            return
        }

        AppUtil.closeKeyboard(activity)

        // Define back button behavior for container (after back pressed).
        when (childFragmentManager.backStackEntryCount) {
            1 -> when (activity) {
                is ContainerActivity -> (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(true)
                is VaultAuthActivity -> (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(true)
                else -> (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(false)
            }
            else -> (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(true)
        }
    }
}