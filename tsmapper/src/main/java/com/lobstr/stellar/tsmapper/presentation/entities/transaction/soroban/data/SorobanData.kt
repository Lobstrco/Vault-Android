package com.lobstr.stellar.tsmapper.presentation.entities.transaction.soroban.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SorobanData(
    val resources: SorobanResources,
    val resourceFee: String
) : Parcelable