package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.Constant
import kotlinx.android.synthetic.main.adapter_item_account_with_status.view.*

class AccountWithStatusViewHolder(
    itemView: View,
    private val listener: OnAccountItemListener
) :
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

        itemView.tvStatus.text =
            if (account.signed == true) {
                itemView.context.getString(R.string.text_tv_status_signed)
            } else {
                itemView.context.getString(R.string.text_tv_status_pending)
            }

        itemView.tvStatus.setTextColor(
            if (account.signed == true) {
                ContextCompat.getColor(itemView.context, R.color.color_5cb87f)
            } else {
                ContextCompat.getColor(itemView.context, R.color.color_fb9e00)
            }
        )

        itemView.setOnLongClickListener {
            val position = this@AccountWithStatusViewHolder.adapterPosition
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