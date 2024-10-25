package com.lobstr.stellar.tsmapper.data.asset

import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset.CanonicalAsset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.AssetType
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.LiquidityPool
import org.stellar.sdk.AssetTypeCreditAlphaNum
import org.stellar.sdk.AssetTypeNative
import org.stellar.sdk.ChangeTrustAsset
import org.stellar.sdk.StrKey
import org.stellar.sdk.TrustLineAsset
import org.stellar.sdk.Util
import java.util.Locale

class AssetMapper {

    fun mapChangeTrustAsset(asset: ChangeTrustAsset): Asset.ChangeTrustAsset {
        return Asset.ChangeTrustAsset(
            mapAssetType(asset.assetType),
            asset.asset?.let { mapAsset(it) },
            asset.liquidityPool?.let { mapLiquidityPool(it) }
        )
    }

    private fun mapLiquidityPool(liquidityPool: org.stellar.sdk.LiquidityPool): LiquidityPool {
        return LiquidityPool(
            liquidityPool.fee,
            mapAsset(liquidityPool.assetA),
            mapAsset(liquidityPool.assetB),
            try {
                liquidityPool.liquidityPoolId
            } catch (exc: Exception) {
                null
            }
        )
    }

    fun mapTrustLineAsset(asset: TrustLineAsset): Asset.TrustLineAsset {
        return Asset.TrustLineAsset(
            mapAssetType(asset.assetType),
            asset.asset?.let { mapAsset(it) },
            asset.liquidityPoolId
        )
    }

    fun mapTrustLineAsset(asset: org.stellar.sdk.xdr.TrustLineAsset): Asset.TrustLineAsset {
        return when (asset.discriminant) {
            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_NATIVE -> Asset.TrustLineAsset(
                AssetType.ASSET_TYPE_NATIVE,
                CanonicalAsset(AssetType.ASSET_TYPE_NATIVE, "XLM"),
                null
            )

            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_CREDIT_ALPHANUM4 -> Asset.TrustLineAsset(
                AssetType.ASSET_TYPE_CREDIT_ALPHANUM4,
                CanonicalAsset(
                    AssetType.ASSET_TYPE_CREDIT_ALPHANUM4,
                    String(asset.alphaNum4.assetCode.assetCode4).trim(),
                    StrKey.encodeEd25519PublicKey(asset.alphaNum4.issuer.accountID.ed25519.uint256)
                ),
                null
            )

            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_CREDIT_ALPHANUM12 -> Asset.TrustLineAsset(
                AssetType.ASSET_TYPE_CREDIT_ALPHANUM12,
                CanonicalAsset(
                    AssetType.ASSET_TYPE_CREDIT_ALPHANUM12,
                    String(asset.alphaNum12.assetCode.assetCode12).trim(),
                    StrKey.encodeEd25519PublicKey(asset.alphaNum12.issuer.accountID.ed25519.uint256)
                ),
                null
            )

            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_POOL_SHARE -> Asset.TrustLineAsset(
                AssetType.ASSET_TYPE_POOL_SHARE,
                null,
                Util.bytesToHex(asset.liquidityPoolID.poolID.hash).lowercase(
                    Locale.getDefault()
                )
            )
        }
    }

    fun mapAsset(asset: org.stellar.sdk.Asset?): CanonicalAsset {
        return when (asset) {
            is AssetTypeCreditAlphaNum -> CanonicalAsset(
                mapAssetType(asset.type),
                asset.code,
                asset.issuer
            )

            is AssetTypeNative -> CanonicalAsset(
                AssetType.ASSET_TYPE_NATIVE,
                "XLM",
                null
            )

            else -> CanonicalAsset(
                AssetType.ASSET_TYPE_NATIVE,
                "XLM",
                null
            )
        }
    }

    private fun mapAssetType(type: org.stellar.sdk.xdr.AssetType): AssetType = when (type) {
        org.stellar.sdk.xdr.AssetType.ASSET_TYPE_NATIVE -> AssetType.ASSET_TYPE_NATIVE
        org.stellar.sdk.xdr.AssetType.ASSET_TYPE_CREDIT_ALPHANUM4 -> AssetType.ASSET_TYPE_CREDIT_ALPHANUM4
        org.stellar.sdk.xdr.AssetType.ASSET_TYPE_CREDIT_ALPHANUM12 -> AssetType.ASSET_TYPE_CREDIT_ALPHANUM12
        org.stellar.sdk.xdr.AssetType.ASSET_TYPE_POOL_SHARE -> AssetType.ASSET_TYPE_POOL_SHARE
    }

    fun mapAsset(asset: org.stellar.sdk.xdr.Asset): CanonicalAsset {
        return when (asset.discriminant) {
            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_NATIVE -> CanonicalAsset(
                AssetType.ASSET_TYPE_NATIVE,
                "XLM",
                null
            )

            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_CREDIT_ALPHANUM4 -> CanonicalAsset(
                AssetType.ASSET_TYPE_CREDIT_ALPHANUM4,
                String(asset.alphaNum4.assetCode.assetCode4).trim(),
                StrKey.encodeEd25519PublicKey(asset.alphaNum4.issuer.accountID.ed25519.uint256)
            )

            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_CREDIT_ALPHANUM12 -> CanonicalAsset(
                AssetType.ASSET_TYPE_CREDIT_ALPHANUM12,
                String(asset.alphaNum12.assetCode.assetCode12).trim(),
                StrKey.encodeEd25519PublicKey(asset.alphaNum12.issuer.accountID.ed25519.uint256)
            )
            // add other asset types here in the future
//            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_POOL_SHARE -> {}
            else -> CanonicalAsset(
                AssetType.ASSET_TYPE_NATIVE,
                "XLM",
                null
            )
        }
    }
}