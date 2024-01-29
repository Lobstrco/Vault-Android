package com.lobstr.stellar.vault.presentation.util

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable

fun Context.getTintDrawable(
    drawableRes: Int,
    colorRes: Int = -1,
    color: Int? = null,
    tintMode: PorterDuff.Mode? = null
): Drawable {
    val source = drawable(drawableRes).mutate()
    val wrapped = DrawableCompat.wrap(source)
    tintMode?.let { DrawableCompat.setTintMode(wrapped, it) }
    DrawableCompat.setTint(wrapped, color ?: color(colorRes))

    return wrapped
}

fun Context.getTintDrawable(
    drawable: Drawable,
    colorRes: Int = -1,
    color: Int? = null,
    tintMode: PorterDuff.Mode? = null
): Drawable {
    val source = drawable.mutate()
    val wrapped = DrawableCompat.wrap(source)
    tintMode?.let { DrawableCompat.setTintMode(wrapped, it) }
    DrawableCompat.setTint(wrapped, color ?: color(colorRes))

    return wrapped
}

fun Context.getResizedDrawable(
    drawableRes: Int,
    width: Float, height: Float
): Drawable {
    val source = drawable(drawableRes).mutate()
    val wrapped = DrawableCompat.wrap(source)

    return wrapped.toBitmap(
        AppUtil.convertDpToPixels(this, width),
        AppUtil.convertDpToPixels(this, height)
    ).toDrawable(resources)
}

fun Context.getResizedDrawable(
    drawable: Drawable,
    width: Float, height: Float
): Drawable {
    val source = drawable.mutate()
    val wrapped = DrawableCompat.wrap(source)

    return wrapped.toBitmap(
        AppUtil.convertDpToPixels(this, width),
        AppUtil.convertDpToPixels(this, height)
    ).toDrawable(resources)
}

fun Context.getTintResizedDrawable(
    drawableRes: Int,
    colorRes: Int = -1,
    color: Int? = null,
    tintMode: PorterDuff.Mode? = null,
    width: Float, height: Float
): Drawable {
    val wrapped = getTintDrawable(
        drawableRes, colorRes, color, tintMode
    )

    return wrapped.toBitmap(
        AppUtil.convertDpToPixels(this, width),
        AppUtil.convertDpToPixels(this, height)
    ).toDrawable(resources)
}

fun Context.drawable(drawableRes: Int) = ContextCompat.getDrawable(this, drawableRes)!!

fun Context.color(colorRes: Int) = ContextCompat.getColor(this, colorRes)
