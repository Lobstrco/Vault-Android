package com.lobstr.stellar.vault.presentation.home.transactions.details.adapter

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_item_operation.view.*


class TransactionOperationViewHolder(itemView: View, private val listener: OnOperationClicked) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(@StringRes title: Int, itemsCount: Int) {
        val context: Context = itemView.context
        itemView.divider.visibility = calculateDividerVisibility(itemsCount)
        itemView.tvOperationTitle.text = capitalize(context.getString(title))
        itemView.setOnClickListener {
            val position = this@TransactionOperationViewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            listener.onOperationItemClick(position)
        }
    }

    private fun capitalize(str: String): String {
        return str.substring(0, 1).toUpperCase() + str.subSequence(1, str.length)
    }

    /**
     * Don't show divider for last ore one item
     * @param itemsCount count of items
     * @return visibility
     */
    private fun calculateDividerVisibility(itemsCount: Int): Int {
        return if (itemsCount == 1 || adapterPosition == itemsCount - 1) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }
}