package com.lobstr.stellar.vault.presentation.util.manager


import androidx.appcompat.app.AppCompatActivity
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.PROGRESS_DIALOG
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogIdentifier.PROGRESS


object ProgressManager {

    fun show(activity: AppCompatActivity?, cancelable: Boolean): AlertDialogFragment? {

        if (activity == null) {
            return null
        }

        val fragment = activity.supportFragmentManager.findFragmentByTag(PROGRESS_DIALOG)

        if (fragment != null
            && fragment is AlertDialogFragment
            && fragment.dialog != null
            && fragment.dialog.isShowing
        ) {

            return fragment
        }

        val dialogFragment = AlertDialogFragment.Builder(false)
            .setCancelable(cancelable)
            .setView(R.layout.layout_preloader)
            .setSpecificDialog(PROGRESS, null)
            .create()

        dialogFragment.show(
            activity.supportFragmentManager,
            PROGRESS_DIALOG
        )

        return dialogFragment
    }

    fun dismiss(alertDialogFragment: AlertDialogFragment?) {
        if (alertDialogFragment == null) {
            return
        }

        alertDialogFragment.dismissAllowingStateLoss()
    }
}