package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcel
import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset

data class CreatePassiveOfferOperation(
    override val sourceAccount: String?,
    val selling: Asset,
    val buying: Asset,
    val amount: String,
    val price: String
) : Operation(sourceAccount), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Asset::class.java.classLoader),
        parcel.readParcelable(Asset::class.java.classLoader),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(sourceAccount)
        parcel.writeParcelable(selling, flags)
        parcel.writeParcelable(buying, flags)
        parcel.writeString(amount)
        parcel.writeString(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CreatePassiveOfferOperation> {
        override fun createFromParcel(parcel: Parcel): CreatePassiveOfferOperation {
            return CreatePassiveOfferOperation(parcel)
        }

        override fun newArray(size: Int): Array<CreatePassiveOfferOperation?> {
            return arrayOfNulls(size)
        }
    }

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["sourceAccount"] = sourceAccount
        map["selling"] = selling.assetCode
        map["buying"] = buying.assetCode
        map["amount"] = amount
        map["price"] = price

        return map
    }
}