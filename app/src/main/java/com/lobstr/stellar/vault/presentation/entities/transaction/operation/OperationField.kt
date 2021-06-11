package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * @param tag Additional info for field.
 */
@Parcelize
data class OperationField(val key: String, var value: String? = null, val tag: @RawValue Any? = null) : Parcelable