package com.lobstr.stellar.vault.presentation.util

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.nfc.NfcAdapter
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.internal.Primitives
import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.util.Constant.Symbol.NULL
import org.stellar.sdk.AccountConverter
import org.stellar.sdk.xdr.MuxedAccount
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*


object AppUtil {

    fun isGooglePlayServicesAvailable(context: Context, showExplanation: Boolean = false): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Log.e("GooglePlayServices", "No valid Google Play Services APK found. ")
                if (context is Activity && showExplanation) {
                    apiAvailability.getErrorDialog(context, resultCode, 9000)?.show()
                }
            } else {
                Log.e("GooglePlayServices", "This device is not supported. ")
                if (showExplanation) {
                    Toast.makeText(
                        context,
                        R.string.msg_google_play_services_not_supported,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return false
        }
        return true
    }

    fun openWebPage(context: Context?, url: String) {
        if (url.isEmpty()) {
            return
        }

        try {
            CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(ContextCompat.getColor(context!!, R.color.color_primary))
                    .build()
            )
            .build()
                .apply {
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    launchUrl(context, Uri.parse(url))
                }
        } catch (exc: ActivityNotFoundException) {
            Toast.makeText(context, R.string.msg_no_app_found, Toast.LENGTH_SHORT).show()
        }
    }

    fun formatDate(date: Long, datePattern: String, locale: Locale = Locale.ENGLISH): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        try {
            val format = SimpleDateFormat(datePattern, locale)
            return format.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return NULL
    }

    fun copyToClipboard(context: Context?, extractedString: String) {
        try {
            val clipboard =
                context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", extractedString)
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(context, R.string.msg_successfully_copied, Toast.LENGTH_SHORT).show()
        } catch (exc: SecurityException) {
            // Handle security exception in some cases (Android Q): https://developer.android.com/about/versions/10/privacy/changes#clipboard-data
            Toast.makeText(context, R.string.msg_unknown_error, Toast.LENGTH_SHORT).show()
        }
    }

    fun getAppVersionCode(context: Context?): Long {
        return try {
            val packageInfo = context?.packageManager?.getPackageInfo(context.packageName, 0)
            if (packageInfo != null) PackageInfoCompat.getLongVersionCode(packageInfo) else -1
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("NameNotFoundException", "Could not get package name:$e")
            -1
        }
    }

    fun closeKeyboard(activity: Activity?) {
        if (activity?.currentFocus != null) {
            val inputMethodManager = activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(
                activity.currentFocus!!
                    .windowToken, 0
            )
        }
    }

    fun getJwtToken(token: String?) = "JWT $token"

    fun <T> convertJsonToPojo(jsonStr: String?, classOfT: Class<T>): T? {
        if (jsonStr == null) {
            return null
        }

        try {
            val result = Gson().fromJson(jsonStr, classOfT)
            return Primitives.wrap(classOfT).cast(result)
        } catch (exc: JsonSyntaxException) {
            exc.printStackTrace()
        } catch (exc: JsonIOException) {
            exc.printStackTrace()
        } catch (exc: IOException) {
            exc.printStackTrace()
        }

        return null
    }

    fun convertPixelsToDp(context: Context, px: Int): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertDpToPixels(context: Context, dp: Float): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun getTextHeight(t: TextView): Int {
        val widthMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(screenWidth(t.context), View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        t.measure(widthMeasureSpec, heightMeasureSpec)
        return t.measuredHeight
    }

    fun screenWidth(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).currentWindowMetrics
            // Gets all excluding insets.
            val windowInsets = WindowInsetsCompat.toWindowInsetsCompat(metrics.windowInsets)
            val insets = windowInsets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars()
                    or WindowInsetsCompat.Type.displayCutout())
            val insetsWidth = insets.right + insets.left
            // Legacy size that Display#getSize reports.
            val bounds = metrics.bounds
            bounds.width() - insetsWidth
        } else {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.x
        }
    }

    fun vibrate(context: Context, pattern: LongArray) {
        val vibrator =
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator? ?: return

        if (!vibrator.hasVibrator()) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    fun getString(@StringRes resId: Int): String =
        getAppContext().getString(resId)

    fun getAppContext(): Context = LVApplication.appContext

    fun ellipsizeStrInMiddle(str: String?, count: Int): String? {
        return if (str.isNullOrEmpty() || count >= (str.length / 2 - 1)) {
            str
        } else str.substring(0, count) + "…" + str.substring(str.length - count)
    }

    fun getConfigType(value: Boolean): Byte = if (value) {
        Constant.ConfigType.YES
    } else {
        Constant.ConfigType.NO
    }

    fun getConfigValue(type: Byte): Boolean = when (type) {
        Constant.ConfigType.YES -> true
        else -> false
    }

    fun getConfigText(type: Byte): String? = when (type) {
        Constant.ConfigType.YES -> getString(R.string.text_config_yes)
        Constant.ConfigType.NO -> getString(R.string.text_config_no)
        else -> null
    }

    fun isNfcAvailable(): Boolean {
        return NfcAdapter.getDefaultAdapter(getAppContext()) != null
    }

    fun isNvsEnabled(): Boolean {
        return NfcAdapter.getDefaultAdapter(getAppContext())?.isEnabled ?: false
    }

    fun sendEmail(
        context: Context,
        mails: Array<String>?,
        subject: String?,
        body: String? = null
    ) {
        context.startActivity(Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, mails ?: "")
            putExtra(Intent.EXTRA_TEXT, body ?: "")
            putExtra(Intent.EXTRA_SUBJECT, subject ?: "")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun getAppBehavior() =
        if (BuildConfig.BUILD_TYPE == Constant.BuildType.RELEASE) Constant.Behavior.PRODUCTION else Constant.Behavior.STAGING

    fun composeLaboratoryUrl(
        input: String,
        type: String = Constant.Laboratory.Type.TRANSACTION_ENVELOPE,
        network: String = Constant.Laboratory.NETWORK.PUBLIC
    ) = String.format(
        Constant.Laboratory.URL, URLEncoder.encode(input, "utf-8"), type, network
    )

    /**
     * Encode Account to MuxedAccount.
     * @return null when invalid account. Otherwise - MuxedAccount.
     */
    fun encodeMuxedAccount(account: String?, enableMuxed: Boolean = true): MuxedAccount? {
        return try {
            if (account.isNullOrEmpty()) {
                null
            } else {
                val converter = if(enableMuxed) AccountConverter.enableMuxed() else AccountConverter.disableMuxed()
                converter.encode(account)
            }
        } catch (exc: Exception) {
            null
        }
    }

    /**
     * Retrieve Account ID for any account.
     */
    fun decodeAccount(account: MuxedAccount): String {
        return AccountConverter.disableMuxed().decode(account)
    }

    /**
     * Get ED25519.
     */
    fun decodeAccountStr(account: String): String {
        return encodeMuxedAccount(account)?.let { decodeAccount(it) } ?: ""
    }

    /**
     * Make Account Validation (ED25519 or MUXED_ED25519).
     * @return true - ED25519 or MUXED_ED25519. Otherwise - false.
     */
    fun isValidAccount(account: String?, enableMuxed: Boolean = true): Boolean {
        return encodeMuxedAccount(account, enableMuxed) != null
    }

    fun createUserIconLink(key: String?): String {
        return Constant.Social.USER_ICON_LINK.plus(key?.let { decodeAccountStr(it) }).plus(".png")
    }
}