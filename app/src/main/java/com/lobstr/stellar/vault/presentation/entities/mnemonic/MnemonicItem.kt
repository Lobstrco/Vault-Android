package com.lobstr.stellar.vault.presentation.entities.mnemonic

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MnemonicItem(val value: String, var hide: Boolean = false) : Parcelable