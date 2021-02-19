package com.lobstr.stellar.vault.presentation.home.settings.config.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.databinding.AdapterItemConfigBinding
import com.lobstr.stellar.vault.presentation.entities.config.Config


class ConfigViewHolder(private val binding: AdapterItemConfigBinding, private val itemClickListener: (config: Config, selectedType: Byte) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(config: Config, selectedType: Byte, itemsCount: Int) {
        binding.tvConfig.text = config.text
        binding.ivConfigStatus.visibility =
            if (config.type == selectedType) View.VISIBLE else View.INVISIBLE
        binding.divider.visibility = calculateDividerVisibility(itemsCount)

        itemView.setOnClickListener {
            val position = this@ConfigViewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            itemClickListener(config, selectedType)
        }
    }

    /**
     * Don't show divider for last ore one item.
     * @param itemsCount Count of items.
     * @return Visibility.
     */
    private fun calculateDividerVisibility(itemsCount: Int): Int {
        return if (itemsCount == 1 || adapterPosition == itemsCount - 1) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }
}