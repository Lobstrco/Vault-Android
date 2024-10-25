package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.claimable_balance

import android.content.Context
import android.os.Parcelable
import android.text.format.DateFormat
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.Claimant
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.ReadableTimeInterval.Type.AFTER
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.ReadableTimeInterval.Type.BEFORE
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.ReadableTimeInterval.Type.BETWEEN
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.ReadableTimeInterval.Type.NON_CLAIMABLE
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.ReadableTimeInterval.Type.NOT_SET
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.tsmapper.presentation.util.TsUtil
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class CreateClaimableBalanceOperation(
    override val sourceAccount: String?,
    val amount: String,
    val asset: Asset.CanonicalAsset,
    val claimants: List<Claimant>
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(context.getString(R.string.op_field_amount), amountFormatter(amount)))
        fields.add(OperationField(context.getString(R.string.op_field_asset), asset.assetCode, asset))

        claimants.forEachIndexed { index, claimant ->
            fields.apply {
                add(
                    OperationField(
                        "${context.getString(R.string.op_field_claimant)} ${if (claimants.size != 1) index + 1 else ""}".trim(),
                        claimant.destination,
                        claimant.destination,
                    )
                )
                claimant.getReadableTimeInterval()?.let {
                    add(
                        OperationField(
                            when (it.type) {
                                NOT_SET, NON_CLAIMABLE -> context.getString(R.string.op_field_claim_conditions)
                                BEFORE -> context.getString(R.string.op_field_claim_before)
                                AFTER -> context.getString(R.string.op_field_claim_after)
                                BETWEEN -> context.getString(R.string.op_field_claim_between)
                                else -> context.getString(R.string.op_field_claim_conditions)
                            },
                            when (it.type) {
                                NOT_SET -> context.getString(R.string.op_field_claim_not_set)
                                NON_CLAIMABLE -> context.getString(R.string.op_field_claim_non_claimable)
                                else -> {
                                    if (it.timeIntervals.isNotEmpty()) {
                                        val is24HourFormat = DateFormat.is24HourFormat(context)
                                        it.timeIntervals.joinToString(separator = "\n") { value ->
                                            value?.let { TsUtil.formatDate(Date().apply { time = it.toLong() } , "dd MMM yyyy, ${if (is24HourFormat) "HH:mm" else "hh:mm a"}") ?: "" } ?: ""
                                        }
                                    } else {
                                        context.getString(R.string.op_field_claim_not_set)
                                    }
                                }
                            },
                            it
                        )
                    )
                }
            }
        }

        return fields
    }
}