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
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

fun View.doOnApplyWindowInsets(
    consumed: Boolean = false,
    f: (View, WindowInsetsCompat, Insets, Insets) -> Unit
) {
    // Create a snapshot of the view's padding and margins state.
    val initialPadding = Insets.of(paddingLeft, paddingTop, paddingRight, paddingBottom)
    val initialMargins = Insets.of(marginLeft, marginTop, marginRight, marginBottom)

    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding state.

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        f(v, insets, initialPadding, initialMargins)
        if (consumed) WindowInsetsCompat.CONSUMED else insets
    }

    // Request some insets.
    requestApplyInsetsWhenAttached()
}

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