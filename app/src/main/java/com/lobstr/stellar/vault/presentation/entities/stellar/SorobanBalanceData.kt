package com.lobstr.stellar.vault.presentation.entities.stellar

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import kotlinx.parcelize.Parcelize

@Parcelize
open class SorobanBalanceData(
    val asset: Asset,
    val beforeAmount: String,
    val afterAmount: String
) : Parcelable