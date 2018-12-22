package com.lobstr.stellar.vault.presentation.home.transactions.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import kotlinx.android.synthetic.main.adapter_item_transaction.view.*

class TransactionViewHolder(itemView: View, private val listener: OnTransactionItemClicked) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: TransactionItem) {
        val context: Context = itemView.context

        itemView.tvTransactionItemData.text = String.format("Data: %s", item.addedAt)
        itemView.tvTransactionItemXDR.text = String.format("XDR: %s", item.xdr)
        itemView.setOnClickListener {
            val position = this@TransactionViewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            listener.onTransactionItemClick(item)
        }
    }
}