package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import kotlinx.parcelize.Parcelize

@Parcelize
data class EndSponsoringFutureReservesOperation(
    override val sourceAccount: String?
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context): MutableList<OperationField> {
        return super.getFields(context)
    }
}