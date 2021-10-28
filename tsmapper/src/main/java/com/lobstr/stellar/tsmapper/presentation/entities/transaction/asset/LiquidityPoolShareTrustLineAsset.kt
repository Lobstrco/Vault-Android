package com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LiquidityPoolShareTrustLineAsset(val liquidityPoolID: String) : Asset("", "pool_share", null), Parcelable