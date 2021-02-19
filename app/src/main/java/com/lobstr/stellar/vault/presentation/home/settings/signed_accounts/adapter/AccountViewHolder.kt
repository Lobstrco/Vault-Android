package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.AdapterItemAccountBinding
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT

class AccountViewHolder(
    private val binding: AdapterItemAccountBinding,
    private val itemClickListener: (account: Account) -> Unit,
    private val itemLongClickListener: (account: Account) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(account: Account, itemsCount: Int) {
        binding.divider.visibility = calculateDividerVisibility(itemsCount)

        // Set user icon.
        Glide.with(itemView.context)
            .load(
                Constant.Social.USER_ICON_LINK
                    .plus(account.address)
                    .plus(".png")
            )
            .placeholder(R.drawable.ic_person)
            .into(binding.ivIdentity)

        binding.tvAccount.visibility =
            if (account.federation.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }

        binding.tvAccount.text = when {
            !account.federation.isNullOrEmpty() -> AppUtil.ellipsizeStrInMiddle(
                account.address,
                PK_TRUNCATE_COUNT
            )
            else -> null
        }

        binding.tvAccountFederation.ellipsize =
            if (account.federation.isNullOrEmpty()) {
                TextUtils.TruncateAt.MIDDLE
            } else {
                TextUtils.TruncateAt.END
            }

        binding.tvAccountFederation.text =
            if (account.federation.isNullOrEmpty()) {
                AppUtil.ellipsizeStrInMiddle(account.address, PK_TRUNCATE_COUNT)
            } else {
                account.federation
            }

        itemView.setOnClickListener {
            val position = this@AccountViewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            itemClickListener(account)
        }

        itemView.setOnLongClickListener {
            val position = this@AccountViewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnLongClickListener false
            }

            itemLongClickListener(account)

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