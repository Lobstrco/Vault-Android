package com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer

import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.util.AppUtil
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.math.MathContext

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
        map["price"] = calculatePrice(selling.assetCode, buying.assetCode, price)

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
            sellingAssetCode == "XLM" -> AppUtil.removeZeroInFractionalPart(revertedPrice)
            buyingAssetCode == "XLM" -> price
            else -> price
        }
    }
}