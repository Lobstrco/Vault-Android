package com.lobstr.stellar.vault.presentation.entities.tangem

import android.os.Parcelable
import com.tangem.common.card.EllipticCurve
import kotlinx.parcelize.Parcelize

@Parcelize
class TangemInfo(
    var cardId: String? = null,
    var accountId: String? = null,
    var pendingTransaction: String? = null,
    var signedTransaction: String? = null,
    var message: String? = null,
    var description: String? = null,
    var cardStatusCode: Int? = -1,
    var curve: EllipticCurve? = null
) : Parcelable {
    fun clearData() {
        cardId = null
        accountId = null
        pendingTransaction = null
        signedTransaction = null
        message = null
        description = null
        cardStatusCode = -1
        curve = null
    }
}


