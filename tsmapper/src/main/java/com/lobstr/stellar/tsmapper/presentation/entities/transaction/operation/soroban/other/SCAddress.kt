package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SCAddress : Parcelable {
    data class Account(val accountId: String) : SCAddress()

    data class Contract(val contractId: String) : SCAddress()

    data class MuxedAccount(val muxedAccount: String) : SCAddress()

    data class ClaimableBalance(val claimableBalanceId: String) : SCAddress()

    data class LiquidityPool(val liquidityPoolId: String) : SCAddress()
}
