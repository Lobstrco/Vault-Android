package com.lobstr.stellar.vault.presentation.entities.fcm

data class FcmResult(

    val id: Long,

    val name: String?,

    val registrationId: String,

    val deviceId: String?,

    val isActive: Boolean,

    val dateCreated: String,

    val type: String
)