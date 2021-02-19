package com.lobstr.stellar.vault.presentation.entities.mnemonic

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MnemonicItem(val value: String, var hide: Boolean = false) : Parcelable