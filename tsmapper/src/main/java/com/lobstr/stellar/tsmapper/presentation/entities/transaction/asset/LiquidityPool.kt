package com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LiquidityPool(
    val fee: Int,
    val assetA: Asset.CanonicalAsset,
    val assetB: Asset.CanonicalAsset,
    val liquidityPoolID: String?
): Parcelable