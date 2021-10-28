package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import kotlinx.parcelize.Parcelize

@Parcelize
data class RevokeOfferSponsorshipOperation(
    override val sourceAccount: String?,
    val seller: String,
    val offerId: Long
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(context.getString(R.string.op_field_seller), seller, seller))
        fields.add(
            OperationField(
                context.getString(R.string.op_field_offer_id),
                offerId.toString()
            )
        )

        return fields
    }
}
