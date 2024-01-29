package com.lobstr.stellar.tsmapper.presentation.util

import android.util.Base64
import org.stellar.sdk.xdr.XdrDataInputStream
import java.io.ByteArrayInputStream

fun String.createXdrDataInputStream(): XdrDataInputStream? {
    return try {
        XdrDataInputStream(ByteArrayInputStream(Base64.decode(this, Base64.DEFAULT)))
    } catch (exc: Exception) {
        null
    }
}