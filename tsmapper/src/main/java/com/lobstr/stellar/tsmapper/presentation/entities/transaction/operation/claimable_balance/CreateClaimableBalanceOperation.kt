package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.claimable_balance

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.Claimant
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateClaimableBalanceOperation(
    override val sourceAccount: String?,
    val amount: String,
    val asset: Asset,
    val claimants: List<Claimant>
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(context.getString(R.string.op_field_amount), amount))
        fields.add(OperationField(context.getString(R.string.op_field_asset), asset.assetCode, asset))

        claimants.forEachIndexed { index, claimant ->
            fields.add(
                OperationField(
                    "${context.getString(R.string.op_field_claimant)} ${if (claimants.size != 1) index + 1 else ""}".trim(),
                    claimant.destination,
                    claimant.destination,
                )
            )
        }

        return fields
    }
}