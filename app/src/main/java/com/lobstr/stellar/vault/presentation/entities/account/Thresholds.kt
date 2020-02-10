package com.lobstr.stellar.vault.presentation.entities.account

/**
 * Represents account thresholds.
 */
data class Thresholds(
    val lowThreshold: Int,
    val medThreshold: Int,
    val highThreshold: Int
)
