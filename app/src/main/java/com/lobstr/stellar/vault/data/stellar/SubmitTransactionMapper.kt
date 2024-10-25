package com.lobstr.stellar.vault.data.stellar

import com.lobstr.stellar.tsmapper.data.transaction.result.TsResultMapper
import com.lobstr.stellar.vault.presentation.entities.stellar.SubmitTransactionResult
import org.stellar.sdk.exception.UnexpectedException
import org.stellar.sdk.responses.Problem.Extras
import org.stellar.sdk.responses.TransactionResponse

class SubmitTransactionMapper(private val tsResultMapper: TsResultMapper) {
    fun transformSubmitResponse(response: TransactionResponse): SubmitTransactionResult {
        return SubmitTransactionResult(
            response.envelopeXdr,
            response.hash,
            tsResultMapper.mapTransactionResult(
                try {
                    response.parseResultXdr()
                } catch (exc: UnexpectedException) {
                    null
                },
                null,
                null
            )
        )
    }

    fun transformSubmitResponse(extras: Extras): SubmitTransactionResult {
        return SubmitTransactionResult(
            extras.envelopeXdr,
            extras.hash,
            tsResultMapper.mapTransactionResult(
                null,
                extras.resultCodes.transactionResultCode,
                extras.resultCodes.operationsResultCodes
            )
        )
    }
}