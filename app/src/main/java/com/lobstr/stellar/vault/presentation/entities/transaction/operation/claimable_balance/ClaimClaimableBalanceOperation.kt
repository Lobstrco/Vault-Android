package com.lobstr.stellar.vault.presentation.entities.transaction.operation.claimable_balance

import android.os.Parcelable
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.vault.presentation.util.AppUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClaimClaimableBalanceOperation(
    override val sourceAccount: String?,
    val balanceId: String
) : Operation(sourceAccount), Parcelable {

    override fun getFields(): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(AppUtil.getString(R.string.op_field_balance_id), balanceId))

        return fields
    }
}