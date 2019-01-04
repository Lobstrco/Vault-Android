package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcel
import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset

data class ManageOfferOperation(
    override val sourceAccount: String?,
    val selling: Asset,
    val buying: Asset,
    val amount: String,
    val price: String,
    val offerId: Long
) : Operation(sourceAccount), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Asset::class.java.classLoader),
        parcel.readParcelable(Asset::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(sourceAccount)
        parcel.writeParcelable(selling, flags)
        parcel.writeParcelable(buying, flags)
        parcel.writeString(amount)
        parcel.writeString(price)
        parcel.writeLong(offerId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ManageOfferOperation> {
        override fun createFromParcel(parcel: Parcel): ManageOfferOperation {
            return ManageOfferOperation(parcel)
        }

        override fun newArray(size: Int): Array<ManageOfferOperation?> {
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
        map["offerId"] = offerId.toString()

        return map
    }
}