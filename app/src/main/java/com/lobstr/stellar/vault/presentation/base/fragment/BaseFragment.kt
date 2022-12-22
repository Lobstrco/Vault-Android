package com.lobstr.stellar.vault.presentation.base.fragment

import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatFragment
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import moxy.ktx.moxyPresenter

abstract class BaseFragment : BaseMvpAppCompatFragment(), BaseFragmentView {

    private val mBasePresenter by moxyPresenter { BaseFragmentPresenter() }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if (isAdded) {
            mvpDelegate.onAttach()
            mBasePresenter.setToolbarTitle()
        }
    }

    override fun setActionBarTitle(title: String?) {
        if (parentFragment is BaseContainerFragment
            && !(parentFragment as BaseContainerFragment).isMenuVisible
        ) {
            return
        }

        (activity as? BaseActivity)?.apply {
            mvpDelegate.onAttach()
            mPresenter.setActionBarTitle(title)
        }
    }

    override fun saveActionBarTitle(titleRes: Int) {
        mBasePresenter.setToolbarTitle(if (titleRes == 0) null else getString(titleRes))
    }

    override fun saveActionBarTitle(title: String?) {
        mBasePresenter.setToolbarTitle(title)
    }
}