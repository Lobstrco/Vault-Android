package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.databinding.AdapterItemAccountBinding
import com.lobstr.stellar.vault.databinding.AdapterItemAccountExtendedBinding
import com.lobstr.stellar.vault.databinding.AdapterItemAccountWithStatusBinding
import com.lobstr.stellar.vault.presentation.entities.account.Account

class AccountAdapter(
    private val dataType: Int,
    private val itemClickListener: (account: Account) -> Unit,
    private val itemLongClickListener: (account: Account) -> Unit
) :
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
                AdapterItemAccountExtendedBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false),
                itemClickListener, itemLongClickListener
            )
            ACCOUNT -> AccountViewHolder(
                AdapterItemAccountBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false),
                itemClickListener, itemLongClickListener
            )
            ACCOUNT_WITH_STATUS -> AccountWithStatusViewHolder(
                AdapterItemAccountWithStatusBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false),
                itemClickListener, itemLongClickListener
            )
            else -> AccountExtendedViewHolder(
                AdapterItemAccountExtendedBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false),
                itemClickListener, itemLongClickListener
            )
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is AccountExtendedViewHolder -> viewHolder.bind(accounts[position])
            is AccountViewHolder -> viewHolder.bind(accounts[position])
            is AccountWithStatusViewHolder -> viewHolder.bind(accounts[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataType
    }

    override fun getItemCount(): Int {
        return accounts.size
    }
}
