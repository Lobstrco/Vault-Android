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
import com.lobstr.stellar.vault.presentation.entities.transaction.Transaction
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT
import kotlinx.android.synthetic.main.adapter_item_transaction.view.*
import java.time.ZonedDateTime

class TransactionViewHolder(itemView: View, private val listener: OnTransactionItemClicked) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(item: TransactionItem) {
        val context: Context = itemView.context

        val isSequenceValid = item.sequenceOutdatedAt.isNullOrEmpty()

        itemView.statusView.background.colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(context, if (isSequenceValid) R.color.color_primary else R.color.color_4D000000),
            PorterDuff.Mode.SRC_IN
        )

        itemView.tvTransactionInvalid.visibility =
            if (isSequenceValid) View.GONE else View.VISIBLE

        itemView.tvSourceAccount.visibility =
            if (item.transaction.sourceAccount.isNullOrEmpty()) View.GONE else View.VISIBLE

        itemView.flIdentityContainer.visibility =
            if (item.transaction.sourceAccount.isNullOrEmpty()) View.GONE else View.VISIBLE

        // set user icon
        Glide.with(itemView.context)
            .load(
                Constant.Social.USER_ICON_LINK
                    .plus(item.transaction.sourceAccount)
                    .plus(".png")
            )
            .placeholder(R.drawable.ic_person)
            .into(itemView.ivIdentity)

        itemView.tvTransactionItemDate.text = AppUtil.formatDate(
            ZonedDateTime.parse(item.addedAt).toInstant().toEpochMilli(),
            if(DateFormat.is24HourFormat(context)) "MMM dd yyyy HH:mm" else "MMM dd yyyy h:mm a"
        )

        itemView.tvSourceAccount.ellipsize = if (item.transaction.federation.isNullOrEmpty()) TextUtils.TruncateAt.MIDDLE else TextUtils.TruncateAt.END
        itemView.tvSourceAccount.text =
            if (item.transaction.federation.isNullOrEmpty()) AppUtil.ellipsizeStrInMiddle(
                item.transaction.sourceAccount,
                PK_TRUNCATE_COUNT
            ) else item.transaction.federation

        itemView.tvTransactionItemOperation.text = getOperationName(item.transaction, context)
        itemView.setOnClickListener {
            val position = this@TransactionViewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            listener.onTransactionItemClick(item)
        }
    }

    private fun getOperationName(transaction: Transaction, context: Context): String {
        return if (transaction.operations.isNotEmpty()) {
            if (transaction.operations.size == 1) {
                context.getString(AppUtil.getTransactionOperationName(transaction.operations[0]))
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