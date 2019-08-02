package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentOperation(
    override val sourceAccount: String?,
    val destination: String,
    val asset: Asset,
    val amount: String,
    val memo: String
) : Operation(sourceAccount) {

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        if (destination.isNotEmpty()) map["destination"] = destination
        map["asset"] = asset.assetCode
        map["amount"] = amount
        if (memo.isNotEmpty()) {
            map["memo"] = memo
        }

        return map
    }
}