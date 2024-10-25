package com.lobstr.stellar.tsmapper.data.asset

import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.LiquidityPoolShareChangeTrustAsset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.LiquidityPoolShareTrustLineAsset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.PoolShareAsset
import org.stellar.sdk.AssetTypeCreditAlphaNum
import org.stellar.sdk.AssetTypeNative
import org.stellar.sdk.AssetTypePoolShare
import org.stellar.sdk.ChangeTrustAsset
import org.stellar.sdk.LiquidityPoolConstantProductParameters
import org.stellar.sdk.LiquidityPoolID
import org.stellar.sdk.StrKey
import org.stellar.sdk.TrustLineAsset

class AssetMapper {
    fun mapChangeTrustAsset(asset: ChangeTrustAsset): Asset {
        return when(asset.type) {
            "pool_share" -> {
                val targetAsset = asset as org.stellar.sdk.LiquidityPoolShareChangeTrustAsset
                val liquidityPoolParameters = targetAsset.liquidityPoolParams as LiquidityPoolConstantProductParameters
                LiquidityPoolShareChangeTrustAsset(
                    try { targetAsset.liquidityPoolID.toString()} catch (exc: Exception) { null }, // Handle errors for Liquidity Pool ID.
                    liquidityPoolParameters.fee,
                    mapAsset(liquidityPoolParameters.assetA),
                    mapAsset(liquidityPoolParameters.assetB)
                )
            }
            else -> mapAsset((asset as ChangeTrustAsset.Wrapper).asset)
        }
    }

    fun mapTrustLineAsset(asset: TrustLineAsset): Asset {
        return when (asset.type) {
            "pool_share" -> {
                val targetAsset = asset as org.stellar.sdk.LiquidityPoolShareTrustLineAsset
                LiquidityPoolShareTrustLineAsset(
                    targetAsset.liquidityPoolID.toString()
                )
            }
            else -> mapAsset((asset as TrustLineAsset.Wrapper).asset)
        }
    }


    fun mapTrustLineAsset(asset: org.stellar.sdk.xdr.TrustLineAsset): Asset {
        return when (asset.discriminant) {
            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_NATIVE -> Asset("XLM", "native")
            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_CREDIT_ALPHANUM4 -> Asset(String(asset.alphaNum4.assetCode.assetCode4).trim(), "credit_alphanum4", StrKey.encodeEd25519PublicKey(asset.alphaNum4.issuer.accountID.ed25519.uint256))
            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_CREDIT_ALPHANUM12 -> Asset(String(asset.alphaNum12.assetCode.assetCode12).trim(), "credit_alphanum12", StrKey.encodeEd25519PublicKey(asset.alphaNum12.issuer.accountID.ed25519.uint256))
            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_POOL_SHARE -> PoolShareAsset(LiquidityPoolID.fromXdr(asset.liquidityPoolID).toString())
            else -> Asset("XLM", "native")
        }
    }

    fun mapAsset(asset: org.stellar.sdk.Asset?): Asset {
        return when (asset) {
            is AssetTypeCreditAlphaNum -> Asset(asset.code, asset.type, asset.issuer)
            is AssetTypeNative -> Asset("XLM", "native")
            is AssetTypePoolShare -> PoolShareAsset(asset.poolId)
            else -> Asset("XLM", "native")
        }
    }

    fun mapAsset(asset: org.stellar.sdk.xdr.Asset): Asset {
        return when (asset.discriminant) {
            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_NATIVE -> Asset("XLM", "native", null)
            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_CREDIT_ALPHANUM4 -> Asset(String(asset.alphaNum4.assetCode.assetCode4).trim(), "credit_alphanum4", StrKey.encodeEd25519PublicKey(asset.alphaNum4.issuer.accountID.ed25519.uint256))
            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_CREDIT_ALPHANUM12 -> Asset(String(asset.alphaNum12.assetCode.assetCode12).trim(), "credit_alphanum12", StrKey.encodeEd25519PublicKey(asset.alphaNum12.issuer.accountID.ed25519.uint256))
            org.stellar.sdk.xdr.AssetType.ASSET_TYPE_POOL_SHARE -> PoolShareAsset(null)
            else -> Asset("XLM", "native")
        }
    }
}