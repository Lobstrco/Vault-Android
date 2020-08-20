package com.lobstr.stellar.vault.presentation.dialog.alert.progress

import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import moxy.ktx.moxyPresenter


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

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { ProgressDialogPresenter() }

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
        alertDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}