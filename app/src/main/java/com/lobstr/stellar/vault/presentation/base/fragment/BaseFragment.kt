package com.lobstr.stellar.vault.presentation.base.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatFragment
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_SHOW_COMMON_TOOLBAR
import com.lobstr.stellar.vault.presentation.util.InsetsMargin
import com.lobstr.stellar.vault.presentation.util.InsetsPadding
import com.lobstr.stellar.vault.presentation.util.doOnApplyWindowInsets
import moxy.ktx.moxyPresenter

abstract class BaseFragment : BaseMvpAppCompatFragment(), BaseFragmentView {

    private val mBasePresenter by moxyPresenter {
        BaseFragmentPresenter(
            arguments?.getBoolean(BUNDLE_SHOW_COMMON_TOOLBAR, true) ?: true
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleInsets(view)
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if (isAdded) {
            mvpDelegate.onAttach()
            mBasePresenter.setToolbarTitle()
        }
    }

    open fun handleInsets(
        view: View?,
        typeMask: Int = WindowInsetsCompat.Type.systemBars()
                or WindowInsetsCompat.Type.displayCutout()
                or WindowInsetsCompat.Type.ime(),
        insetsPadding: InsetsPadding? = InsetsPadding(
            left = true, right = true, bottom = true
        ),
        insetsMargin: InsetsMargin? = null
    ) {
        // According common logic apply left/right and bottom insets.
        view?.doOnApplyWindowInsets { view, insets, padding, margins ->
            val innerPadding = insets.getInsets(typeMask)
            insetsPadding?.apply {
                view.updatePadding(
                    left = padding.left + if (left) innerPadding.left else 0,
                    top = padding.top + if (top) innerPadding.top else 0,
                    right = padding.right + if (right) innerPadding.right else 0,
                    bottom = if (requireActivity() is HomeActivity) {
                        padding.bottom
                    } else {
                        padding.bottom + if (bottom) innerPadding.bottom else 0
                    }
                )
            }
            insetsMargin?.apply {
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(
                        left = margins.left + if (left) innerPadding.left else 0,
                        top = margins.top + if (top) innerPadding.top else 0,
                        right = margins.right + if (right) innerPadding.right else 0,
                        bottom = if (requireActivity() is HomeActivity) {
                            margins.bottom
                        } else {
                            margins.bottom + if (bottom) innerPadding.bottom else 0
                        }
                    )
                }
            }
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