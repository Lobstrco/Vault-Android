package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.util.AppUtil
import kotlinx.parcelize.Parcelize

@Parcelize
open class Operation(open val sourceAccount: String?) : Parcelable {

    open fun getFields(): MutableList<OperationField> = mutableListOf()

    fun applyOperationSourceAccountTo(fields: MutableList<OperationField>): MutableList<OperationField> {
        if(!sourceAccount.isNullOrEmpty()) fields.add(OperationField(AppUtil.getString(R.string.op_field_source_account), sourceAccount))
        return fields
    }
}