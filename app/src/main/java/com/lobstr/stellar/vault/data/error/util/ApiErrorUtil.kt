package com.lobstr.stellar.vault.data.error.util

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.internal.Primitives
import java.io.IOException

object ApiErrorUtil {
    fun <T> convertJsonToPojo(classOfT: Class<T>, errorBody: String?): T? {
        try {
            if (TextUtils.isEmpty(errorBody)) {
                return null
            }
            val `object` = Gson().fromJson(errorBody, classOfT)
            return Primitives.wrap(classOfT).cast(`object`)
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