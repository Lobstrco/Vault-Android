package com.lobstr.stellar.vault.presentation.home.transactions.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.AdapterItemTransactionBinding
import com.lobstr.stellar.vault.presentation.entities.transaction.Transaction
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT
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

        binding.tvTransactionInvalid.visibility =
            if (isSequenceValid) View.GONE else View.VISIBLE

        binding.tvSourceAccount.visibility =
            if (item.transaction.sourceAccount.isNullOrEmpty()) View.GONE else View.VISIBLE

        binding.flIdentityContainer.visibility =
            if (item.transaction.sourceAccount.isNullOrEmpty()) View.GONE else View.VISIBLE

        // set user icon
        Glide.with(itemView.context)
            .load(
                Constant.Social.USER_ICON_LINK
                    .plus(item.transaction.sourceAccount)
                    .plus(".png")
            )
            .placeholder(R.drawable.ic_person)
            .into(binding.ivIdentity)

        binding.tvTransactionItemDate.text = AppUtil.formatDate(
            ZonedDateTime.parse(item.addedAt).toInstant().toEpochMilli(),
            if(DateFormat.is24HourFormat(context)) "MMM dd yyyy HH:mm" else "MMM dd yyyy h:mm a"
        )

        binding.tvSourceAccount.ellipsize = if (item.transaction.federation.isNullOrEmpty()) TextUtils.TruncateAt.MIDDLE else TextUtils.TruncateAt.END
        binding.tvSourceAccount.text =
            if (item.transaction.federation.isNullOrEmpty()) AppUtil.ellipsizeStrInMiddle(
                item.transaction.sourceAccount,
                PK_TRUNCATE_COUNT
            ) else item.transaction.federation

        binding.tvTransactionItemOperation.text = getOperationName(context, item.transaction, item.transactionType)
        itemView.setOnClickListener {
            val position = this@TransactionViewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            itemClickListener(item)
        }
        item.transactionType
    }

    private fun getOperationName(context: Context, transaction: Transaction, type: String?): String {
        return if (transaction.operations.isNotEmpty()) {
            if (transaction.operations.size == 1) {
                context.getString(AppUtil.getTransactionOperationName(transaction.operations[0], type))
            } else {
                String.format(
                    context.getString(R.string.text_operation_name_several_operation),
                    transaction.operations.size
                )
            }
        } else {
            context.getString(R.string.text_operation_name_unknown)
        }
    }
}