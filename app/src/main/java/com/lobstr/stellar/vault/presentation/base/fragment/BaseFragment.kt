package com.lobstr.stellar.vault.presentation.base.fragment

import android.os.Bundle
import androidx.annotation.StringRes
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatFragment
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity


abstract class BaseFragment : BaseMvpAppCompatFragment(), BaseFragmentView {

    @InjectPresenter
    lateinit var mBasePresenter: BaseFragmentPresenter

    @ProvidePresenter
    fun provideBaseFragmentPresenter() = BaseFragmentPresenter()

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        setHasOptionsMenu(isVisibleToUser)

        if (isAdded) {
            mvpDelegate.onAttach()
            mBasePresenter.setToolbarTitle()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (parentFragment !is BaseContainerFragment) {
            return
        }

        // Check: has menu options or don't
        if ((parentFragment as BaseContainerFragment).userVisibleHint) {
            setHasOptionsMenu(true)
        }
    }

    /**
     * @return boolean Return false to allow normal back press processing to
     * proceed, true to consume it here.
     * @see BaseActivity.onBackPressed
     */
    open fun onBackPressed() = false

    override fun setActionBarTitle(@StringRes titleRes: Int) {
        if (parentFragment is BaseContainerFragment
            && !(parentFragment as BaseContainerFragment).userVisibleHint
        ) {
            return
        }

        (activity as? BaseActivity)?.getMvpDelegate()?.onAttach()
        (activity as? BaseActivity)?.mPresenter?.setActionBarTitle(titleRes)
    }

    override fun saveActionBarTitle(titleRes: Int) {
        mBasePresenter.setToolbarTitle(titleRes)
    }
}