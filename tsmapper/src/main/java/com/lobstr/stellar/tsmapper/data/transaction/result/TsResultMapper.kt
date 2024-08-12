package com.lobstr.stellar.tsmapper.data.transaction.result

import android.content.Context
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.TsResult
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.TxResultCode
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.TxResultCode.Code.TX_FAILED
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.operation.OpResultCode
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.operation.OpResultCode.Code.OP_UNDEFINED
import org.stellar.sdk.xdr.OperationResult
import org.stellar.sdk.xdr.OperationType
import org.stellar.sdk.xdr.TransactionResult


class TsResultMapper(private val c: Context) {

    fun mapTransactionResult(result: TransactionResult?, txResultCode: String?, opResultCodes: List<String>?): TsResult {
        val txResCode = createTxResultCode(result, txResultCode)
        val opResCodes = if (txResCode.code == TX_FAILED) createOpResultCodes(result, opResultCodes) else listOf()

        return TsResult(
            txResCode,
            opResCodes
        )
    }

    fun createTxResultCode(result: TransactionResult?, txResultCode: String?): TxResultCode {
        return if (result != null) TxResultCode.create(c, result.result?.discriminant) else TxResultCode.create(c, txResultCode)
    }

    fun createOpResultCodes(result: TransactionResult?, opResultCodes: List<String>?): List<OpResultCode> {
        return if (result != null) mapOperationResults(result.result?.results) else mapOpResultCodes(opResultCodes)
    }

    fun mapOpResultCodes(codes: List<String>?): List<OpResultCode> {
        return mutableListOf<OpResultCode>().apply {
            codes?.forEach {
                add(OpResultCode.TsDefaultResultCode.create(c, it))
            }
        }
    }

    fun mapOperationResults(results: Array<OperationResult>?): List<OpResultCode> {
        return mutableListOf<OpResultCode>().apply {
            results?.forEach {
                add(mapOperationResult(it))
            }
        }
    }

    fun mapOperationResult(result: OperationResult?): OpResultCode {
        val opResultCode = when (result?.tr?.discriminant) {
            OperationType.CREATE_ACCOUNT -> OpResultCode.TsCreateAccountResultCode.create(c, result.tr.createAccountResult.discriminant)
            OperationType.PAYMENT -> OpResultCode.TsPaymentResultCode.create(c, result.tr.paymentResult.discriminant)
            OperationType.PATH_PAYMENT_STRICT_RECEIVE -> OpResultCode.TsPathPaymentStrictReceiveResultCode.create(c, result.tr.pathPaymentStrictReceiveResult.discriminant)
            OperationType.MANAGE_SELL_OFFER -> OpResultCode.TsManageSellOfferResultCode.create(c, result.tr.manageSellOfferResult.discriminant)
            OperationType.CREATE_PASSIVE_SELL_OFFER -> OpResultCode.TsCreatePassiveSellOfferResultCode.create(c, result.tr.createPassiveSellOfferResult.discriminant)
            OperationType.MANAGE_BUY_OFFER -> OpResultCode.TsManageBuyOfferResultCode.create(c, result.tr.manageBuyOfferResult.discriminant)
            OperationType.PATH_PAYMENT_STRICT_SEND -> OpResultCode.TsPathPaymentStrictSendResultCode.create(c, result.tr.pathPaymentStrictSendResult.discriminant)
            OperationType.SET_OPTIONS -> OpResultCode.TsSetOptionsResultCode.create(c, result.tr.setOptionsResult.discriminant)
            OperationType.CHANGE_TRUST -> OpResultCode.TsChangeTrustResultCode.create(c, result.tr.changeTrustResult.discriminant)
            OperationType.ALLOW_TRUST -> OpResultCode.TsAllowTrustResultCode.create(c, result.tr.allowTrustResult.discriminant)
            OperationType.ACCOUNT_MERGE -> OpResultCode.TsAccountMergeResultCode.create(c, result.tr.accountMergeResult.discriminant)
            OperationType.INFLATION -> OpResultCode.TsInflationResultCode.create(c, result.tr.inflationResult.discriminant)
            OperationType.BUMP_SEQUENCE -> OpResultCode.TsBumpSequenceResultCode.create(c, result.tr.bumpSeqResult.discriminant)
            OperationType.MANAGE_DATA -> OpResultCode.TsManageDataResultCode.create(c, result.tr.manageDataResult.discriminant)
            OperationType.CLAIM_CLAIMABLE_BALANCE -> OpResultCode.TsClaimClaimableBalanceResultCode.create(c, result.tr.claimClaimableBalanceResult.discriminant)
            OperationType.CREATE_CLAIMABLE_BALANCE -> OpResultCode.TsCreateClaimableBalanceResultCode.create(c, result.tr.createClaimableBalanceResult.discriminant)
            OperationType.BEGIN_SPONSORING_FUTURE_RESERVES -> OpResultCode.TsBeginSponsoringFutureReservesResultCode.create(c, result.tr.beginSponsoringFutureReservesResult.discriminant)
            OperationType.END_SPONSORING_FUTURE_RESERVES -> OpResultCode.TsEndSponsoringFutureReservesResultCode.create(c, result.tr.endSponsoringFutureReservesResult.discriminant)
            OperationType.REVOKE_SPONSORSHIP -> OpResultCode.TsRevokeSponsorshipResultCode.create(c, result.tr.revokeSponsorshipResult.discriminant)
            OperationType.CLAWBACK -> OpResultCode.TsClawbackResultCode.create(c, result.tr.clawbackResult.discriminant)
            OperationType.CLAWBACK_CLAIMABLE_BALANCE -> OpResultCode.TsClawbackClaimableBalanceResultCode.create(c, result.tr.clawbackClaimableBalanceResult.discriminant)
            OperationType.SET_TRUST_LINE_FLAGS -> OpResultCode.TsSetTrustLineFlagsResultCode.create(c, result.tr.setTrustLineFlagsResult.discriminant)
            OperationType.LIQUIDITY_POOL_DEPOSIT -> OpResultCode.TsLiquidityPoolDepositResultCode.create(c, result.tr.liquidityPoolDepositResult.discriminant)
            OperationType.LIQUIDITY_POOL_WITHDRAW -> OpResultCode.TsLiquidityPoolWithdrawResultCode.create(c, result.tr.liquidityPoolWithdrawResult.discriminant)
            else -> OpResultCode.TsDefaultResultCode.create(c, result?.discriminant)
        }

        // Additional check for the parsed Operation Code. Use common code and message for the OP_UNDEFINED case.
        return when (opResultCode) {
            is OpResultCode.TsDefaultResultCode -> opResultCode
            else -> {
                if (opResultCode.code == OP_UNDEFINED) {
                    OpResultCode.TsDefaultResultCode.create(c, result?.discriminant).apply {
                        opResultCode.code = code
                        opResultCode.message = message
                    }
                }
                opResultCode
            }
        }
    }
}