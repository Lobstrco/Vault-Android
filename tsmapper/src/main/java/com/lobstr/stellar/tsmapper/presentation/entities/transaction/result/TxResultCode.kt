package com.lobstr.stellar.tsmapper.presentation.entities.transaction.result

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import kotlinx.parcelize.Parcelize
import org.stellar.sdk.xdr.TransactionResultCode

@Parcelize
data class TxResultCode(val code: String, var message: String) : Parcelable {
    object Code {
        const val TX_FEE_BUMP_INNER_SUCCESS = "tx_fee_bump_inner_success"
        const val TX_SUCCESS = "tx_success"
        const val TX_FAILED = "tx_failed"
        const val TX_TOO_EARLY = "tx_too_early"
        const val TX_TOO_LATE = "tx_too_late"
        const val TX_MISSING_OPERATION = "tx_missing_operation"
        const val TX_BAD_SEQ = "tx_bad_seq"
        const val TX_BAD_AUTH = "tx_bad_auth"
        const val TX_INSUFFICIENT_BALANCE = "tx_insufficient_balance"
        const val TX_NO_SOURCE_ACCOUNT = "tx_no_source_account"
        const val TX_INSUFFICIENT_FEE = "tx_insufficient_fee"
        const val TX_BAD_AUTH_EXTRA = "tx_bad_auth_extra"
        const val TX_INTERNAL_ERROR = "tx_internal_error"
        const val TX_NOT_SUPPORTED = "tx_not_supported"
        const val TX_FEE_BUMP_INNER_FAILED = "tx_fee_bump_inner_failed"
        const val TX_BAD_SPONSORSHIP = "tx_bad_sponsorship"
        const val TX_BAD_MIN_SEQ_AGE_OR_GAP = "tx_bad_min_seq_age_or_gap"
        const val TX_MALFORMED = "tx_malformed"
        const val TX_UNDEFINED = "tx_undefined"
        const val TX_SOROBAN_INVALID = "tx_soroban_invalid"
    }

    companion object {
        fun create(c: Context, code: TransactionResultCode?): TxResultCode =
            when (code) {
                TransactionResultCode.txFEE_BUMP_INNER_SUCCESS -> TxResultCode(
                    Code.TX_FEE_BUMP_INNER_SUCCESS,
                    c.getString(R.string.tx_fee_bump_inner_success)
                )
                TransactionResultCode.txSUCCESS -> TxResultCode(
                    Code.TX_SUCCESS,
                    c.getString(R.string.tx_success)
                )
                TransactionResultCode.txFAILED -> TxResultCode(
                    Code.TX_FAILED,
                    c.getString(R.string.tx_failed)
                )
                TransactionResultCode.txTOO_EARLY -> TxResultCode(
                    Code.TX_TOO_EARLY,
                    c.getString(R.string.tx_too_early)
                )
                TransactionResultCode.txTOO_LATE -> TxResultCode(
                    Code.TX_TOO_LATE,
                    c.getString(R.string.tx_too_late)
                )
                TransactionResultCode.txMISSING_OPERATION -> TxResultCode(
                    Code.TX_MISSING_OPERATION,
                    c.getString(R.string.tx_missing_operation)
                )
                TransactionResultCode.txBAD_SEQ -> TxResultCode(
                    Code.TX_BAD_SEQ,
                    c.getString(R.string.tx_bad_seq)
                )
                TransactionResultCode.txBAD_AUTH -> TxResultCode(
                    Code.TX_BAD_AUTH,
                    c.getString(R.string.tx_bad_auth)
                )
                TransactionResultCode.txINSUFFICIENT_BALANCE -> TxResultCode(
                    Code.TX_INSUFFICIENT_BALANCE,
                    c.getString(R.string.tx_insufficient_balance)
                )
                TransactionResultCode.txNO_ACCOUNT -> TxResultCode(
                    Code.TX_NO_SOURCE_ACCOUNT,
                    c.getString(R.string.tx_no_account)
                )
                TransactionResultCode.txINSUFFICIENT_FEE -> TxResultCode(
                    Code.TX_INSUFFICIENT_FEE,
                    c.getString(R.string.tx_insufficient_fee)
                )
                TransactionResultCode.txBAD_AUTH_EXTRA -> TxResultCode(
                    Code.TX_BAD_AUTH_EXTRA,
                    c.getString(R.string.tx_bad_auth_extra)
                )
                TransactionResultCode.txINTERNAL_ERROR -> TxResultCode(
                    Code.TX_INTERNAL_ERROR,
                    c.getString(R.string.tx_internal_error)
                )
                TransactionResultCode.txNOT_SUPPORTED -> TxResultCode(
                    Code.TX_NOT_SUPPORTED,
                    c.getString(R.string.tx_not_supported)
                )
                TransactionResultCode.txFEE_BUMP_INNER_FAILED -> TxResultCode(
                    Code.TX_FEE_BUMP_INNER_FAILED,
                    c.getString(R.string.tx_bump_inner_failed)
                )
                TransactionResultCode.txBAD_SPONSORSHIP -> TxResultCode(
                    Code.TX_BAD_SPONSORSHIP,
                    c.getString(R.string.tx_bad_sponsorship)
                )
                TransactionResultCode.txBAD_MIN_SEQ_AGE_OR_GAP -> TxResultCode(
                    Code.TX_BAD_MIN_SEQ_AGE_OR_GAP,
                    c.getString(R.string.tx_bad_min_sec_age_or_gap)
                )
                TransactionResultCode.txMALFORMED -> TxResultCode(
                    Code.TX_MALFORMED,
                    c.getString(R.string.tx_malformed)
                )
                TransactionResultCode.txSOROBAN_INVALID -> TxResultCode(
                    Code.TX_SOROBAN_INVALID,
                    c.getString(R.string.tx_soroban_invalid)
                )
                else -> TxResultCode(Code.TX_UNDEFINED, c.getString(R.string.tx_undefined))
            }

        fun create(c: Context, code: String?): TxResultCode = when (code) {
            Code.TX_FEE_BUMP_INNER_SUCCESS -> TxResultCode(
                code,
                c.getString(R.string.tx_fee_bump_inner_success)
            )
            Code.TX_SUCCESS -> TxResultCode(
                code,
                c.getString(R.string.tx_success)
            )
            Code.TX_FAILED -> TxResultCode(
                code,
                c.getString(R.string.tx_failed)
            )
            Code.TX_TOO_EARLY -> TxResultCode(
                code,
                c.getString(R.string.tx_too_early)
            )
            Code.TX_TOO_LATE -> TxResultCode(
                code,
                c.getString(R.string.tx_too_late)
            )
            Code.TX_MISSING_OPERATION -> TxResultCode(
                code,
                c.getString(R.string.tx_missing_operation)
            )
            Code.TX_BAD_SEQ -> TxResultCode(
                code,
                c.getString(R.string.tx_bad_seq)
            )
            Code.TX_BAD_AUTH -> TxResultCode(
                code,
                c.getString(R.string.tx_bad_auth)
            )
            Code.TX_INSUFFICIENT_BALANCE -> TxResultCode(
                code,
                c.getString(R.string.tx_insufficient_balance)
            )
            Code.TX_NO_SOURCE_ACCOUNT -> TxResultCode(
                code,
                c.getString(R.string.tx_no_account)
            )
            Code.TX_INSUFFICIENT_FEE -> TxResultCode(
                code,
                c.getString(R.string.tx_insufficient_fee)
            )
            Code.TX_BAD_AUTH_EXTRA -> TxResultCode(
                code,
                c.getString(R.string.tx_bad_auth_extra)
            )
            Code.TX_INTERNAL_ERROR -> TxResultCode(
                code,
                c.getString(R.string.tx_internal_error)
            )
            Code.TX_NOT_SUPPORTED -> TxResultCode(
                code,
                c.getString(R.string.tx_not_supported)
            )
            Code.TX_FEE_BUMP_INNER_FAILED -> TxResultCode(
                code,
                c.getString(R.string.tx_bump_inner_failed)
            )
            Code.TX_BAD_SPONSORSHIP -> TxResultCode(
                code,
                c.getString(R.string.tx_bad_sponsorship)
            )
            Code.TX_BAD_MIN_SEQ_AGE_OR_GAP -> TxResultCode(
                code,
                c.getString(R.string.tx_bad_min_sec_age_or_gap)
            )
            Code.TX_MALFORMED -> TxResultCode(
                code,
                c.getString(R.string.tx_malformed)
            )
            Code.TX_SOROBAN_INVALID -> TxResultCode(
                code,
                c.getString(R.string.tx_soroban_invalid)
            )
            else -> TxResultCode(Code.TX_UNDEFINED, c.getString(R.string.tx_undefined))
        }
    }
}