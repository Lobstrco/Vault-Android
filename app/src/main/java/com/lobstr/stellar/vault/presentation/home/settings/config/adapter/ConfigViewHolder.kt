package com.lobstr.stellar.vault.presentation.home.settings.config.adapter

import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.databinding.AdapterItemConfigBinding
import com.lobstr.stellar.vault.presentation.entities.config.Config
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener


class ConfigViewHolder(private val binding: AdapterItemConfigBinding, private val itemClickListener: (config: Config, selectedType: Byte) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(config: Config, selectedType: Byte) {
        binding.apply {
            tvConfig.text = config.text
            ivConfigStatus.isInvisible = config.type != selectedType
        }

        itemView.setSafeOnClickListener {
            val position = this@ConfigViewHolder.bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setSafeOnClickListener
            }

            itemClickListener(config, selectedType)
        }
    }
}