package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list.adapter

import android.text.TextUtils
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.AdapterItemOperationDetailsBinding
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant

class OperationDetailsViewHolder(
    private val binding: AdapterItemOperationDetailsBinding,
    private val itemClickListener: (key: String, value: String?, tag: Any?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(name: String, value: String?, tag: Any?) {
        binding.tvFieldName.text = name
        binding.tvFieldValue.ellipsize = when (name) {
            AppUtil.getString(R.string.op_field_destination_federation) -> TextUtils.TruncateAt.END
            else -> TextUtils.TruncateAt.MIDDLE
        }
        binding.tvFieldValue.isSingleLine = value?.contains("\n") != true

        val isValuePublicKey = AppUtil.isPublicKey(value)
        val isAssetTag = tag is Asset

        binding.tvFieldValue.text =
            if (isValuePublicKey) {
                AppUtil.ellipsizeStrInMiddle(value, Constant.Util.PK_TRUNCATE_COUNT)
            } else {
                value
            }

        // Set selectable foreground for public key values and asset code (fields with Asset tag).
        binding.root.foreground = if(isValuePublicKey || isAssetTag) {
            val outValue = TypedValue()
            if (itemView.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)) {
                ContextCompat.getDrawable(itemView.context, outValue.resourceId)
            } else {
                null
            }
        } else {
            null
        }

        // Set Click Listener only for public key values and asset code (fields with Asset tag).
        if(isValuePublicKey || isAssetTag) {
            itemView.setOnClickListener {
                val position = this@OperationDetailsViewHolder.bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                itemClickListener(name, value, tag)
            }
        } else {
            itemView.setOnClickListener(null)
        }
    }
}