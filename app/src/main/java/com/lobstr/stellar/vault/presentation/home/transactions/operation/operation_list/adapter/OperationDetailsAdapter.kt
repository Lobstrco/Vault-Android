package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.R

class OperationDetailsAdapter(private val mMap: Map<String, String?>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_item_operation_details, viewGroup, false)
        return OperationDetailsViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val key = mMap.keys.toList()[position]
        (viewHolder as OperationDetailsViewHolder).bind(key, mMap[key])
    }

    override fun getItemCount(): Int {
        return mMap.size
    }
}
