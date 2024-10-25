package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset.ChangeTrustAsset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset.TrustLineAsset
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
                is Asset.CanonicalAsset -> {
                    fields.add(
                        OperationField(
                            context.getString(R.string.op_field_asset),
                            asset.assetCode,
                            asset
                        )
                    )
                    asset.assetIssuer?.let {
                        fields.add(
                            OperationField(
                                context.getString(R.string.op_field_asset_issuer),
                                it,
                                it
                            )
                        )
                    }
                }

                is ChangeTrustAsset -> {
                    asset.liquidityPool?.let {
                        it.liquidityPoolID?.let { id ->
                            fields.add(
                                OperationField(
                                    context.getString(R.string.op_field_liquidity_pool_id),
                                    id
                                )
                            )
                        }
                        it.assetA.apply {
                            fields.add(
                                OperationField(
                                    context.getString(R.string.op_field_asset_a),
                                    assetCode,
                                    this
                                )
                            )
                            assetIssuer?.let { issuer ->
                                fields.add(
                                    OperationField(
                                        context.getString(R.string.op_field_asset_a_issuer),
                                        issuer,
                                        issuer
                                    )
                                )
                            }
                        }
                        it.assetB.apply {
                            fields.add(
                                OperationField(
                                    context.getString(R.string.op_field_asset_b),
                                    assetCode,
                                    this
                                )
                            )
                            assetIssuer?.let { issuer ->
                                fields.add(
                                    OperationField(
                                        context.getString(R.string.op_field_asset_b_issuer),
                                        issuer,
                                        issuer
                                    )
                                )
                            }
                        }

                        fields.add(
                            OperationField(
                                context.getString(R.string.op_field_fee),
                                amountFormatter(it.fee.toString())
                            )
                        )
                    }
                    asset.asset?.let {
                        fields.add(
                            OperationField(
                                context.getString(R.string.op_field_asset),
                                it.assetCode,
                                it
                            )
                        )
                        it.assetIssuer?.let { issuer ->
                            fields.add(
                                OperationField(
                                    context.getString(R.string.op_field_asset_issuer),
                                    issuer,
                                    issuer
                                )
                            )
                        }
                    }
                }

                is TrustLineAsset -> {
                    asset.liquidityPoolId?.let {
                        fields.add(
                            OperationField(
                                context.getString(R.string.op_field_liquidity_pool_id),
                                it
                            )
                        )
                    }
                    asset.asset?.let {
                        fields.add(
                            OperationField(
                                context.getString(R.string.op_field_asset),
                                it.assetCode,
                                it
                            )
                        )
                        it.assetIssuer?.let { issuer ->
                            fields.add(
                                OperationField(
                                    context.getString(R.string.op_field_asset_issuer),
                                    issuer,
                                    issuer
                                )
                            )
                        }
                    }
                }
            }

            return fields
        }
    }
}