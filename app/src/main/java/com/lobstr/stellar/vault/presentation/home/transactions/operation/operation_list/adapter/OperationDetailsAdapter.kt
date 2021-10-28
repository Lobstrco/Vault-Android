package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.vault.databinding.AdapterItemOperationDetailsBinding

class OperationDetailsAdapter(
    private val fields: MutableList<OperationField>,
    private val itemClickListener: (key: String, value: String?, tag: Any?) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return OperationDetailsViewHolder(
            AdapterItemOperationDetailsBinding.inflate(
                LayoutInflater.from(
                    viewGroup.context
                ), viewGroup, false
            ),
            itemClickListener
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as OperationDetailsViewHolder).bind(
            fields[position].key,
            fields[position].value,
            fields[position].tag
        )
    }

    override fun getItemCount(): Int {
        return fields.size
    }
}
