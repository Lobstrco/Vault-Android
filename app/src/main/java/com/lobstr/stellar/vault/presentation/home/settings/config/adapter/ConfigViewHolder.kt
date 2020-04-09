package com.lobstr.stellar.vault.presentation.home.settings.config.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.presentation.entities.config.Config
import kotlinx.android.synthetic.main.adapter_item_config.view.*


class ConfigViewHolder(itemView: View, private val listener: OnConfigItemListener) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(config: Config, selectedType: Byte, itemsCount: Int) {
        itemView.tvConfig.text = config.text
        itemView.ivConfigStatus.visibility =
            if (config.type == selectedType) View.VISIBLE else View.INVISIBLE
        itemView.divider.visibility = calculateDividerVisibility(itemsCount)

        itemView.setOnClickListener {
            val position = this@ConfigViewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            listener.onConfigItemClick(config, selectedType)
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