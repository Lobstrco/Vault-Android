package com.lobstr.stellar.vault.presentation

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog


open class BaseBottomSheetDialog : BaseMvpAppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(context!!, theme)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        // if dialog is showed - skip it
        if (manager?.findFragmentByTag(tag)?.isVisible == true) {
            return
        }

        super.show(manager, tag)
    }
}