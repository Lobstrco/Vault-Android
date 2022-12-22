package com.lobstr.stellar.vault.presentation.entities.error

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Error(val description: String, val shortDescription: String?, val xdr: String) :
    Parcelable
