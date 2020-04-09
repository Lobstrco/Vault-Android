package com.lobstr.stellar.vault.presentation.home.settings.config.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.config.Config

class ConfigAdapter(
    private var configs: List<Config>,
    private var selectedType: Byte,
    private val listener: OnConfigItemListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    fun setSelectedType(selectedType: Byte) {
        this.selectedType = selectedType
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_item_config, viewGroup, false)
        return ConfigViewHolder(view, listener)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as ConfigViewHolder).bind(configs[position], selectedType, itemCount)
    }

    override fun getItemCount(): Int {
        return configs.size
    }
}