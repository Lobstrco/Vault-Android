package com.lobstr.stellar.vault.data.stellar

import com.lobstr.stellar.tsmapper.data.transaction.result.TsResultMapper
import com.lobstr.stellar.tsmapper.presentation.util.createXdrDataInputStream
import com.lobstr.stellar.vault.presentation.entities.stellar.SubmitTransactionResult
import org.stellar.sdk.responses.SubmitTransactionResponse
import org.stellar.sdk.xdr.TransactionResult
import java.io.IOException
import kotlin.jvm.optionals.getOrNull

class SubmitTransactionMapper(private val tsResultMapper: TsResultMapper) {
    @OptIn(ExperimentalStdlibApi::class)
    fun transformSubmitResponse(response: SubmitTransactionResponse): SubmitTransactionResult {
        return SubmitTransactionResult(
            response.envelopeXdr.getOrNull(),
            response.hash,
            tsResultMapper.mapTransactionResult(
                response.decodedTransactionResult.getOrNull() ?: response.resultXdr.getOrNull()?.run {
                    createXdrDataInputStream()?.run {
                        try {
                            TransactionResult.decode(this)
                        } catch (exc: IOException) {
                            null
                        }
                    }
                },
                response.extras?.resultCodes?.transactionResultCode,
                response.extras?.resultCodes?.operationsResultCodes
            )
        )
    }
}