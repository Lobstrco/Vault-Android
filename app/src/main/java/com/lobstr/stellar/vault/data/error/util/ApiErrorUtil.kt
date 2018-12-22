package com.lobstr.stellar.vault.data.error.util

import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.internal.Primitives
import retrofit2.HttpException
import java.io.IOException

object ApiErrorUtil {

    @Nullable
    fun <T> convertJsonToPojo(@NonNull classOfT: Class<T>, @NonNull throwable: HttpException): T? {
        if (throwable.response().errorBody() == null) {
            return null
        }

        try {
            val error = throwable.response().errorBody()!!.string()
            if (TextUtils.isEmpty(error)) {
                return null
            }

            val result = Gson().fromJson(error, classOfT)
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