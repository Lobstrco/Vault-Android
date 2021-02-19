package com.lobstr.stellar.vault.presentation.home.rate_us.suggest

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.widget.Toast
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class SuggestRateUsDialogFragment : AlertDialogFragment(), SuggestRateUsView {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    @Inject
    lateinit var presenterProvider: Provider<SuggestRateUsPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { presenterProvider.get() }

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

    override fun sendMail(mail: String, subject: String, body: String?) {
        try {
            AppUtil.sendEmail(requireContext(), arrayOf(mail), subject, body)
        } catch (exc: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_mail_client_not_found),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}