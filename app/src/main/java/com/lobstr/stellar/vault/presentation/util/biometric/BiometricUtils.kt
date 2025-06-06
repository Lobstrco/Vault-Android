package com.lobstr.stellar.vault.presentation.util.biometric

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.core.app.ActivityCompat


@SuppressLint("MissingPermission")
object BiometricUtils {

    /*
     * Condition II: Check if the device has biometric sensors.
     * */
    private fun isHardwareSupported(context: Context): Boolean {
        return try {
            when(BiometricManager.from(context).canAuthenticate(BIOMETRIC_WEAK)) {
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> false
                else -> true
            }
        } catch (exc: SecurityException) {
            exc.printStackTrace()
            false
        }
    }

    /*
     * Condition III: Biometric authentication can be matched with a
     * registered biometric of the user. So we need to perform this check
     * in order to enable biometric authentication.
     *
     * */
    fun isBiometricAvailable(context: Context): Boolean {
        return try {
            BiometricManager.from(context).canAuthenticate(BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
        } catch (exc: SecurityException) {
            exc.printStackTrace()
            false
        }
    }

    /*
     * Condition IV: Check if the permission has been added to
     * the app. This permission will be granted as soon as the user
     * installs the app on their device.
     *
     * */
    fun isPermissionGranted(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.USE_FINGERPRINT
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isBiometricSupported(context: Context): Boolean {
        return try {
            isHardwareSupported(context) && isPermissionGranted(context)
        } catch (exc: SecurityException) {
            exc.printStackTrace()
            false
        }
    }
}
