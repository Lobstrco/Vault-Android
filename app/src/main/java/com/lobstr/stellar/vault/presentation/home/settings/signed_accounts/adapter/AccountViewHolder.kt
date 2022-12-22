package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter

import android.text.TextUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.AdapterItemAccountBinding
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener

class AccountViewHolder(
    private val binding: AdapterItemAccountBinding,
    private val itemClickListener: (account: Account) -> Unit,
    private val itemLongClickListener: (account: Account) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(account: Account) {
        binding.apply {
            // Set user icon.
            Glide.with(itemView.context)
                .load(AppUtil.createUserIconLink(account.address))
                .placeholder(R.drawable.ic_person)
                .into(ivIdentity)

            tvAccountNameBottom.isVisible = !(account.federation.isNullOrEmpty() && account.name.isNullOrEmpty())

            tvAccountNameBottom.text = when {
                !account.federation.isNullOrEmpty() || !account.name.isNullOrEmpty() -> AppUtil.ellipsizeStrInMiddle(
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
                    AppUtil.ellipsizeStrInMiddle(account.address, PK_TRUNCATE_COUNT)
                } else {
                    if(!account.name.isNullOrEmpty()){
                        account.name
                    } else {
                        account.federation
                    }
                }
        }

        itemView.setSafeOnClickListener {
            val position = this@AccountViewHolder.bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setSafeOnClickListener
            }

            itemClickListener(account)
        }

        itemView.setOnLongClickListener {
            val position = this@AccountViewHolder.bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnLongClickListener false
            }

            itemLongClickListener(account)

            true
        }
    }
}