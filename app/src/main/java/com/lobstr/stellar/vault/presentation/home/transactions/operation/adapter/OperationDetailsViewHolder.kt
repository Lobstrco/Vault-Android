package com.lobstr.stellar.vault.presentation.home.transactions.operation.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.R
import kotlinx.android.synthetic.main.adapter_item_operation_details.view.*


class OperationDetailsViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(name: String, value: String?) {
        val context = itemView.context
        itemView.tvOperationFieldName.text = capitalize(name)
        if (value == null) {
            itemView.tvOperationFieldValue.text = context.getString(R.string.text_details_none)
        } else {
            itemView.tvOperationFieldValue.text = value
        }
    }

    private fun capitalize(str: String): String {
        return str.substring(0, 1).toUpperCase() + str.subSequence(1, str.length)
    }
}