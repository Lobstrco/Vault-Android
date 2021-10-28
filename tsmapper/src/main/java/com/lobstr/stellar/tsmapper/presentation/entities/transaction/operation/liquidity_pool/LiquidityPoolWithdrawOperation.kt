package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.liquidity_pool

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import kotlinx.parcelize.Parcelize

@Parcelize
class LiquidityPoolWithdrawOperation(
    override val sourceAccount: String?,
    val liquidityPoolID: String,
    val amount: String,
    val minAmountA: String,
    val minAmountB: String
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(context.getString(R.string.op_field_liquidity_pool_id), liquidityPoolID))
        fields.add(OperationField(context.getString(R.string.op_field_amount), amount))
        fields.add(OperationField(context.getString(R.string.op_field_min_amount_a), minAmountA))
        fields.add(OperationField(context.getString(R.string.op_field_min_amount_b), minAmountB))

        return fields
    }
}