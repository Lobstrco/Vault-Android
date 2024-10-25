package com.lobstr.stellar.tsmapper.presentation.util

import android.content.Context
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.TsMemo
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.*
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.claimable_balance.ClaimClaimableBalanceOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.claimable_balance.CreateClaimableBalanceOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.clawback.ClawbackClaimableBalanceOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.clawback.ClawbackOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.liquidity_pool.LiquidityPoolDepositOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.liquidity_pool.LiquidityPoolWithdrawOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.offer.*
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.ExtendFootprintTTLOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.InvokeHostFunctionOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.RestoreFootprintOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring.*
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.operation.OpResultCode
import com.lobstr.stellar.tsmapper.presentation.util.Constant.Util.UNDEFINED_VALUE
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


object TsUtil {
    /**
     * Used for receive operation Name.
     * @param operation Operation.
     */
    fun getTransactionOperationName(operation: Operation): Int {
        return when (operation) {
            is PaymentOperation -> R.string.operation_name_payment
            is CreateAccountOperation -> R.string.operation_name_create_account
            is PathPaymentStrictSendOperation -> R.string.operation_name_path_payment_strict_send
            is PathPaymentStrictReceiveOperation -> R.string.operation_name_path_payment_strict_receive
            is SellOfferOperation -> R.string.operation_name_sell_offer
            is CancelSellOfferOperation -> R.string.operation_name_cancel_offer
            is BuyOfferOperation -> R.string.operation_name_buy_offer
            is CancelBuyOfferOperation -> R.string.operation_name_cancel_offer
            is CreatePassiveSellOfferOperation -> R.string.operation_name_create_passive_sell_offer
            is SetOptionsOperation -> R.string.operation_name_set_options
            is ChangeTrustOperation -> R.string.operation_name_change_trust
            is AllowTrustOperation -> R.string.operation_name_allow_trust
            is SetTrustlineFlagsOperation -> R.string.operation_name_set_trustline_flags
            is AccountMergeOperation -> R.string.operation_name_account_merge
            is InflationOperation -> R.string.operation_name_inflation
            is ManageDataOperation -> R.string.operation_name_manage_data
            is BumpSequenceOperation -> R.string.operation_name_bump_sequence
            is BeginSponsoringFutureReservesOperation -> R.string.operation_name_begin_sponsoring_future_reserves
            is EndSponsoringFutureReservesOperation -> R.string.operation_name_end_sponsoring_future_reserves
            is RevokeAccountSponsorshipOperation -> R.string.operation_name_revoke_account_sponsorship
            is RevokeClaimableBalanceSponsorshipOperation -> R.string.operation_name_revoke_claimable_balance_sponsorship
            is RevokeDataSponsorshipOperation -> R.string.operation_name_revoke_data_sponsorship
            is RevokeOfferSponsorshipOperation -> R.string.operation_name_revoke_offer_sponsorship
            is RevokeSignerSponsorshipOperation -> R.string.operation_name_revoke_signer_sponsorship
            is RevokeTrustlineSponsorshipOperation -> R.string.operation_name_revoke_trustline_sponsorship
            is CreateClaimableBalanceOperation -> R.string.operation_name_create_claimable_balance
            is ClaimClaimableBalanceOperation -> R.string.operation_name_claim_claimable_balance
            is ClawbackClaimableBalanceOperation -> R.string.operation_name_clawback_claimable_balance
            is ClawbackOperation -> R.string.operation_name_clawback
            is LiquidityPoolDepositOperation -> R.string.operation_name_liquidity_pool_deposit
            is LiquidityPoolWithdrawOperation -> R.string.operation_name_liquidity_pool_withdraw
            is ExtendFootprintTTLOperation -> R.string.operation_name_extend_footprint_ttl
            is RestoreFootprintOperation -> R.string.operation_name_restore_footprint
            is InvokeHostFunctionOperation -> R.string.operation_name_invoke_host_function

            else -> UNDEFINED_VALUE
        }
    }


        /**
         * Used for receive operation Name.
         * @param opResultCode Operation Result Code.
         */
        fun getTransactionOperationName(opResultCode: OpResultCode): Int {
            return when (opResultCode) {
                is OpResultCode.TsPaymentResultCode -> R.string.operation_name_payment
                is OpResultCode.TsCreateAccountResultCode -> R.string.operation_name_create_account
                is OpResultCode.TsPathPaymentStrictSendResultCode -> R.string.operation_name_path_payment_strict_send
                is OpResultCode.TsPathPaymentStrictReceiveResultCode -> R.string.operation_name_path_payment_strict_receive
                is OpResultCode.TsCreatePassiveSellOfferResultCode -> R.string.operation_name_create_passive_sell_offer
                is OpResultCode.TsManageSellOfferResultCode -> R.string.operation_name_manage_sell_offer
                is OpResultCode.TsManageBuyOfferResultCode -> R.string.operation_name_manage_buy_offer
                is OpResultCode.TsSetOptionsResultCode -> R.string.operation_name_set_options
                is OpResultCode.TsChangeTrustResultCode -> R.string.operation_name_change_trust
                is OpResultCode.TsAllowTrustResultCode -> R.string.operation_name_allow_trust
                is OpResultCode.TsSetTrustLineFlagsResultCode -> R.string.operation_name_set_trustline_flags
                is OpResultCode.TsAccountMergeResultCode -> R.string.operation_name_account_merge
                is OpResultCode.TsInflationResultCode -> R.string.operation_name_inflation
                is OpResultCode.TsManageDataResultCode -> R.string.operation_name_manage_data
                is OpResultCode.TsBumpSequenceResultCode -> R.string.operation_name_bump_sequence
                is OpResultCode.TsBeginSponsoringFutureReservesResultCode -> R.string.operation_name_begin_sponsoring_future_reserves
                is OpResultCode.TsEndSponsoringFutureReservesResultCode -> R.string.operation_name_end_sponsoring_future_reserves
                is OpResultCode.TsRevokeSponsorshipResultCode -> R.string.operation_name_revoke_sponsorship
                is OpResultCode.TsCreateClaimableBalanceResultCode -> R.string.operation_name_create_claimable_balance
                is OpResultCode.TsClaimClaimableBalanceResultCode -> R.string.operation_name_claim_claimable_balance
                is OpResultCode.TsClawbackClaimableBalanceResultCode -> R.string.operation_name_clawback_claimable_balance
                is OpResultCode.TsClawbackResultCode -> R.string.operation_name_clawback
                is OpResultCode.TsLiquidityPoolDepositResultCode -> R.string.operation_name_liquidity_pool_deposit
                is OpResultCode.TsLiquidityPoolWithdrawResultCode -> R.string.operation_name_liquidity_pool_withdraw
                is OpResultCode.TsExtendFootprintTTLResultCode -> R.string.operation_name_extend_footprint_ttl
                is OpResultCode.TsRestoreFootprintResultCode -> R.string.operation_name_restore_footprint
                is OpResultCode.TsInvokeHostFunctionResultCode -> R.string.operation_name_invoke_host_function

                else -> UNDEFINED_VALUE
            }
        }

    /**
     * Used for receive memo type.
     * @param memo Target Memo.
     */
    fun getMemoTypeStr(context: Context, memo: TsMemo): String {
        return when (memo) {
            is TsMemo.MEMO_TEXT -> context.getString(R.string.transaction_memo_text)
            is TsMemo.MEMO_ID -> context.getString(R.string.transaction_memo_id)
            is TsMemo.MEMO_HASH -> context.getString(R.string.transaction_memo_hash)
            is TsMemo.MEMO_RETURN -> context.getString(R.string.transaction_memo_return)
            is TsMemo.MEMO_NONE -> ""
        }
    }

    fun ellipsizeStrInMiddle(str: String?, count: Int): String? {
        return if (str.isNullOrEmpty() || count >= (str.length / 2 - 1)) {
            str
        } else str.substring(0, count) + "…" + str.substring(str.length - count)
    }

    fun formatDate(date: Date, datePattern: String): String? {
        try {
            val format =
                SimpleDateFormat(datePattern, Locale(Locale.getDefault().language))
            return format.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * Get Amount representation for the provided String value.
     * @param minFractionDigits Min fraction digits. -1 - use locale currency rule (.xx).
     * @return Formatted amount or original 'number' value in case exception or empty string.
     */
    fun getFormattedAmount(number: String, locale: Locale = Locale.US, minFractionDigits: Int = 0, maxFractionDigits: Int = 12, rounding: RoundingMode = RoundingMode.HALF_EVEN): String {
        return try {
            number.trim().let { if (it.isNotEmpty()) getCurrencyFormatInstance(locale, minFractionDigits, maxFractionDigits, rounding).format(BigDecimal(it)).trim() else number }
        } catch (exc: Exception) {
            number
        }
    }

    fun getAmountRepresentationFromStr(number: String): String {
        return getFormattedAmount(number, maxFractionDigits = 7, rounding = RoundingMode.DOWN)
    }

    /**
     * Method for parsing formatted string (1.000.000,50 - 1000000.5)
     * @param number String representation of the number. Can be usual number like 1000.50 for avoiding wrong number parsing.
     */
    fun parseFormattedAmount(number: String, locale: Locale = Locale.US, minFractionDigits: Int = 0, maxFractionDigits: Int = 12): String {
        val formatInstance =  getCurrencyFormatInstance(locale, minFractionDigits, maxFractionDigits)

        return try {
            number.replace(formatInstance.decimalFormatSymbols.groupingSeparator.toString(), "").replace(formatInstance.decimalFormatSymbols.decimalSeparator.toString(), ".").replace(formatInstance.decimalFormatSymbols.currencySymbol, "").trim()
        } catch (exc: Exception) {
            "0"
        }
    }

    /**
     * Get Currency Format Instance.
     * @param minFractionDigits Min fraction digits. -1 - use locale currency rule (.xx).
     */
    private fun getCurrencyFormatInstance(locale: Locale = Locale.US, minFractionDigits: Int = 0, maxFractionDigits: Int = 12, rounding: RoundingMode = RoundingMode.HALF_EVEN): DecimalFormat {
        return (NumberFormat.getCurrencyInstance(locale) as DecimalFormat).apply {
            if(minFractionDigits != -1) minimumFractionDigits = minFractionDigits
            maximumFractionDigits = maxFractionDigits
            roundingMode = rounding
            decimalFormatSymbols = decimalFormatSymbols.apply { currencySymbol = "" }
        }
    }
}