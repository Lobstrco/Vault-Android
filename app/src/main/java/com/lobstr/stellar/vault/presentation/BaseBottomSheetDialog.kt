package com.lobstr.stellar.vault.presentation

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog


open class BaseBottomSheetDialog : BaseMvpAppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        // If dialog don't equals null - skip it.
        if (manager.findFragmentByTag(tag) != null) {
            return
        }

        super.show(manager, tag)
    }
}