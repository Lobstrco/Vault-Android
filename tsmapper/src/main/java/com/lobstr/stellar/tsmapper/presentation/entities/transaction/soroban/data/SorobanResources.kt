package com.lobstr.stellar.tsmapper.presentation.entities.transaction.soroban.data

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.ledger.LedgerFootprint
import kotlinx.parcelize.Parcelize

@Parcelize
class SorobanResources(
    val footprint: LedgerFootprint,
    val instructions: String,
    val readBytes: String,
    val writeBytes: String
) : Parcelable