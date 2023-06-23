package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter

import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.AdapterItemAccountWithStatusBinding
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener

class AccountWithStatusViewHolder(
    private val binding: AdapterItemAccountWithStatusBinding,
    private val itemClickListener: (account: Account) -> Unit,
    private val itemLongClickListener: (account: Account) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(account: Account) {
        binding.apply {
            // Set user icon.
            Glide.with(itemView.context)
                .load(AppUtil.createUserIconLink(account.address))
                .placeholder(R.drawable.ic_person)
                .into(ivIdentity)

            // Show section for address with federation and vault account marker.
            tvAccountNameBottom.isVisible = !account.federation.isNullOrEmpty() || account.isVaultAccount == true || !account.name.isNullOrEmpty()

            tvAccountNameBottom.text = when {
                !account.federation.isNullOrEmpty() || account.isVaultAccount == true || !account.name.isNullOrEmpty() -> AppUtil.ellipsizeStrInMiddle(
                    account.address,
                    PK_TRUNCATE_COUNT
                )
                else -> null
            }

            tvAccountNameTop.ellipsize =
                if (account.federation.isNullOrEmpty() && account.name.isNullOrEmpty()) {
                    TextUtils.TruncateAt.MIDDLE
                } else {
                    TextUtils.TruncateAt.END
                }

            tvAccountNameTop.text =
                if (account.federation.isNullOrEmpty() && account.name.isNullOrEmpty()) {
                    if(account.isVaultAccount == true) {
                        itemView.context.getString(R.string.transaction_details_signer_item_vault_marker_label)
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

            tvStatus.text =
                if (account.signed == true) {
                    itemView.context.getString(R.string.transaction_details_signer_item_status_signed_label)
                } else {
                    itemView.context.getString(R.string.transaction_details_signer_item_status_pending_label)
                }

            tvStatus.setTextColor(
                if (account.signed == true) {
                    ContextCompat.getColor(itemView.context, R.color.color_5cb87f)
                } else {
                    ContextCompat.getColor(itemView.context, R.color.color_fb9e00)
                }
            )
        }

        itemView.setSafeOnClickListener {
            val position = this@AccountWithStatusViewHolder.bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setSafeOnClickListener
            }

            itemClickListener(account)
        }

        itemView.setOnLongClickListener {
            val position = this@AccountWithStatusViewHolder.bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnLongClickListener false
            }

            itemLongClickListener(account)

            true
        }
    }
}