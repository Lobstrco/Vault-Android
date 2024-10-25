package com.lobstr.stellar.tsmapper.presentation.entities.transaction.ledger

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
class LedgerFootprint(
    val readOnly: List<LedgerKey> = listOf(),
    val readWrite: List<LedgerKey> = listOf()
) : Parcelable