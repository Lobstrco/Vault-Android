package com.lobstr.stellar.vault.presentation.entities.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Claimant(
    val destination: String
) : Parcelable