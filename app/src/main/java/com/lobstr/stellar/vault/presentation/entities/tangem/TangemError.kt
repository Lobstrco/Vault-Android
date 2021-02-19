package com.lobstr.stellar.vault.presentation.entities.tangem

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TangemError(
    var errorCode: Int = -1,
    var errorMod: Int = -1,
    var errorTitle: String? = null,
    var errorDescription: String? = null
) : Parcelable {
    fun clearData() {
        errorCode= -1
        errorMod = -1
        errorTitle = null
        errorDescription = null
    }
}