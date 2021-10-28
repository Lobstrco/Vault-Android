package com.lobstr.stellar.vault.presentation.util.manager


import androidx.fragment.app.FragmentManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.PROGRESS_DIALOG
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogIdentifier.PROGRESS


object ProgressManager {
    fun show(show: Boolean, manager: FragmentManager, handleInFragment: Boolean = false, cancelable: Boolean = false) {
        if (show) {
            AlertDialogFragment.Builder(handleInFragment)
                .setCancelable(cancelable)
                .setView(R.layout.layout_preloader)
                .setSpecificDialog(PROGRESS)
                .create()
                .showNow(manager, PROGRESS_DIALOG)
        } else {
            (manager.findFragmentByTag(PROGRESS_DIALOG) as? AlertDialogFragment)?.dismissAllowingStateLoss()
        }
    }
}