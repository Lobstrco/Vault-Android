package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.util.AppUtil.removeZeroInFractionalPart
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.math.MathContext

@Parcelize
data class ManageSellOfferOperation(
    override val sourceAccount: String?,
    val selling: Asset,
    val buying: Asset,
    val amount: String,
    val price: String,
    val offerId: Long
) : Operation(sourceAccount), Parcelable {

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["selling"] = selling.assetCode
        map["buying"] = buying.assetCode
        map["amount"] = amount
        map["price"] = calculatePrice(selling.assetCode, buying.assetCode, price)
        map["offerId"] = offerId.toString()

        return map
    }

    // NOTE: REMOVE calculate price logic for new trade api in future
    @Deprecated("Remove")
    private fun calculatePrice(sellingAssetCode: String, buyingAssetCode: String, price: String): String {
        val mathContext = MathContext(10)
        val bdOne = BigDecimal(1, mathContext)
        val bdPrice = BigDecimal(price, mathContext)
        val revertedPrice = bdOne.divide(bdPrice, mathContext).setScale(7, BigDecimal.ROUND_DOWN).toString()

        return when {
            sellingAssetCode == "XLM" -> removeZeroInFractionalPart(revertedPrice)
            buyingAssetCode == "XLM" -> price
            else -> price
        }
    }
}