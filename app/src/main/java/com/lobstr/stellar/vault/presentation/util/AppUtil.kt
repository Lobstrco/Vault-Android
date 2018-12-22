package com.lobstr.stellar.vault.presentation.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.internal.Primitives
import com.lobstr.stellar.vault.R
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


object AppUtil {

    fun launchGoogleCustomTabs(context: Context?, url: String) {
        if (url.isEmpty()) {
            return
        }

        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        builder.setToolbarColor(ContextCompat.getColor(context!!, R.color.color_ff3a6c99))
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    fun formatDate(date: Date, datePattern: String): String? {
        try {
            val format = SimpleDateFormat(datePattern)
            return format.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun copyToClipboard(context: Context?, extractedString: String) {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", extractedString)
        clipboard?.primaryClip = clip
        Toast.makeText(context, R.string.msg_successfully_copied, Toast.LENGTH_SHORT).show()
    }

    fun pasteFromClipboard(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val item = clipboard.primaryClip.getItemAt(0)

        return if (item == null || item.text == null) {
            null
        } else item.text.toString()
    }

    fun getAppVersionCode(context: Context?): Int {
        return try {
            val packageInfo = context?.packageManager?.getPackageInfo(context.packageName, 0)
            packageInfo?.versionCode ?: -1
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("NameNotFoundException", "Could not get package name:$e")
            -1
        }
    }

    fun getAppVersionName(context: Context?): String? {
        return try {
            val packageInfo = context?.packageManager?.getPackageInfo(context.packageName, 0)
            packageInfo?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("NameNotFoundException", "Could not get package name:$e")
            null
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

    fun showKeyboard(activity: Activity?) {
        (activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
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
}