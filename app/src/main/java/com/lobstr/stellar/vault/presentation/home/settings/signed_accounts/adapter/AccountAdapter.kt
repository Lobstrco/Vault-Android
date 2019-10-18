package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.account.Account

class AccountAdapter(private val dataType: Int, private val listener: OnAccountItemListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ACCOUNT_EXTENDED = 0
        const val ACCOUNT = 1
        const val ACCOUNT_WITH_STATUS = 2
    }

    private var accounts: MutableList<Account> = mutableListOf()

    fun setAccountList(accounts: List<Account>) {
        this.accounts.clear()
        this.accounts.addAll(accounts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ACCOUNT_EXTENDED -> AccountExtendedViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.adapter_item_account_extended, viewGroup, false),
                listener
            )
            ACCOUNT -> AccountViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.adapter_item_account, viewGroup, false), listener
            )
            ACCOUNT_WITH_STATUS -> AccountWithStatusViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.adapter_item_account_with_status, viewGroup, false),
                listener
            )
            else -> AccountExtendedViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.adapter_item_account_extended, viewGroup, false),
                listener
            )
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is AccountExtendedViewHolder -> viewHolder.bind(accounts[position], itemCount)
            is AccountViewHolder -> viewHolder.bind(accounts[position], itemCount)
            is AccountWithStatusViewHolder -> viewHolder.bind(accounts[position], itemCount)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataType
    }

    override fun getItemCount(): Int {
        return accounts.size
    }
}
