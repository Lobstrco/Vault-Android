package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.LiquidityPoolShareChangeTrustAsset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.LiquidityPoolShareTrustLineAsset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.PoolShareAsset
import com.lobstr.stellar.tsmapper.presentation.util.TsUtil.getAmountRepresentationFromStr
import kotlinx.parcelize.Parcelize

@Parcelize
open class Operation(open val sourceAccount: String?) : Parcelable {

    /**
     * @param amountFormatter Formatter for the Amount data representation.
     */
    open fun getFields(context: Context, amountFormatter: (value: String) -> String = ::getAmountRepresentationFromStr): MutableList<OperationField> = mutableListOf()

    fun applyOperationSourceAccountTo(context: Context, fields: MutableList<OperationField>): MutableList<OperationField> {
        if(!sourceAccount.isNullOrEmpty()) fields.add(OperationField(context.getString(R.string.op_field_source_account), sourceAccount, sourceAccount))
        return fields
    }

    companion object {
        fun mapAssetFields(
            context: Context,
            fields: MutableList<OperationField>,
            asset: Asset,
            amountFormatter: (value: String) -> String = ::getAmountRepresentationFromStr
        ): MutableList<OperationField> {
            when (asset) {
                is LiquidityPoolShareChangeTrustAsset -> {
                    if (asset.liquidityPoolID != null) fields.add(
                        OperationField(
                            context.getString(R.string.op_field_liquidity_pool_id),
                            asset.liquidityPoolID
                        )
                    )
                    fields.add(
                        OperationField(
                            context.getString(R.string.op_field_asset_a),
                            asset.assetA.assetCode,
                            asset
                        )
                    )
                    if (asset.assetA.assetIssuer != null) fields.add(
                        OperationField(
                            context.getString(
                                R.string.op_field_asset_a_issuer
                            ), asset.assetA.assetIssuer, asset.assetA.assetIssuer
                        )
                    )
                    fields.add(
                        OperationField(
                            context.getString(R.string.op_field_asset_b),
                            asset.assetB.assetCode,
                            asset
                        )
                    )
                    if (asset.assetB.assetIssuer != null) fields.add(
                        OperationField(
                            context.getString(
                                R.string.op_field_asset_b_issuer
                            ), asset.assetB.assetIssuer, asset.assetB.assetIssuer
                        )
                    )
                    fields.add(
                        OperationField(
                            context.getString(R.string.op_field_fee),
                            amountFormatter(asset.fee.toString())
                        )
                    )
                }

                is LiquidityPoolShareTrustLineAsset -> {
                    fields.add(
                        OperationField(
                            context.getString(R.string.op_field_liquidity_pool_id),
                            asset.liquidityPoolID
                        )
                    )
                }

                is PoolShareAsset -> {
                    fields.add(
                        OperationField(
                            context.getString(R.string.op_field_liquidity_pool_id),
                            asset.poolID
                        )
                    )
                }

                else -> {
                    fields.add(
                        OperationField(
                            context.getString(R.string.op_field_asset),
                            asset.assetCode,
                            asset
                        )
                    )
                    if (asset.assetIssuer != null) fields.add(
                        OperationField(
                            context.getString(R.string.op_field_asset_issuer),
                            asset.assetIssuer,
                            asset.assetIssuer
                        )
                    )
                }
            }

            return fields
        }
    }
}