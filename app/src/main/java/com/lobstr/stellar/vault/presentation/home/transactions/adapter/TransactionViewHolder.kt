package com.lobstr.stellar.vault.presentation.home.transactions.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.text.TextUtils
import android.text.format.DateFormat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.Transaction
import com.lobstr.stellar.tsmapper.presentation.util.Constant.TransactionType.AUTH_CHALLENGE
import com.lobstr.stellar.tsmapper.presentation.util.TsUtil
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.AdapterItemTransactionBinding
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import java.time.ZonedDateTime

class TransactionViewHolder(private val binding: AdapterItemTransactionBinding, private val itemClickListener: (transactionItem: TransactionItem) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: TransactionItem) {
        val context: Context = itemView.context

        val isSequenceValid = item.sequenceOutdatedAt.isNullOrEmpty()

        binding.statusView.background.colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(context, if (isSequenceValid) R.color.color_primary else R.color.color_4d000000),
            PorterDuff.Mode.SRC_IN
        )

        binding.tvTransactionInvalid.isVisible = !isSequenceValid

        binding.tvSourceAccount.isVisible = item.transaction.sourceAccount.isNotEmpty()

        binding.flIdentityContainer.isVisible = item.transaction.sourceAccount.isNotEmpty()

        // set user icon
        Glide.with(itemView.context)
            .load(AppUtil.createUserIconLink(item.transaction.sourceAccount))
            .placeholder(R.drawable.ic_person)
            .into(binding.ivIdentity)

        binding.tvTransactionItemDate.text = AppUtil.formatDate(
            ZonedDateTime.parse(item.addedAt).toInstant().toEpochMilli(),
            if(DateFormat.is24HourFormat(context)) "MMM dd yyyy HH:mm" else "MMM dd yyyy h:mm a"
        )

        binding.tvSourceAccount.ellipsize = if (item.federation.isNullOrEmpty() && item.name.isNullOrEmpty()) TextUtils.TruncateAt.MIDDLE else TextUtils.TruncateAt.END
        binding.tvSourceAccount.text =
            if (item.federation.isNullOrEmpty() && item.name.isNullOrEmpty()) AppUtil.ellipsizeStrInMiddle(
                item.transaction.sourceAccount,
                PK_TRUNCATE_COUNT
            ) else {
                if(!item.name.isNullOrEmpty()){
                    item.name
                } else {
                    item.federation
                }
            }

        binding.tvTransactionItemOperation.text = getTransactionName(context, item.transaction, item.transaction.transactionType)
        itemView.setSafeOnClickListener {
            val position = this@TransactionViewHolder.bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setSafeOnClickListener
            }

            itemClickListener(item)
        }
    }

    private fun getTransactionName(context: Context, transaction: Transaction, type: String?): String {
        return if (transaction.operations.isNotEmpty()) {
            when(type) {
                AUTH_CHALLENGE -> context.getString(R.string.text_transaction_challenge)
                else -> if (transaction.operations.size == 1) {
                    context.getString(TsUtil.getTransactionOperationName(transaction.operations[0]))
                } else {
                    context.getString(R.string.text_operation_name_several_operation, transaction.operations.size)
                }
            }
        } else {
            context.getString(R.string.text_operation_name_unknown)
        }
    }
}