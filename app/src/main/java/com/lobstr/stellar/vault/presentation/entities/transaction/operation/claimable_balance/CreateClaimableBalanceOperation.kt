package com.lobstr.stellar.vault.presentation.entities.transaction.operation.claimable_balance

import android.os.Parcelable
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.entities.transaction.Claimant
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateClaimableBalanceOperation(
    override val sourceAccount: String?,
    val amount: String,
    val asset: Asset,
    val claimants: List<Claimant>
) : Operation(sourceAccount), Parcelable {

    override fun getFields(): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(AppUtil.getString(R.string.op_field_amount), amount))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_asset), asset.assetCode))

        if (!claimants.isNullOrEmpty()) {
            if (claimants.size == 1) {
                fields.add(
                    OperationField(
                        AppUtil.getString(R.string.op_field_claimant),
                        claimants.first().destination
                    )
                )

            } else {
                fields.add(OperationField(AppUtil.getString(R.string.op_field_claimants),
                    claimants.joinToString(separator = " ") {
                        if (AppUtil.isPublicKey(it.destination)) AppUtil.ellipsizeStrInMiddle(
                            it.destination,
                            PK_TRUNCATE_COUNT
                        ).toString().plus("\n") else it.destination.plus("\n")
                    }
                ))
            }
        }

        return fields
    }
}