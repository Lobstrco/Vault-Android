package com.lobstr.stellar.vault.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lobstr.stellar.vault.presentation.util.InsetsMargin
import com.lobstr.stellar.vault.presentation.util.InsetsPadding
import com.lobstr.stellar.vault.presentation.util.doOnApplyWindowInsets

open class BaseBottomSheetDialog : BaseMvpAppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleTopInsets()
        handleInsets(view)
    }

    private fun handleTopInsets() {
        // Push status bar.
        (requireView().parent as? View)?.doOnApplyWindowInsets { view, insets, _, margins ->
            val innerPadding = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            // Workaround: set margin for the top view parent. Otherwise - invalid scroll behaviour for some cases.
            (view.parent as? View)?.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(
                    top = margins.top + innerPadding.top
                )
            }
            (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    /**
     * Handled by  <item name="paddingBottomSystemWindowInsets">true</item>
     */
    open fun handleInsets(
        view: View?,
        typeMask: Int = WindowInsetsCompat.Type.systemBars()
                or WindowInsetsCompat.Type.displayCutout()
                or WindowInsetsCompat.Type.ime(),
        insetsPadding: InsetsPadding? = InsetsPadding(
            left = true, right = true, bottom = true
        ),
        insetsMargin: InsetsMargin? = null
    ) {
        // According common logic apply left/right and bottom insets.
        view?.doOnApplyWindowInsets { view, insets, padding, margins ->
            val innerPadding = insets.getInsets(typeMask)
            insetsPadding?.apply {
                view.updatePadding(
                    left = padding.left + if (left) innerPadding.left else 0,
                    top = padding.top + if (top) innerPadding.top else 0,
                    right = padding.right + if (right) innerPadding.right else 0,
                    bottom = padding.bottom + if (bottom) innerPadding.bottom else 0
                )
            }
            insetsMargin?.apply {
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(
                        left = margins.left + if (left) innerPadding.left else 0,
                        top = margins.top + if (top) innerPadding.top else 0,
                        right = margins.right + if (right) innerPadding.right else 0,
                        bottom = margins.bottom + if (bottom) innerPadding.bottom else 0
                    )
                }
            }
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