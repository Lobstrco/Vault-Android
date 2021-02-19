package com.lobstr.stellar.vault.presentation.entities.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Asset(
    val assetCode: String,
    val assetType: String,
    val assetIssuer: String?
) : Parcelable