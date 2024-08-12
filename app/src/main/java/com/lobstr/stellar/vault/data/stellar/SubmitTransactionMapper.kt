package com.lobstr.stellar.vault.data.stellar

import com.lobstr.stellar.tsmapper.data.transaction.result.TsResultMapper
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.TxResultCode
import com.lobstr.stellar.tsmapper.presentation.util.createXdrDataInputStream
import com.lobstr.stellar.vault.presentation.entities.stellar.SubmitTransactionResult
import org.stellar.sdk.exception.BadRequestException
import org.stellar.sdk.responses.TransactionResponse
import org.stellar.sdk.xdr.TransactionResult
import java.io.IOException


class SubmitTransactionMapper(private val tsResultMapper: TsResultMapper) {
    @OptIn(ExperimentalStdlibApi::class)
    fun transformSubmitResponse(response: TransactionResponse): SubmitTransactionResult {
        return SubmitTransactionResult(
            response.envelopeXdr,
            response.hash,
            tsResultMapper.mapTransactionResult(
                response.parseResultXdr(),
                TxResultCode.Code.TX_SUCCESS,
                null,
            )
        )
    }

    fun transformBadRequestException(
        throwable: BadRequestException,
        envelopeXdr: String,
        hash: String
    ): SubmitTransactionResult {
        return SubmitTransactionResult(
            envelopeXdr,
            hash,
            tsResultMapper.mapTransactionResult(
                throwable.problem?.extras?.resultXdr?.run {
                    createXdrDataInputStream()?.run {
                        try {
                            TransactionResult.decode(this)
                        } catch (exc: IOException) {
                            null
                        }
                    }
                },
                throwable.problem?.extras?.resultCodes?.transactionResultCode,
                throwable.problem?.extras?.resultCodes?.operationsResultCodes
            )
        )
    }
}