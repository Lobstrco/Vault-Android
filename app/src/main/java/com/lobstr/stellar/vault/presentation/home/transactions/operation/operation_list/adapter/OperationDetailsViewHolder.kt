package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list.adapter

import android.text.TextUtils
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.AdapterItemOperationDetailsBinding
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant


class OperationDetailsViewHolder(private val binding: AdapterItemOperationDetailsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(name: String, value: String?) {
        binding.tvOperationFieldName.text = name
        binding.tvOperationFieldValue.ellipsize = when (name) {
            AppUtil.getString(R.string.op_field_destination_federation) -> TextUtils.TruncateAt.END
            else -> TextUtils.TruncateAt.MIDDLE
        }
        binding.tvOperationFieldValue.isSingleLine = value?.contains("\n") != true

        binding.tvOperationFieldValue.text =
            if (AppUtil.isPublicKey(value)) {
                AppUtil.ellipsizeStrInMiddle(value, Constant.Util.PK_TRUNCATE_COUNT)
            } else {
                value
            }
    }
}