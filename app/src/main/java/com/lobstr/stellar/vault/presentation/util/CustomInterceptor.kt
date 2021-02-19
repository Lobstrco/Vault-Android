package com.lobstr.stellar.vault.presentation.util

import okhttp3.Interceptor
import okhttp3.Response


class CustomInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .header("Referer", "https://vault.lobstr.co/")
                .build()
        )
    }
}