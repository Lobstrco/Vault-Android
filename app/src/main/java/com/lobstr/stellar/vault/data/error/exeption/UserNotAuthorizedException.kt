package com.lobstr.stellar.vault.data.error.exeption

import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException.Action.DEFAULT

class UserNotAuthorizedException(details: String, val action: Byte = DEFAULT) :
    DefaultException(details) {
    object Action {
        const val DEFAULT: Byte = 0
        const val AUTH_REQUIRED: Byte = 1
    }
}