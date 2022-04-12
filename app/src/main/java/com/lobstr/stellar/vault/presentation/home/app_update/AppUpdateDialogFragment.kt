package com.lobstr.stellar.vault.presentation.home.app_update

import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener


class AppUpdateDialogFragment : AlertDialogFragment() {

    override fun onStart() {
        super.onStart()
        // Prevent dialog dismissing after onClick action.
        (dialog as? AlertDialog)?.getButton(Dialog.BUTTON_POSITIVE)?.setSafeOnClickListener {
            onPositiveBtnClick(dialog!!)
        }
    }

    override fun onPositiveBtnClick(dialogInterface: DialogInterface) {
        (mOnBaseAlertDialogListener as? OnAppUpdateDialogListener)?.onUpdateClicked()
    }

    override fun onNegativeBtnClick(dialogInterface: DialogInterface) {
        dismiss()
        (mOnBaseAlertDialogListener as? OnAppUpdateDialogListener)?.onSkipClicked()
    }

    interface OnAppUpdateDialogListener : OnBaseAlertDialogListener {
        fun onUpdateClicked()
        fun onSkipClicked()
    }
}