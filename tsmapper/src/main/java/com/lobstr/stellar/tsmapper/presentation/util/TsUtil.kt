package com.lobstr.stellar.tsmapper.presentation.util

import android.content.*
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.*
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.claimable_balance.ClaimClaimableBalanceOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.claimable_balance.CreateClaimableBalanceOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.clawback.ClawbackClaimableBalanceOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.clawback.ClawbackOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.liquidity_pool.LiquidityPoolDepositOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.liquidity_pool.LiquidityPoolWithdrawOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.offer.CancelSellOfferOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.offer.CreatePassiveSellOfferOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.offer.ManageBuyOfferOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.offer.SellOfferOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring.*
import java.util.*


object TsUtil {
    /**
     * Used for receive operation type.
     * @param operation Operation.
     * @param transactionType Target Transaction Type for determining Sep 10 Challenge.
     */
    fun getTransactionOperationName(operation: Operation): Int {
        return when (operation) {
            is PaymentOperation -> R.string.text_operation_name_payment
            is CreateAccountOperation -> R.string.text_operation_name_create_account
            is PathPaymentStrictSendOperation -> R.string.text_operation_name_path_payment_strict_send
            is PathPaymentStrictReceiveOperation -> R.string.text_operation_name_path_payment_strict_receive
            is SellOfferOperation -> R.string.text_operation_name_sell_offer
            is CancelSellOfferOperation -> R.string.text_operation_name_cancel_offer
            is ManageBuyOfferOperation -> R.string.text_operation_name_manage_buy_offer
            is CreatePassiveSellOfferOperation -> R.string.text_operation_name_create_passive_sell_offer
            is SetOptionsOperation -> R.string.text_operation_name_set_options
            is ChangeTrustOperation -> R.string.text_operation_name_change_trust
            is AllowTrustOperation -> R.string.text_operation_name_allow_trust
            is SetTrustlineFlagsOperation -> R.string.text_operation_name_set_trustline_flags
            is AccountMergeOperation -> R.string.text_operation_name_account_merge
            is InflationOperation -> R.string.text_operation_name_inflation
            is ManageDataOperation -> R.string.text_operation_name_manage_data
            is BumpSequenceOperation -> R.string.text_operation_name_bump_sequence
            is BeginSponsoringFutureReservesOperation -> R.string.text_operation_name_begin_sponsoring_future_reserves
            is EndSponsoringFutureReservesOperation -> R.string.text_operation_name_end_sponsoring_future_reserves
            is RevokeAccountSponsorshipOperation -> R.string.text_operation_name_revoke_account_sponsorship
            is RevokeClaimableBalanceSponsorshipOperation -> R.string.text_operation_name_revoke_claimable_balance_sponsorship
            is RevokeDataSponsorshipOperation -> R.string.text_operation_name_revoke_data_sponsorship
            is RevokeOfferSponsorshipOperation -> R.string.text_operation_name_revoke_offer_sponsorship
            is RevokeSignerSponsorshipOperation -> R.string.text_operation_name_revoke_signer_sponsorship
            is RevokeTrustlineSponsorshipOperation -> R.string.text_operation_name_revoke_trustline_sponsorship
            is CreateClaimableBalanceOperation -> R.string.text_operation_name_create_claimable_balance
            is ClaimClaimableBalanceOperation -> R.string.text_operation_name_claim_claimable_balance
            is ClawbackClaimableBalanceOperation -> R.string.text_operation_name_clawback_claimable_balance
            is ClawbackOperation -> R.string.text_operation_name_clawback
            is LiquidityPoolDepositOperation -> R.string.text_operation_name_liquidity_pool_deposit
            is LiquidityPoolWithdrawOperation -> R.string.text_operation_name_liquidity_pool_withdraw
            else -> -1
        }
    }

    fun isPublicKey(value: String?): Boolean {
        if (value.isNullOrEmpty() || value.length != 56) {
            return false
        }

        if (value[0] != 'G') {
            return false
        }

        for (element in value) {
            val letterCode = element.code
            if (!(letterCode in 65..90 || letterCode in 48..57)) {
                return false
            }
        }
        return true
    }

    fun ellipsizeStrInMiddle(str: String?, count: Int): String? {
        return if (str.isNullOrEmpty() || count >= (str.length / 2 - 1)) {
            str
        } else str.substring(0, count) + "…" + str.substring(str.length - count)
    }
}