package com.lobstr.stellar.vault.presentation.base.fragment

import android.os.Bundle
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatFragment
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import moxy.ktx.moxyPresenter

abstract class BaseFragment : BaseMvpAppCompatFragment(), BaseFragmentView {

    private val mBasePresenter by moxyPresenter { BaseFragmentPresenter() }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        setHasOptionsMenu(menuVisible)

        if (isAdded) {
            mvpDelegate.onAttach()
            mBasePresenter.setToolbarTitle()

            if (menuVisible) {
                mBasePresenter.checkOptionsMenuVisibility()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check: has menu options or don't (by default true)
        setHasOptionsMenu(if (parentFragment is BaseContainerFragment) (parentFragment as BaseContainerFragment).isMenuVisible else true)
    }

    /**
     * @return False to allow normal back press processing to
     * proceed, true to consume it here.
     * @see BaseActivity.onBackPressed
     */
    open fun onBackPressed() = false

    override fun saveOptionsMenuVisibility(visible: Boolean) {
        mBasePresenter.saveOptionsMenuVisibilityCalled(visible)
    }

    override fun setOptionsMenuVisible(visible: Boolean) {
        if (parentFragment is BaseContainerFragment) {
            // manage options menu visibility only i case when BaseContainerFragment is visible to user
            if (parentFragment?.isMenuVisible == true) {
                setHasOptionsMenu(visible)
            }
        } else {
            // otherwise - handle as is
            setHasOptionsMenu(visible)
        }
    }

    override fun setActionBarTitle(title: String?) {
        if (parentFragment is BaseContainerFragment
            && !(parentFragment as BaseContainerFragment).isMenuVisible
        ) {
            return
        }

        (activity as? BaseActivity)?.mvpDelegate?.onAttach()
        (activity as? BaseActivity)?.mPresenter?.setActionBarTitle(title)
    }

    override fun saveActionBarTitle(titleRes: Int) {
        mBasePresenter.setToolbarTitle(if (titleRes == 0) null else getString(titleRes))
    }

    override fun saveActionBarTitle(title: String?) {
        mBasePresenter.setToolbarTitle(title)
    }
}