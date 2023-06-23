package com.lobstr.stellar.vault.presentation.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

enum class VibrateType(val pattern: LongArray) {
    TYPE_ONE(longArrayOf(0, 8, 0, 0)),
    TYPE_TWO(longArrayOf(1500, 175, 0, 0)),
    TYPE_THREE(longArrayOf(2000, 50, 50, 50)),
    TYPE_FOUR(longArrayOf(0, 20, 0, 0))
}

object VibratorUtil {

    fun vibrate(context: Context, type: VibrateType) {
        vibrate(context, type.pattern)
    }

    private fun vibrate(context: Context, pattern: LongArray, amplitudes: IntArray? = null) {
        val vibrator  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =  context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
        }

        if (vibrator == null || !vibrator.hasVibrator()) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                if (amplitudes != null && amplitudes.size == pattern.size && vibrator.hasAmplitudeControl()) {
                    VibrationEffect.createWaveform(
                        pattern,
                        amplitudes,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                } else {
                    VibrationEffect.createWaveform(
                        pattern,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                }
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }
}