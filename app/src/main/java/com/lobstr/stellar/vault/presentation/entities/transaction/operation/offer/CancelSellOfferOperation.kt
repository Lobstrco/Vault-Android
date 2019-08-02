package com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer

import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CancelSellOfferOperation(
    override val sourceAccount: String?,
    private val selling: Asset,
    private val buying: Asset,
    private val price: String,
    val offerId: Long
) : ManageSellOfferOperation(sourceAccount, selling, buying, price), Parcelable {

    override fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["selling"] = selling.assetCode
        map["buying"] = buying.assetCode
        map["price"] = price
        map["offerId"] = offerId.toString()

        return map
    }
}