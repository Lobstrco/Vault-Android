package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter

import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.AdapterItemAccountWithStatusBinding
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT

class AccountWithStatusViewHolder(
    private val binding: AdapterItemAccountWithStatusBinding,
    private val itemLongClickListener: (account: Account) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

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

        // Show section for address with federation and vault account marker.
        binding.tvAccountNameBottom.isVisible = !account.federation.isNullOrEmpty() || account.isVaultAccount == true || !account.name.isNullOrEmpty()

        binding.tvAccountNameBottom.text = when {
            !account.federation.isNullOrEmpty() || account.isVaultAccount == true || !account.name.isNullOrEmpty() -> AppUtil.ellipsizeStrInMiddle(
                account.address,
                PK_TRUNCATE_COUNT
            )
            else -> null
        }

        binding.tvAccountNameTop.ellipsize =
            if (account.federation.isNullOrEmpty() && account.name.isNullOrEmpty()) {
                TextUtils.TruncateAt.MIDDLE
            } else {
                TextUtils.TruncateAt.END
            }

        binding.tvAccountNameTop.text =
            if (account.federation.isNullOrEmpty() && account.name.isNullOrEmpty()) {
                if(account.isVaultAccount == true) {
                    itemView.context.getString(R.string.text_tv_vault_account_marker)
                } else {
                    AppUtil.ellipsizeStrInMiddle(account.address, PK_TRUNCATE_COUNT)
                }
            } else {
                if(!account.name.isNullOrEmpty()){
                    account.name
                } else {
                    account.federation
                }
            }

        binding.tvStatus.text =
            if (account.signed == true) {
                itemView.context.getString(R.string.text_tv_status_signed)
            } else {
                itemView.context.getString(R.string.text_tv_status_pending)
            }

        binding.tvStatus.setTextColor(
            if (account.signed == true) {
                ContextCompat.getColor(itemView.context, R.color.color_5cb87f)
            } else {
                ContextCompat.getColor(itemView.context, R.color.color_fb9e00)
            }
        )

        itemView.setOnLongClickListener {
            val position = this@AccountWithStatusViewHolder.bindingAdapterPosition
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
        return if (itemsCount == 1 || bindingAdapterPosition == itemsCount - 1) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }
}