package com.lobstr.stellar.vault.presentation.home.rate_us.suggest

import android.content.DialogInterface
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import moxy.ktx.moxyPresenter

class SuggestRateUsDialogFragment : AlertDialogFragment(), SuggestRateUsView {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { SuggestRateUsPresenter() }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onPositiveBtnClick(dialogInterface: DialogInterface) {
        super.onPositiveBtnClick(dialogInterface)
        mPresenter.rateClicked()
    }

    override fun onNegativeBtnClick(dialogInterface: DialogInterface) {
        super.onNegativeBtnClick(dialogInterface)
        mPresenter.cancelClicked()
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun showRateUsDialog() {
        Builder(false)
            .setCancelable(true)
            .setSpecificDialog(DialogIdentifier.RATE_US)
            .setTitle(R.string.title_rate_us_dialog)
            .setMessage(R.string.msg_rate_us_dialog)
            .setNegativeBtnText(R.string.text_btn_rate_never)
            .setNeutralBtnText(R.string.text_btn_rate_later)
            .setPositiveBtnText(R.string.text_btn_rate_us)
            .create()
            .show(requireActivity().supportFragmentManager, DialogFragmentIdentifier.RATE_US)
    }

    override fun showFeedbackDialog() {
        Builder(false)
            .setCancelable(true)
            .setSpecificDialog(DialogIdentifier.PROVIDE_FEEDBACK)
            .setTitle(R.string.title_provide_feedback)
            .setMessage(R.string.msg_provide_feedback)
            .setNegativeBtnText(R.string.text_btn_close)
            .setPositiveBtnText(R.string.text_btn_provide_feedback)
            .create()
            .show(requireActivity().supportFragmentManager, DialogFragmentIdentifier.PROVIDE_FEEDBACK)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}