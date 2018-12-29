package com.lobstr.stellar.vault.presentation.home.transactions.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.R

class TransactionOperationAdapter(private val listener: OnOperationClicked) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var operationItems: MutableList<Int> = mutableListOf()

    fun setOperationList(operations: MutableList<Int>) {
        operationItems = operations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_item_operation, viewGroup, false)
        return TransactionOperationViewHolder(view, listener)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as TransactionOperationViewHolder).bind(operationItems[position])
    }

    override fun getItemCount(): Int {
        return operationItems.size
    }
}
