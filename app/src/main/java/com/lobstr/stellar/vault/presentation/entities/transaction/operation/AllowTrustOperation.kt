package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcel
import android.os.Parcelable

data class AllowTrustOperation(
    override val sourceAccount: String?,
    val trustor: String,
    val assetCode: String,
    val authorize: Boolean
) : Operation(sourceAccount), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(sourceAccount)
        parcel.writeString(trustor)
        parcel.writeString(assetCode)
        parcel.writeByte(if (authorize) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllowTrustOperation> {
        override fun createFromParcel(parcel: Parcel): AllowTrustOperation {
            return AllowTrustOperation(parcel)
        }

        override fun newArray(size: Int): Array<AllowTrustOperation?> {
            return arrayOfNulls(size)
        }
    }

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["sourceAccount"] = sourceAccount
        map["trustor"] = trustor
        map["assetCode"] = assetCode
        map["authorize"] = authorize.toString()

        return map
    }
}