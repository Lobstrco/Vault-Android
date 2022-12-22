package com.lobstr.stellar.vault.data.stellar

import com.lobstr.stellar.tsmapper.data.transaction.result.TsResultMapper
import com.lobstr.stellar.tsmapper.presentation.util.StellarUtil
import com.lobstr.stellar.vault.presentation.entities.stellar.SubmitTransactionResult
import org.stellar.sdk.responses.SubmitTransactionResponse
import org.stellar.sdk.xdr.TransactionResult
import java.io.IOException

class SubmitTransactionMapper(private val tsResultMapper: TsResultMapper) {
    fun transformSubmitResponse(response: SubmitTransactionResponse): SubmitTransactionResult {
        return SubmitTransactionResult(
            response.envelopeXdr.orNull(),
            response.hash,
            tsResultMapper.mapTransactionResult(
                response.decodedTransactionResult.orNull() ?: response.resultXdr.orNull()?.run {
                    StellarUtil.createXdrDataInputStream(this)?.run {
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