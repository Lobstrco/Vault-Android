package com.lobstr.stellar.vault.presentation.home.rate_us

import android.content.DialogInterface
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class RateUsDialogFragment : AlertDialogFragment(), RateUsView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = RateUsDialogFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @Inject
    lateinit var daggerPresenter: Lazy<RateUsPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { daggerPresenter.get() }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onPositiveBtnClick(dialogInterface: DialogInterface) {
        super.onPositiveBtnClick(dialogInterface)
        mPresenter.rateUsClicked()
    }

    override fun onNegativeBtnClick(dialogInterface: DialogInterface) {
        super.onNegativeBtnClick(dialogInterface)
        mPresenter.dontAskAgainClicked()
    }

    override fun onNeutralBtnClick(dialogInterface: DialogInterface) {
        super.onNeutralBtnClick(dialogInterface)
        mPresenter.laterClicked()
    }

    override fun onCanceled(dialogInterface: DialogInterface) {
        super.onCanceled(dialogInterface)
        mPresenter.laterClicked()
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun showStore(storeUrl: String) {
        AppUtil.openWebPage(context, storeUrl)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}