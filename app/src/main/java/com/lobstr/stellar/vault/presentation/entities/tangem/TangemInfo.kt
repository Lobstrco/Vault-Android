package com.lobstr.stellar.vault.presentation.entities.tangem

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TangemInfo(
    var cardId: String? = null,
    var accountId: String? = null,
    var pendingTransaction: String? = null,
    var signedTransaction: String? = null,
    var message: String? = null,
    var description: String? = null,
    var cardStatus: String? = null,
    var cardStatusCode:Int? = -1
) : Parcelable {
    fun clearData() {
        cardId = null
        accountId = null
        pendingTransaction = null
        signedTransaction = null
        message = null
        description = null
        cardStatus = null
        cardStatusCode = -1
    }
}


