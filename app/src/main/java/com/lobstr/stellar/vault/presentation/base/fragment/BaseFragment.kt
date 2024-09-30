package com.lobstr.stellar.vault.presentation.base.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatFragment
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.util.doOnApplyWindowInsets
import moxy.ktx.moxyPresenter

abstract class BaseFragment : BaseMvpAppCompatFragment(), BaseFragmentView {

    private val mBasePresenter by moxyPresenter { BaseFragmentPresenter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleInsets()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if (isAdded) {
            mvpDelegate.onAttach()
            mBasePresenter.setToolbarTitle()
        }
    }

    open fun handleInsets() {
        // According common logic apply left/right and bottom insets.
        view?.doOnApplyWindowInsets { view, insets, padding, _ ->
            val innerPadding = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
                        or WindowInsetsCompat.Type.ime()
            )
            view.updatePadding(
                left = padding.left + innerPadding.left,
                right = padding.right + innerPadding.right,
                bottom = if (requireActivity() is HomeActivity) {
                    padding.bottom
                } else {
                    padding.bottom + innerPadding.bottom
                }
            )
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