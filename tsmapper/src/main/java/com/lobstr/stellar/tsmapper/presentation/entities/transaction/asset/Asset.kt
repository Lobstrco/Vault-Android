package com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class Asset(
    val assetCode: String,
    val assetType: String,
    val assetIssuer: String?
) : Parcelable