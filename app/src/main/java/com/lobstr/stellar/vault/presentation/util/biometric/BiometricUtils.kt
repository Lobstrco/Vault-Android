package com.lobstr.stellar.vault.presentation.util.biometric

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.core.app.ActivityCompat


@SuppressLint("MissingPermission")
object BiometricUtils {


    /*
     * Condition I: Check if the android version in device is greater than
     * Marshmallow, since biometric authentication is only supported
     * from Android 6.0.
     * Note: If your project's minSdkversion is 23 or higher,
     * then you won't need to perform this check.
     * */
    private val isSdkVersionSupported: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    /*
     * Condition II: Check if the device has biometric sensors.
     * */
    private fun isHardwareSupported(context: Context): Boolean {
        return try {
            BiometricManager.from(context).canAuthenticate() != BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
        } catch (exc: SecurityException) {
            exc.printStackTrace()
            false
        }
    }

    /*
     * Condition III: Biometric authentication can be matched with a
     * registered biometric of the user. So we need to perform this check
     * in order to enable biometric authentication
     *
     * */
    fun isBiometricAvailable(context: Context): Boolean {
        return try {
            BiometricManager.from(context).canAuthenticate() != BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
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
            isSdkVersionSupported && isHardwareSupported(context) && isPermissionGranted(context)
        } catch (exc: SecurityException) {
            exc.printStackTrace()
            false
        }
    }
}
