package com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer

import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import kotlinx.android.parcel.Parcelize

@Parcelize
class SellOfferOperation(
    override val sourceAccount: String?,
    private val selling: Asset,
    private val buying: Asset,
    private val amount: String,
    private val price: String
) : ManageSellOfferOperation(sourceAccount, selling, buying, price), Parcelable {

    override fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["selling"] = selling.assetCode
        map["buying"] = buying.assetCode
        map["amount"] = amount
        map["price"] = price

        return map
    }
}