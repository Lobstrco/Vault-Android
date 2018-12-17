package com.lobstr.stellar.vault.presentation.dialog.alert.progress

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment


class ProgressDialog : AlertDialogFragment(), ProgressDialogView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ProgressDialog::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: ProgressDialogPresenter

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideProgressDialogPresenter() = ProgressDialogPresenter()

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setTransparentBackground() {
        val alertDialog = dialog
        alertDialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}