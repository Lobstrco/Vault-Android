package com.lobstr.stellar.vault.presentation.home.transactions.details.adapter

import android.content.Context
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.databinding.AdapterItemOperationBinding
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener


class TransactionOperationViewHolder(private val binding: AdapterItemOperationBinding, private val itemClickListener: (position: Int) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(@StringRes title: Int) {
        val context: Context = itemView.context
        binding.tvOperationTitle.text = capitalize(context.getString(title))
        itemView.setSafeOnClickListener {
            val position = this@TransactionOperationViewHolder.bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setSafeOnClickListener
            }

            itemClickListener(position)
        }
    }

    private fun capitalize(str: String): String {
        return str.substring(0, 1).uppercase() + str.subSequence(1, str.length)
    }
}