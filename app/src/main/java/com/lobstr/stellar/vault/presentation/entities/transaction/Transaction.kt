package com.lobstr.stellar.vault.presentation.entities.transaction

import android.os.Parcel
import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.Operation


data class Transaction(
    val operations: List<Operation>,
    var mSequenceNumber: Long = 0
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(Operation.CREATOR),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(operations)
        parcel.writeLong(mSequenceNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Transaction> {
        override fun createFromParcel(parcel: Parcel): Transaction {
            return Transaction(parcel)
        }

        override fun newArray(size: Int): Array<Transaction?> {
            return arrayOfNulls(size)
        }
    }
}