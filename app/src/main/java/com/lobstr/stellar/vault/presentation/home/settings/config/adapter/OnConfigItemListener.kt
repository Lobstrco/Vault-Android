package com.lobstr.stellar.vault.presentation.home.settings.config.adapter

import com.lobstr.stellar.vault.presentation.entities.config.Config


interface OnConfigItemListener {
    fun onConfigItemClick(config: Config, selectedType: Byte)
}