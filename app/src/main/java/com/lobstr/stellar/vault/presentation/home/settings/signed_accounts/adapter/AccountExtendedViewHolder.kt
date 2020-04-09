package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT
import kotlinx.android.synthetic.main.adapter_item_account_extended.view.*

class AccountExtendedViewHolder(itemView: View, private val listener: OnAccountItemListener) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(account: Account, itemsCount: Int) {
        itemView.divider.visibility = calculateDividerVisibility(itemsCount)

        // Set user icon.
        Glide.with(itemView.context)
            .load(
                Constant.Social.USER_ICON_LINK
                    .plus(account.address)
                    .plus(".png")
            )
            .placeholder(R.drawable.ic_person)
            .into(itemView.ivIdentity)

        itemView.tvAccount.visibility =
            if (account.federation.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }

        itemView.tvAccount.text = when {
            !account.federation.isNullOrEmpty() -> AppUtil.ellipsizeStrInMiddle(
                account.address,
                PK_TRUNCATE_COUNT
            )
            else -> null
        }

        itemView.tvAccountFederation.ellipsize =
            if (account.federation.isNullOrEmpty()) {
                TextUtils.TruncateAt.MIDDLE
            } else {
                TextUtils.TruncateAt.END
            }

        itemView.tvAccountFederation.text =
            if (account.federation.isNullOrEmpty()) {
                AppUtil.ellipsizeStrInMiddle(account.address, PK_TRUNCATE_COUNT)
            } else {
                account.federation
            }

        itemView.setOnClickListener {
            val position = this@AccountExtendedViewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            listener.onAccountItemClick(account)
        }

        itemView.setOnLongClickListener {
            val position = this@AccountExtendedViewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnLongClickListener false
            }

            listener.onAccountItemLongClick(account)

            true
        }
    }

    /**
     * Don't show divider for last ore one item.
     * @param itemsCount Count of items.
     * @return Visibility.
     */
    private fun calculateDividerVisibility(itemsCount: Int): Int {
        return if (itemsCount == 1 || adapterPosition == itemsCount - 1) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }
}