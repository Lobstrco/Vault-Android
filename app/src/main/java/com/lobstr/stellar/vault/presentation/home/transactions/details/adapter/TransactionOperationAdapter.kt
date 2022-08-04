package com.lobstr.stellar.vault.presentation.home.transactions.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.databinding.AdapterItemOperationBinding

class TransactionOperationAdapter(
    private val operations: List<Int>,
    private val itemClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return TransactionOperationViewHolder(
            AdapterItemOperationBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false),
            itemClickListener
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as TransactionOperationViewHolder).bind(operations[position])
    }

    override fun getItemCount(): Int {
        return operations.size
    }
}
