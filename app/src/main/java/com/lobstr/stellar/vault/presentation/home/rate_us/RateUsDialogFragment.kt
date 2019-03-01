package com.lobstr.stellar.vault.presentation.home.rate_us

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil


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

    @InjectPresenter
    lateinit var mPresenter: RateUsPresenter

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideIRateUsPresenter() = RateUsPresenter()

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
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl)))
        } catch (exc: ActivityNotFoundException) {
            AppUtil.launchGoogleCustomTabs(context, storeUrl)
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}