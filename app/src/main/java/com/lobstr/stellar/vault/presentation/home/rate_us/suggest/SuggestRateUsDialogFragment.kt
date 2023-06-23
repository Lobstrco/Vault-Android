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
            .setTitle(R.string.rate_us_title)
            .setMessage(R.string.rate_us_description)
            .setNegativeBtnText(R.string.rate_us_rate_never_action)
            .setNeutralBtnText(R.string.rate_us_rate_later_action)
            .setPositiveBtnText(R.string.rate_us_rate_action)
            .create()
            .show(requireActivity().supportFragmentManager, DialogFragmentIdentifier.RATE_US)
    }

    override fun showFeedbackDialog() {
        Builder(false)
            .setCancelable(true)
            .setSpecificDialog(DialogIdentifier.PROVIDE_FEEDBACK)
            .setTitle(R.string.provide_feedback_title)
            .setMessage(R.string.provide_feedback_description)
            .setNegativeBtnText(R.string.close_action)
            .setPositiveBtnText(R.string.provide_feedback_action)
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