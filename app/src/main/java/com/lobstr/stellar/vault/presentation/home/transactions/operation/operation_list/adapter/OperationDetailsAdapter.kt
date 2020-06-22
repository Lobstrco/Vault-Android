package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.OperationField

class OperationDetailsAdapter(private val fields: MutableList<OperationField>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_item_operation_details, viewGroup, false)
        return OperationDetailsViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as OperationDetailsViewHolder).bind(fields[position].key, fields[position].value)
    }

    override fun getItemCount(): Int {
        return fields.size
    }
}
