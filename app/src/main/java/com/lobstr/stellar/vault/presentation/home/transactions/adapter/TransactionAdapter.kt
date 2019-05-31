package com.lobstr.stellar.vault.presentation.home.transactions.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem

class TransactionAdapter(private val listener: OnTransactionItemClicked) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var transactionItems: MutableList<TransactionItem> = mutableListOf()

    fun setTransactionList(transactionItems: MutableList<TransactionItem>) {
        this.transactionItems = transactionItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_item_transaction, viewGroup, false)
        return TransactionViewHolder(view, listener)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as TransactionViewHolder).bind(transactionItems[position])
    }

    override fun getItemCount(): Int {
        return transactionItems.size
    }
}
