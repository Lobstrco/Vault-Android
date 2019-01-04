package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcel
import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset

data class PaymentOperation(
    override val sourceAccount: String?,
    val destination: String,
    val asset: Asset,
    val amount: String
) : Operation(sourceAccount), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Asset::class.java.classLoader),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(sourceAccount)
        parcel.writeString(destination)
        parcel.writeParcelable(asset, flags)
        parcel.writeString(amount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentOperation> {
        override fun createFromParcel(parcel: Parcel): PaymentOperation {
            return PaymentOperation(parcel)
        }

        override fun newArray(size: Int): Array<PaymentOperation?> {
            return arrayOfNulls(size)
        }
    }

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["sourceAccount"] = sourceAccount
        map["destination"] = destination
        map["asset"] = asset.assetCode
        map["amount"] = amount

        return map
    }
}