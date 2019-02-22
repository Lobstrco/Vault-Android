package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.presentation.entities.account.Account
import kotlinx.android.synthetic.main.adapter_item_signed_account.view.*

class SignedAccountViewHolder(itemView: View, private val listener: OnSignedAcoountItemClicked) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(account: Account) {
        itemView.tvSignedAccount.text = account.address
        itemView.setOnClickListener {
            val position = this@SignedAccountViewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            listener.onSignedAccountItemClick(account)
        }
    }
}