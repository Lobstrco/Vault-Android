package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.Constant
import kotlinx.android.synthetic.main.adapter_item_account_extended.view.*

class AccountViewHolder(itemView: View, private val listener: OnAccountItemListener) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(account: Account, itemsCount: Int) {

        itemView.divider.visibility = calculateDividerVisibility(itemsCount)

        // set user icon
        Glide.with(itemView.context)
            .load(
                Constant.Social.USER_ICON_LINK
                    .plus(account.address)
                    .plus(".png")
            )
            .placeholder(R.drawable.ic_person)
            .into(itemView.ivIdentity)

        itemView.tvAccount.text = account.address

        itemView.setOnLongClickListener {
            val position = this@AccountViewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnLongClickListener false
            }

            listener.onAccountItemLongClick(account)

            true
        }
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