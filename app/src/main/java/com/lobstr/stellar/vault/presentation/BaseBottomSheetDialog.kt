package com.lobstr.stellar.vault.presentation

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog


open class BaseBottomSheetDialog : BaseMvpAppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(context!!, theme)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // handle IllegalStateException: DialogFragment can not be attached to a container view
        // in this case just dismiss dialog
        try {
            super.onActivityCreated(savedInstanceState)
        } catch (exc: IllegalStateException) {
            dismiss()
        }
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        // if dialog is showed - skip it
        if (manager?.findFragmentByTag(tag)?.isVisible == true) {
            return
        }

        super.show(manager, tag)
    }
}