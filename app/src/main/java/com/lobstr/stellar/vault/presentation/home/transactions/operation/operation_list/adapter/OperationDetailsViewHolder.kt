package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import kotlinx.android.synthetic.main.adapter_item_operation_details.view.*


class OperationDetailsViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(name: String, value: String?) {
        itemView.tvOperationFieldName.text = String.format("%s:", capitalize(name))
        itemView.tvOperationFieldValue.text =
            if (AppUtil.isPublicKey(value)) {
                AppUtil.ellipsizeStrInMiddle(value, Constant.Util.PK_TRUNCATE_COUNT)
            } else {
                value
            }
    }

    private fun capitalize(str: String): String {
        return str.substring(0, 1).toUpperCase() + str.subSequence(1, str.length)
    }
}