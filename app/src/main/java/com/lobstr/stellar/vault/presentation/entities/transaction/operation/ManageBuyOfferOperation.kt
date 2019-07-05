package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import kotlinx.android.parcel.Parcelize

@Parcelize
class ManageBuyOfferOperation(
    override val sourceAccount: String?,
    private val selling: Asset,
    private val buying: Asset,
    private val amount: String,
    private val price: String,
    private val offerId: Long
) : Operation(sourceAccount), Parcelable {

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["selling"] = selling.assetCode
        map["buying"] = buying.assetCode
        map["amount"] = amount
        map["price"] = price
        map["offerId"] = offerId.toString()

        return map
    }
}