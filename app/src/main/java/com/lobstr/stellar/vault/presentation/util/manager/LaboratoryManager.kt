package com.lobstr.stellar.vault.presentation.util.manager

import com.lobstr.stellar.vault.presentation.util.Constant

fun composeViewXdrUrl(
    xdr: String,
    network: String = Constant.Laboratory.NETWORK.MAINNET,
    horizonUrl: String = "",
    rpcUrl: String = "",
    passphrase: String = "",
    type: String = Constant.Laboratory.Type.TRANSACTION_ENVELOPE,
): String {
    return Constant.Laboratory.URL
        .plus(Constant.Laboratory.XDR_VIEW)
        .plus(String.format(
            Constant.Laboratory.HORIZON_QUERY,
            network,
            network.replaceFirstChar { it.uppercase() },
            maskInput(horizonUrl),
            maskInput(rpcUrl),
            maskInput(passphrase)
        ))
        .plus(String.format(
            Constant.Laboratory.XDR_QUERY,
            maskInput(xdr),
            type
        ))
}

private fun maskInput(input: String) = input.let {
    if (it.isEmpty()) {
        it
    } else {
        it.replace("/", "//")
            .replace(";", "/;")
            .replace("&", "/&")
    }
}