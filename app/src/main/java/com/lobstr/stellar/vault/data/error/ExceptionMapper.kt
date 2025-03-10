package com.lobstr.stellar.vault.data.error

import android.content.Context
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.entity.Error
import com.lobstr.stellar.vault.data.error.exeption.*
import com.lobstr.stellar.vault.data.error.util.ApiErrorUtil
import com.lobstr.stellar.vault.data.error.util.HttpStatusCodes
import com.lobstr.stellar.vault.presentation.util.NetworkUtil
import org.stellar.sdk.exception.BadResponseException
import org.stellar.sdk.exception.ConnectionErrorException
import org.stellar.sdk.exception.SdkException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ExceptionMapper(private val context: Context) {

    fun transformHttpToDomain(throwable: Throwable): Exception {
        // For case when called several transformHttpToDomain() in Observable/Single/Completable.
        if (throwable is DefaultException) {
            return throwable
        }

        if (throwable is HttpException) {
            val error = ApiErrorUtil.convertJsonToPojo(
                Error::class.java,
                throwable.response()?.errorBody()?.string()
            )

            when {
                throwable.code() == HttpStatusCodes.HTTP_NOT_FOUND -> return HttpNotFoundException(context.getString(R.string.api_error_not_found))
                throwable.code() == HttpStatusCodes.HTTP_UNAUTHORIZED -> return UserNotAuthorizedException(
                    context.getString(
                        R.string.api_error_user_not_authorized
                    )
                )
                throwable.code() == HttpStatusCodes.HTTP_FORBIDDEN -> return getHttpForbiddenError(error, throwable)
                throwable.code() == HttpStatusCodes.HTTP_BAD_REQUEST -> return getHttpBadRequestError(error, throwable)
                throwable.code() >= HttpStatusCodes.HTTP_INTERNAL_ERROR -> return InternalException(context.getString(R.string.api_error_internal_default))
            }
        }

        if (throwable is IOException) {
            // A network or conversion error happened.
            return when (throwable) {
                is SocketTimeoutException -> HttpTimeOutException(context.getString(R.string.api_error_connection_timeout))
                is UnknownHostException, is ConnectException -> {
                    if (NetworkUtil.isConnected(context = context)) DefaultException(
                        throwable.message ?: ""
                    )
                    else NoInternetConnectionException(context.getString(R.string.api_error_connection_error))
                }
                else -> DefaultException(throwable.message!!)
            }
        }

        if (throwable is SdkException) {
            return handleStellarSdkException(throwable)
        }

        return DefaultException(throwable.message!!)
    }

    private fun getHttpForbiddenError(error: Error?, throwable: Throwable): DefaultException {
        return when {
            error == null -> DefaultException(throwable.message!!)
            !error.detail.isNullOrEmpty() && error.detail.contains(context.getString(R.string.api_error_invalid_token)) ->
                UserNotAuthorizedException(context.getString(R.string.api_error_user_not_authorized))
            !error.message.isNullOrEmpty() -> ForbiddenException(error.message)
            !error.detail.isNullOrEmpty() -> ForbiddenException(error.detail)
            !error.error.isNullOrEmpty() -> ForbiddenException(error.error)
            !error.nonFieldErrors.isNullOrEmpty() -> ForbiddenException(error.nonFieldErrors[0])
            else -> DefaultException(throwable.message!!)
        }
    }

    private fun getHttpBadRequestError(error: Error?, throwable: Throwable): DefaultException {
        return when {
            error == null -> DefaultException(context.getString(R.string.api_error_internal_default))
            !error.message.isNullOrEmpty() -> BadRequestException(error.message)
            !error.detail.isNullOrEmpty() -> BadRequestException(error.detail)
            !error.error.isNullOrEmpty() -> BadRequestException(error.error)
            !error.nonFieldErrors.isNullOrEmpty() && error.nonFieldErrors[0].equals("Signature has expired.") ->
                ExpiredSignatureException(error.nonFieldErrors[0])
            !error.nonFieldErrors.isNullOrEmpty() -> BadRequestException(error.nonFieldErrors[0])
            else -> DefaultException(throwable.message!!)
        }
    }

    private fun handleStellarSdkException(throwable: SdkException): Exception = when (throwable) {
        is ConnectionErrorException -> {
            if (NetworkUtil.isConnected(context = context)) DefaultException(
                throwable.message ?: ""
            )
            else NoInternetConnectionException(context.getString(R.string.api_error_connection_error))
        }
        is BadResponseException -> InternalException(context.getString(R.string.api_error_internal_default)) // 5xx
        is org.stellar.sdk.exception.BadRequestException -> {
            when (throwable.code) {
                HttpStatusCodes.HTTP_NOT_FOUND -> HttpNotFoundException(
                    throwable.problem?.detail ?: context.getString(R.string.api_error_not_found)
                )
                else -> BadRequestException(throwable.problem?.detail ?: throwable.message ?: "")
            }
        }

        else -> throwable
    }
}