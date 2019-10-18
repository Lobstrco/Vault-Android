package com.lobstr.stellar.vault.data.mnemonic

import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem


class MnemonicsMapper {

    fun transformMnemonicsStr(mnemonicsStr: String?): ArrayList<MnemonicItem> {
        val mnemonicItems = arrayListOf<MnemonicItem>()

        if (!mnemonicsStr.isNullOrEmpty()) {
            val mnemonicsStrList = mnemonicsStr.split(" ".toRegex()).dropLastWhile { it.isEmpty() }

            for (mnemonic in mnemonicsStrList) {
                mnemonicItems.add(MnemonicItem(mnemonic))
            }
        }

        return mnemonicItems
    }

    fun transformMnemonicsArray(mnemonicsArray: CharArray): ArrayList<MnemonicItem> {
        val mnemonicItems = arrayListOf<MnemonicItem>()

        val mnemonicsStrList = String(mnemonicsArray).split(" ".toRegex()).dropLastWhile { it.isEmpty() }

        for (mnemonic in mnemonicsStrList) {
            mnemonicItems.add(MnemonicItem(mnemonic))
        }

        return mnemonicItems
    }
}