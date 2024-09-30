package com.lobstr.stellar.vault.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lobstr.stellar.vault.presentation.util.doOnApplyWindowInsets

open class BaseBottomSheetDialog : BaseMvpAppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleTopInsets()
        handleInsets()
    }

    private fun handleTopInsets() {
        // Push status bar.
        dialog?.window?.decorView?.doOnApplyWindowInsets { view, insets, padding, _ ->
            val innerPadding = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            view.updatePadding(
                top = padding.top + innerPadding.top
            )
        }
    }

    /**
     * Handled by  <item name="paddingBottomSystemWindowInsets">true</item>
     */
    open fun handleInsets() {
        view?.doOnApplyWindowInsets { view, insets, padding, _ ->
            val innerPadding = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
                        or WindowInsetsCompat.Type.ime()
            )
            view.updatePadding(
                left = padding.left + innerPadding.left,
                right = padding.right + innerPadding.right,
                bottom = padding.bottom + innerPadding.bottom
            )
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        // If dialog don't equals null - skip it.
        if (manager.findFragmentByTag(tag) != null) {
            return
        }

        super.show(manager, tag)
    }
}