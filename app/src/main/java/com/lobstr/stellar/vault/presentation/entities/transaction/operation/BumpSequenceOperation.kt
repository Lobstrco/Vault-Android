package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcel
import android.os.Parcelable

data class BumpSequenceOperation(
    override val sourceAccount: String?,
    val bumpTo: Long
) : Operation(sourceAccount), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(sourceAccount)
        parcel.writeLong(bumpTo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BumpSequenceOperation> {
        override fun createFromParcel(parcel: Parcel): BumpSequenceOperation {
            return BumpSequenceOperation(parcel)
        }

        override fun newArray(size: Int): Array<BumpSequenceOperation?> {
            return arrayOfNulls(size)
        }
    }

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["sourceAccount"] = sourceAccount
        map["bumpTo"] = bumpTo.toString()

        return map
    }
}