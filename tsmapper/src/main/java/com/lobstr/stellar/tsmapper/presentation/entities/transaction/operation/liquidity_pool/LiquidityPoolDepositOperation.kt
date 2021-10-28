package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.liquidity_pool

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import kotlinx.parcelize.Parcelize

@Parcelize
data class LiquidityPoolDepositOperation(
    override val sourceAccount: String?,
    val liquidityPoolID: String,
    val maxAmountA: String,
    val maxAmountB: String,
    val minPrice: String?,
    val maxPrice: String?
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(context.getString(R.string.op_field_liquidity_pool_id), liquidityPoolID))
        fields.add(OperationField(context.getString(R.string.op_field_max_amount_a), maxAmountA))
        fields.add(OperationField(context.getString(R.string.op_field_max_amount_b), maxAmountB))
        if(!minPrice.isNullOrEmpty()) fields.add(OperationField(context.getString(R.string.op_field_min_price), minPrice))
        if(!maxPrice.isNullOrEmpty()) fields.add(OperationField(context.getString(R.string.op_field_max_price), maxPrice))

        return fields
    }
}