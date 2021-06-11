package com.lobstr.stellar.vault.presentation.home.transactions.details.adapter

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.databinding.AdapterItemOperationBinding


class TransactionOperationViewHolder(private val binding: AdapterItemOperationBinding, private val itemClickListener: (position: Int) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(@StringRes title: Int, itemsCount: Int) {
        val context: Context = itemView.context
        binding.divider.visibility = calculateDividerVisibility(itemsCount)
        binding.tvOperationTitle.text = capitalize(context.getString(title))
        itemView.setOnClickListener {
            val position = this@TransactionOperationViewHolder.bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            itemClickListener(position)
        }
    }

    private fun capitalize(str: String): String {
        return str.substring(0, 1).uppercase() + str.subSequence(1, str.length)
    }

    /**
     * Don't show divider for last ore one item.
     * @param itemsCount Count of items.
     * @return Visibility.
     */
    private fun calculateDividerVisibility(itemsCount: Int): Int {
        return if (itemsCount == 1 || bindingAdapterPosition == itemsCount - 1) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }
}