package com.lobstr.stellar.vault.data.key_store

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.lobstr.stellar.vault.data.mnemonic.MnemonicsMapper
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import java.math.BigInteger
import java.security.*
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.security.auth.x500.X500Principal

class KeyStoreRepositoryImpl(
    val context: Context,
    val prefsUtil: PrefsUtil,
    private val mnemonicsMapper: MnemonicsMapper
) :
    KeyStoreRepository {

    companion object {
        private const val DEFAULT_KEYSTORE_TYPE = "AndroidKeyStore"
        private const val CIPHER_AES_TRANSFORMATION_ALGORITHM: String = "AES/GCM/NoPadding"
        private const val CIPHER_RSA_TRANSFORMATION_ALGORITHM: String = "RSA/ECB/PKCS1Padding"
        private const val AUTHENTICATION_TAG_LENGTH = 128
    }

    private val keyStore: KeyStore by lazy {
        val keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE)
        keyStore.load(null)
        return@lazy keyStore
    }

    override fun encryptData(data: String, alias: String, aliasIV: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val masterKey = generateKeyWithKeyGenParameterSpec(alias)
            prefsUtil[alias] = encryptAESData(masterKey, data, aliasIV)
        } else {
            val keyPair = generateKeyWithKeyPairGeneratorSpec(alias)
            prefsUtil[alias] = encryptRSAData(keyPair.public, data)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun generateKeyWithKeyGenParameterSpec(alias: String): Key {
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, DEFAULT_KEYSTORE_TYPE)
        val builder = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setRandomizedEncryptionRequired(false)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        generator.init(builder.build())

        return generator.generateKey()
    }

    private fun generateKeyWithKeyPairGeneratorSpec(alias: String): KeyPair {
        val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, DEFAULT_KEYSTORE_TYPE)
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.YEAR, 10)

        val builder = KeyPairGeneratorSpec.Builder(context)
            .setAlias(alias)
            .setSerialNumber(BigInteger.ONE)
            .setSubject(X500Principal("CN=$alias CA Certificate"))
            .setStartDate(startDate.time)
            .setEndDate(endDate.time)
        generator.initialize(builder.build())

        return generator.generateKeyPair()
    }

    private fun encryptAESData(masterKey: Key, data: String, aliasIV: String): String {
        val inCipher = Cipher.getInstance(CIPHER_AES_TRANSFORMATION_ALGORITHM)
        inCipher.init(Cipher.ENCRYPT_MODE, masterKey, createGCMParameterSpec())
        prefsUtil[aliasIV] = inCipher.iv
        val encryptedBytes = inCipher.doFinal(data.toByteArray(Charsets.UTF_8))

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    private fun createGCMParameterSpec(): GCMParameterSpec {
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        return GCMParameterSpec(AUTHENTICATION_TAG_LENGTH, iv)
    }

    private fun encryptRSAData(publicKey: PublicKey, data: String): String {
        val inCipher = Cipher.getInstance(CIPHER_RSA_TRANSFORMATION_ALGORITHM)
        inCipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = inCipher.doFinal(data.toByteArray(Charsets.UTF_8))

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    override fun decryptData(alias: String, aliasIV: String): String? {
        val encryptedString = prefsUtil.getString(alias)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val masterKey = keyStore.getKey(alias, null) as SecretKey?
            val iv = prefsUtil.getString(aliasIV)

            return if (masterKey == null || iv.isNullOrEmpty() || encryptedString.isNullOrEmpty()) {
                null
            } else {
                decryptAESData(masterKey, Base64.decode(iv, Base64.DEFAULT), encryptedString)
            }
        } else {
            val entry = keyStore.getEntry(alias, null)
            val privateKey = (entry as? KeyStore.PrivateKeyEntry)?.privateKey
            return if (privateKey == null || encryptedString.isNullOrEmpty()) {
                null
            } else {
                decryptRSAData(privateKey, encryptedString)
            }
        }
    }

    override fun decryptDataToMnemonicItems(alias: String, aliasIV: String): ArrayList<MnemonicItem> {
        return mnemonicsMapper.transformMnemonicsStr(decryptData(alias, aliasIV))
    }

    private fun decryptAESData(masterKey: SecretKey, iv: ByteArray, encryptedString: String): String {
        val encryptedBytes = Base64.decode(encryptedString.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
        val inCipher = Cipher.getInstance(CIPHER_AES_TRANSFORMATION_ALGORITHM)
        inCipher.init(Cipher.DECRYPT_MODE, masterKey, GCMParameterSpec(AUTHENTICATION_TAG_LENGTH, iv))
        val decryptedBytes = inCipher.doFinal(encryptedBytes)

        return String(decryptedBytes)
    }

    private fun decryptRSAData(privateKey: PrivateKey, encryptedString: String): String {
        val encryptedBytes = Base64.decode(encryptedString.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
        val inCipher = Cipher.getInstance(CIPHER_RSA_TRANSFORMATION_ALGORITHM)
        inCipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = inCipher.doFinal(encryptedBytes)

        return String(decryptedBytes)
    }

    /**
     * Clears all aliases in the Android Key Store
     */
    override fun clearAll() {
        val aliases = keyStore.aliases()
        aliases.iterator().forEach {
            try {
                keyStore.deleteEntry(it)
            } catch (exc: KeyStoreException) {
                exc.printStackTrace()
            }
        }
    }

    override fun clear(alias: String) {
        try {
            keyStore.deleteEntry(alias)
        } catch (exc: KeyStoreException) {
            exc.printStackTrace()
        }
    }
}