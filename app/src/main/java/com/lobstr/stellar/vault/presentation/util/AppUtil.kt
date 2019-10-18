package com.lobstr.stellar.vault.presentation.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.internal.Primitives
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.*
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.CancelSellOfferOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.CreatePassiveSellOfferOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.ManageBuyOfferOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.SellOfferOperation
import com.lobstr.stellar.vault.presentation.util.Constant.Symbol.NULL
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


object AppUtil {

    fun openWebPage(context: Context?, url: String) {
        if (url.isEmpty()) {
            return
        }

        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        builder.setToolbarColor(ContextCompat.getColor(context!!, R.color.color_primary))
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    fun formatDate(date: Long, datePattern: String): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        try {
            val format = SimpleDateFormat(datePattern, Locale.ENGLISH)
            return format.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return NULL
    }

    fun copyToClipboard(context: Context?, extractedString: String) {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", extractedString)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, R.string.msg_successfully_copied, Toast.LENGTH_SHORT).show()
    }

    fun pasteFromClipboard(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val item = clipboard.primaryClip?.getItemAt(0)

        return if (item == null || item.text == null) {
            null
        } else item.text.toString()
    }

    fun getAppVersionCode(context: Context?): Long {
        return try {
            val packageInfo = context?.packageManager?.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo?.longVersionCode ?: -1
            } else {
                @Suppress("DEPRECATION")
                packageInfo?.versionCode?.toLong() ?: -1
            }
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

    fun getTransactionOperationName(operation: Operation): Int {
        return when (operation) {
            is PaymentOperation -> R.string.text_operation_name_payment
            is CreateAccountOperation -> R.string.text_operation_name_create_account
            is PathPaymentStrictSendOperation -> R.string.text_operation_name_path_payment_strict_send
            is PathPaymentStrictReceiveOperation -> R.string.text_operation_name_path_payment_strict_receive
            is SellOfferOperation -> R.string.text_operation_name_sell_offer
            is CancelSellOfferOperation -> R.string.text_operation_name_cancel_offer
            is ManageBuyOfferOperation -> R.string.text_operation_name_manage_buy_offer
            is CreatePassiveSellOfferOperation -> R.string.text_operation_name_create_passive_sell_offer
            is SetOptionsOperation -> R.string.text_operation_name_set_options
            is ChangeTrustOperation -> R.string.text_operation_name_change_trust
            is AllowTrustOperation -> R.string.text_operation_name_allow_trust
            is AccountMergeOperation -> R.string.text_operation_name_account_merge
            is InflationOperation -> R.string.text_operation_name_inflation
            is ManageDataOperation -> R.string.text_operation_name_manage_data
            is BumpSequenceOperation -> R.string.text_operation_name_bump_sequence
            else -> -1
        }
    }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertDpToPixels(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    fun getTextHeight(t: TextView): Int {
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(screenWidth(t.context), View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        t.measure(widthMeasureSpec, heightMeasureSpec)
        return t.measuredHeight
    }

    fun screenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
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

    fun removeZeroInFractionalPart(price: String): String {
        if (TextUtils.isEmpty(price)) {
            return "0"
        }

        val builder = StringBuilder(price)
        return if (builder.indexOf(".") != -1) {
            while (builder.indexOf(".") != -1) {
                if (builder[builder.length - 1] == '0' || builder[builder.length - 1] == '.') {
                    builder.deleteCharAt(builder.length - 1)
                } else {
                    break
                }
            }
            builder.toString()
        } else {
            builder.toString()
        }
    }
}