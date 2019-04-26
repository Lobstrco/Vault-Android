package com.lobstr.stellar.vault.presentation.util.biometric

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lobstr.stellar.vault.R
import kotlinx.android.synthetic.main.layout_biometric_bottom_sheet.*

class BiometricDialogV23(
    context: Context,
    private var biometricCallback: BiometricCallback
) :
    BottomSheetDialog(context, R.style.BottomSheetDialogTheme),
    View.OnClickListener {

    init {
        setDialogView()
    }

    private fun setDialogView() {
        val bottomSheetView = layoutInflater.inflate(R.layout.layout_biometric_bottom_sheet, null)
        setContentView(bottomSheetView)

        btnCancel.setOnClickListener(this)
    }

    fun setTitle(title: String) {
        tvTitle.text = title
    }

    fun updateStatus(status: String) {
        tvStatus.text = status
    }

    fun setSubtitle(subtitle: String) {
        tvSubtitle.text = subtitle
    }

    fun setDescription(description: String) {
        tvDescription.text = description
    }

    fun setButtonText(negativeButtonText: String) {
        btnCancel.text = negativeButtonText
    }

    override fun onClick(view: View) {
        dismiss()
        biometricCallback.onAuthenticationCancelled()
    }
}
