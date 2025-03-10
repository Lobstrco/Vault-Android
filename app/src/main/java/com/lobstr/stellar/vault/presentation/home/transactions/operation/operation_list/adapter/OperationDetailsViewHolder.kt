package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list.adapter

import android.text.TextUtils
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.vault.databinding.AdapterItemOperationDetailsBinding
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener

class OperationDetailsViewHolder(
    private val binding: AdapterItemOperationDetailsBinding,
    private val itemClickListener: (key: String, value: String?, tag: Any?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(name: String, value: String?, tag: Any?) {
        binding.apply {
            val isPublicKeyTag by lazy { AppUtil.isValidAccount(tag as? String) }
            val isPublicKeyValue by lazy { AppUtil.isValidAccount(value) }
            val isAssetTag by lazy {  tag is Asset.CanonicalAsset }
            val isContractIdValue by lazy { AppUtil.isValidContractID(value) }

            val isSelectableField = isPublicKeyTag || isAssetTag || isContractIdValue

            tvFieldName.text = name
            tvFieldValue.ellipsize = when (name) {
                AppUtil.getString(com.lobstr.stellar.tsmapper.R.string.op_field_destination_federation) -> TextUtils.TruncateAt.END
                else -> {
                    when {
                        // Case for the Account Name.
                        isPublicKeyTag && !isPublicKeyValue -> TextUtils.TruncateAt.END
                        else -> TextUtils.TruncateAt.MIDDLE
                    }
                }
            }
            tvFieldValue.isSingleLine = value?.contains("\n") != true

            tvFieldValue.text =
                if (isPublicKeyValue || isContractIdValue) {
                    AppUtil.ellipsizeStrInMiddle(value, Constant.Util.PK_TRUNCATE_COUNT)
                } else {
                    value
                }

            root.foreground = if (isSelectableField) {
                val outValue = TypedValue()
                if (itemView.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)) {
                    ContextCompat.getDrawable(itemView.context, outValue.resourceId)
                } else {
                    null
                }
            } else {
                null
            }

            if (isSelectableField) {
                itemView.setSafeOnClickListener {
                    val position = this@OperationDetailsViewHolder.bindingAdapterPosition
                    if (position == RecyclerView.NO_POSITION) {
                        return@setSafeOnClickListener
                    }

                    itemClickListener(name, value, tag)
                }
            } else {
                itemView.setOnClickListener(null)
            }
        }
    }
}