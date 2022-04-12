package com.lobstr.stellar.vault.presentation.home.dashboard.account.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.databinding.AdapterItemAccountsDialogBinding
import com.lobstr.stellar.vault.presentation.entities.account.AccountDialogItem

class AccountsDialogAdapter(
    private val operations: List<AccountDialogItem>,
    private val itemClickListener: (account: AccountDialogItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder =
        AccountsDialogViewHolder(
            AdapterItemAccountsDialogBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            ),
            itemClickListener
        )

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as AccountsDialogViewHolder).bind(operations[position])
    }

    override fun getItemCount(): Int = operations.size
}
