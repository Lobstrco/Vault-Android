package com.lobstr.stellar.tsmapper.presentation.entities.transaction.result

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.operation.OpResultCode
import kotlinx.parcelize.Parcelize


@Parcelize
data class TsResult(val txResultCode: TxResultCode, val opResultCodes: List<OpResultCode>) : Parcelable