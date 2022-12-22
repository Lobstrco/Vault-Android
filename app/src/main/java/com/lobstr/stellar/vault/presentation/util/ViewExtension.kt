package com.lobstr.stellar.vault.presentation.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


fun View.doOnApplyWindowInsets(f: (View, WindowInsetsCompat, InitialPadding) -> Unit) {
    // Create a snapshot of the view's padding state.
    val initialPadding = recordInitialPaddingForView(this)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding state.

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        f(v, insets, initialPadding)
        // Always return the insets, so that children can also use them.
        insets
    }

    // Request some insets.
    requestApplyInsetsWhenAttached()
}

data class InitialPadding(
    val left: Int, val top: Int,
    val right: Int, val bottom: Int
)

private fun recordInitialPaddingForView(view: View) = InitialPadding(
    view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
)

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal.
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are.
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

fun CharSequence.applyColor(
    @ColorInt color: Int,
    start: Int,
    end: Int
) = (if (this is Spannable) this else SpannableString(this)).apply {
    setSpan(
        ForegroundColorSpan(color),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}

fun CharSequence.applyTypeface(
    typeface: Typeface,
    start: Int,
    end: Int,
) = (if (this is Spannable) this else SpannableString(this)).apply {
    setSpan(object : TypefaceSpan(null) {
        override fun updateDrawState(ds: TextPaint) {
            ds.typeface = typeface
        }
    }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun CharSequence.applySize(
    proportion: Float,
    start: Int,
    end: Int,
) = (if (this is Spannable) this else SpannableString(this)).apply {
    setSpan(RelativeSizeSpan(proportion), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun CharSequence.applyStyle(
    style: Int,
    start: Int,
    end: Int,
) = (if (this is Spannable) this else SpannableString(this)).apply {
    setSpan(StyleSpan(style), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}