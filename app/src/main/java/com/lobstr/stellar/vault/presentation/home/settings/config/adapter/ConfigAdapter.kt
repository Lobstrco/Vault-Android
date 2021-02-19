package com.lobstr.stellar.vault.presentation.home.settings.config.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lobstr.stellar.vault.databinding.AdapterItemConfigBinding
import com.lobstr.stellar.vault.presentation.entities.config.Config

class ConfigAdapter(
    private var configs: List<Config>,
    private var selectedType: Byte,
    private val itemClickListener: (config: Config, selectedType: Byte) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    fun setSelectedType(selectedType: Byte) {
        this.selectedType = selectedType
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return ConfigViewHolder(AdapterItemConfigBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false), itemClickListener)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as ConfigViewHolder).bind(configs[position], selectedType, itemCount)
    }

    override fun getItemCount(): Int {
        return configs.size
    }
}