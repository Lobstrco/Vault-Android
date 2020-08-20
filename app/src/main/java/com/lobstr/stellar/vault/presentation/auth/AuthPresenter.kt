package com.lobstr.stellar.vault.presentation.auth

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.lobstr.stellar.vault.R
import moxy.MvpPresenter

class AuthPresenter : MvpPresenter<AuthView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setActionBarIconVisibility(false)
        updateToolbar(
            android.R.color.white,
            R.drawable.ic_arrow_back,
            R.color.color_primary,
            R.color.color_primary
        )

        viewState.showAuthFragment()
    }

    fun updateToolbar(
        @ColorRes toolbarColor: Int?,
        @DrawableRes upArrow: Int?,
        @ColorRes upArrowColor: Int?,
        @ColorRes titleColor: Int?
    ) {
        toolbarColor?.let { viewState.setupToolbarColor(it) }
        upArrow?.let { arrow ->
            upArrowColor?.let { color ->
                viewState.setupToolbarUpArrow(
                    arrow,
                    color
                )
            }
        }
        titleColor?.let { viewState.setupToolbarTitleColor(it) }
    }
}