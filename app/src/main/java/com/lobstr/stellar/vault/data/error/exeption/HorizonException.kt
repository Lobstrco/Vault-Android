package com.lobstr.stellar.vault.data.error.exeption


class HorizonException(details: String, val xdr: String) : DefaultException(details)