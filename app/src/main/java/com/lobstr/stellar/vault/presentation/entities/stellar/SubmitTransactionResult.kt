package com.lobstr.stellar.vault.presentation.entities.stellar

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.TsResult
import kotlinx.parcelize.Parcelize

@Parcelize
class SubmitTransactionResult(
    val envelopXdr: String?,
    val hash: String?,
    val tsResult: TsResult
) : Parcelable