package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class SCVec(val scVec: List<SCVal> = listOf()) : Parcelable