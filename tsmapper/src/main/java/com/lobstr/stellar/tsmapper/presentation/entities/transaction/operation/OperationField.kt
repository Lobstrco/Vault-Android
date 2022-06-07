package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * @param tag Additional info for field.
 */
@Parcelize
data class OperationField(var key: String, var value: String? = null, var tag: @RawValue Any? = null) : Parcelable