package com.lobstr.stellar.vault.presentation.home.transactions.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem

class TransactionAdapter(private val listener: OnTransactionItemClicked) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val CONTENT_VIEW_TYPE = 0
        private const val PROGRESS_VIEW_TYPE = 1
    }

    private var transactionItems: MutableList<TransactionItem> = mutableListOf()
    private var needShowProgress: Boolean = false

    fun setTransactionList(
        transactionItems: List<TransactionItem>,
        needShowProgress: Boolean
    ) {
        this.transactionItems.clear()
        this.transactionItems.addAll(transactionItems)
        this.needShowProgress = needShowProgress
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CONTENT_VIEW_TYPE -> {
                TransactionViewHolder(
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.adapter_item_transaction, viewGroup, false),
                    listener
                )
            }
            else -> {
                ProgressViewHolder(
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.layout_list_preloader, viewGroup, false)
                )
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as? TransactionViewHolder)?.bind(transactionItems[position])
    }

    override fun getItemCount(): Int {
        return calculateItemCount()
    }

    private fun calculateItemCount(): Int {
        return if (needShowProgress) transactionItems.size + 1 else transactionItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= transactionItems.size) PROGRESS_VIEW_TYPE else CONTENT_VIEW_TYPE
    }
}
