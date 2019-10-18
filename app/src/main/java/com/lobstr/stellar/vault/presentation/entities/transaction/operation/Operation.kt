package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Operation(open val sourceAccount: String?) : Parcelable