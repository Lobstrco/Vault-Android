package com.lobstr.stellar.vault.data.key_store

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import java.math.BigInteger
import java.security.KeyStore
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.security.auth.x500.X500Principal

class KeyStoreRepositoryImpl(val context: Context, val prefsUtil: PrefsUtil) : KeyStoreRepository {

    companion object {
        private const val DEFAULT_KEYSTORE_TYPE = "AndroidKeyStore"
        private const val CIPHER_TRANSFORMATION_ALGORITHM: String = "AES/GCM/NoPadding"
        private const val AUTHENTICATION_TAG_LENGTH = 128
    }

    private val keyStore: KeyStore by lazy {
        val keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE)
        keyStore.load(null)
        return@lazy keyStore
    }

    override fun encryptData(data: String, alias: String, aliasIV: String) {
        val masterKey = createKey(alias)
        prefsUtil[alias] = encryptData(masterKey, data, aliasIV)
    }

    private fun encryptData(masterKey: SecretKey, data: String, aliasIV: String): String {
        val inCipher = Cipher.getInstance(CIPHER_TRANSFORMATION_ALGORITHM)
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

    private fun createKey(alias: String): SecretKey {
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, DEFAULT_KEYSTORE_TYPE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            generateKeyWithKeyGenParameterSpec(generator, alias)
        } else {
            generateKeyWithKeyPairGeneratorSpec(generator, alias)
        }

        return generator.generateKey()
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun generateKeyWithKeyGenParameterSpec(generator: KeyGenerator, alias: String) {
        val builder = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setRandomizedEncryptionRequired(false)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        generator.init(builder.build())
    }

    private fun generateKeyWithKeyPairGeneratorSpec(generator: KeyGenerator, alias: String) {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.YEAR, 10)

        val builder = KeyPairGeneratorSpec.Builder(context)
            .setAlias(alias)
            .setSerialNumber(BigInteger.ONE)
            .setSubject(X500Principal("CN=$alias CA Certificate"))
            .setStartDate(startDate.time)
            .setEndDate(endDate.time)

        generator.init(builder.build())
    }

    private fun getKeyFromAndroidKeystore(alias: String): SecretKey? {
        return keyStore.getKey(alias, null) as SecretKey?
    }

    override fun decryptData(alias: String, aliasIV: String): String? {
        val masterKey = getKeyFromAndroidKeystore(alias)
        val iv = prefsUtil.getString(aliasIV)
        val encryptedString = prefsUtil.getString(alias)

        return if (masterKey == null || iv.isNullOrEmpty() || encryptedString.isNullOrEmpty()) {
            null
        } else {
            decryptData(masterKey, Base64.decode(iv, Base64.DEFAULT), encryptedString)
        }
    }

    private fun decryptData(masterKey: SecretKey, iv: ByteArray, encryptedString: String): String {
        val encryptedBytes = Base64.decode(encryptedString.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
        val inCipher = Cipher.getInstance(CIPHER_TRANSFORMATION_ALGORITHM)
        inCipher.init(Cipher.DECRYPT_MODE, masterKey, GCMParameterSpec(AUTHENTICATION_TAG_LENGTH, iv))
        val decryptedBytes = inCipher.doFinal(encryptedBytes)

        return String(decryptedBytes)
    }

    /**
     * Clears all aliases in the Android Key Store
     */
    override fun clearAll() {
        val aliases = keyStore.aliases()
        aliases.iterator().forEach {
            keyStore.deleteEntry(it)
        }
    }

    override fun clear(alias: String) {
        keyStore.deleteEntry(alias)
    }
}