package com.lobstr.stellar.vault.presentation.base.fragment

import android.os.Bundle
import android.view.View
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatFragment
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.util.AppUtil


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

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        setHasOptionsMenu(menuVisible)

        if (isAdded) {
            val fragment = childFragmentManager.findFragmentById(R.id.fl_container)
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

        // define back button behavior for container (after back pressed)
        when (childFragmentManager.backStackEntryCount) {
            1 -> when (activity) {
                is ContainerActivity -> (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(true)
                else -> (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(false)
            }
            else -> (activity as? BaseActivity)?.mPresenter?.changeHomeBtnVisibility(true)
        }
    }
}