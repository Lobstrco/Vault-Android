package com.lobstr.stellar.vault.data.error.exeption


class HorizonException(details: String, val shortDetails: String? = null, val xdr: String) : DefaultException(details)