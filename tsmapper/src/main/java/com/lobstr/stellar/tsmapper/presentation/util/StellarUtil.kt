package com.lobstr.stellar.tsmapper.presentation.util

import org.stellar.sdk.xdr.XdrDataInputStream
import shadow.com.google.common.io.BaseEncoding
import java.io.ByteArrayInputStream


object StellarUtil {
    fun createXdrDataInputStream(data: CharSequence): XdrDataInputStream? {
        return try {
            val base64Encoding: BaseEncoding = BaseEncoding.base64()
            val bytes: ByteArray = base64Encoding.decode(data)
            val inputStream = ByteArrayInputStream(bytes)
            XdrDataInputStream(inputStream)
        } catch (exc: Exception) {
            null
        }
    }
}