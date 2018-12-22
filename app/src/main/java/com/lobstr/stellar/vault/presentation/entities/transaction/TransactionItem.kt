package com.lobstr.stellar.vault.presentation.entities.transaction

import android.os.Parcel
import android.os.Parcelable


data class TransactionItem(

    val cancelledAt: String?,

    val addedAt: String?,

    val xdr: String?,

    val signedAt: String?,

    val hash: String,

    val getStatusDisplay: String?,

    val status: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cancelledAt)
        parcel.writeString(addedAt)
        parcel.writeString(xdr)
        parcel.writeString(signedAt)
        parcel.writeString(hash)
        parcel.writeString(getStatusDisplay)
        parcel.writeValue(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TransactionItem> {
        override fun createFromParcel(parcel: Parcel): TransactionItem {
            return TransactionItem(parcel)
        }

        override fun newArray(size: Int): Array<TransactionItem?> {
            return arrayOfNulls(size)
        }
    }
}