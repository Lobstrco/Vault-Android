package com.lobstr.stellar.vault.presentation.signed_accounts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.account.Account

class SignedAccountAdapter(private val listener: OnSignedAcoountItemClicked) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var accounts: MutableList<Account> = mutableListOf()

    fun setAccountList(accounts: List<Account>) {
        this.accounts.clear()
        this.accounts.addAll(accounts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_item_signed_account, viewGroup, false)
        return SignedAccountViewHolder(view, listener)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as SignedAccountViewHolder).bind(accounts[position])
    }

    override fun getItemCount(): Int {
        return accounts.size
    }
}
