package com.lobstr.stellar.vault.presentation.home.dashboard.account.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.AdapterItemAccountsDialogBinding
import com.lobstr.stellar.vault.presentation.entities.account.AccountDialogItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener

class AccountsDialogViewHolder(
    private val binding: AdapterItemAccountsDialogBinding,
    private val itemClickListener: (account: AccountDialogItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: AccountDialogItem) {
        binding.apply {
            tvAccountsDialogName.text = item.name
            tvAccountsDialogKey.text = AppUtil.ellipsizeStrInMiddle(
                item.address,
                Constant.Util.PK_TRUNCATE_COUNT
            )

            cbAccountsDialog.isChecked = item.isChecked

            // Set user icon.
            Glide.with(itemView.context)
                .load(AppUtil.createUserIconLink(item.address))
                .placeholder(R.drawable.ic_person)
                .into(ivAccountsDialogIcon)
        }

        itemView.setSafeOnClickListener {
            val position = this@AccountsDialogViewHolder.bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setSafeOnClickListener
            }

            itemClickListener(item)
        }
    }
}