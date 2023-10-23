package com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LiquidityPoolShareChangeTrustAsset(
    val liquidityPoolID: String?,
    val fee: Int,
    val assetA: Asset,
    val assetB: Asset,
) : PoolShareAsset(liquidityPoolID), Parcelable