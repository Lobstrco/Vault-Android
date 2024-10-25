package com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class Asset(open val type: AssetType) : Parcelable {

    class CanonicalAsset(
        override val type: AssetType,
        val assetCode: String,
        val assetIssuer: String? = null
    ) : Asset(type)

    class ChangeTrustAsset(
        override val type: AssetType,
        val asset: CanonicalAsset?,
        val liquidityPool: LiquidityPool?
    ) : Asset(type)

    class TrustLineAsset(
        override val type: AssetType,
        val asset: CanonicalAsset?,
        val liquidityPoolId: String?
    ) : Asset(type)
}

enum class AssetType {
    ASSET_TYPE_NATIVE,
    ASSET_TYPE_CREDIT_ALPHANUM4,
    ASSET_TYPE_CREDIT_ALPHANUM12,
    ASSET_TYPE_POOL_SHARE
}
