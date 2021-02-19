package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InflationOperation(
    override val sourceAccount: String?
) : Operation(sourceAccount), Parcelable {

    override fun getFields(): MutableList<OperationField> {
        return super.getFields()
    }
}