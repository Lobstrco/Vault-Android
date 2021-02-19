package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OperationField(val key: String, var value: String? = null) : Parcelable