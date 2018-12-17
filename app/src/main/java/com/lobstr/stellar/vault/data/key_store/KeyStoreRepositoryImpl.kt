package com.lobstr.stellar.vault.data.key_store

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

class KeyStoreRepositoryImpl(val context: Context) : KeyStoreRepository {

    companion object {
        private const val DEFAULT_KEYSTORE_TYPE = "AndroidKeyStore"
        private const val CIPHER_TRANSFORMATION_ALGORITHM: String = "RSA/ECB/PKCS1Padding"
    }

    private val keyStore: KeyStore by lazy {
        val keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE)
        keyStore.load(null)
        return@lazy keyStore
    }

    /**
     * @return encrypted secret key
     */
    override fun encryptSecretKey(pin: String, secretKey: String): String {
        val keyPair = createAsymmetricKeyPair(pin)
        return encryptData(keyPair, secretKey)
    }

    private fun encryptData(masterKey: KeyPair, secretKey: String): String {
        val inCipher = Cipher.getInstance(CIPHER_TRANSFORMATION_ALGORITHM)
        inCipher.init(Cipher.ENCRYPT_MODE, masterKey.public)
        val encryptedBytes = inCipher.doFinal(secretKey.toByteArray(Charsets.UTF_8))

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    /**
     * Creates asymmetric RSA key with default and saves it to Android Key Store.
     *
     * @return KeyPair for encrypt or decrypt data
     */
    private fun createAsymmetricKeyPair(pin: String): KeyPair {
        val generator = KeyPairGenerator.getInstance("RSA", DEFAULT_KEYSTORE_TYPE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            generateKeyWithKeyGenParameterSpec(generator, pin)
        } else {
            generateKeyWithKeyPairGeneratorSpec(generator, pin)
        }

        return generator.generateKeyPair()
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun generateKeyWithKeyGenParameterSpec(generator: KeyPairGenerator, alias: String) {
        val builder = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
        generator.initialize(builder.build())
    }

    private fun generateKeyWithKeyPairGeneratorSpec(generator: KeyPairGenerator, alias: String) {
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
    }

    /**
     * @return asymmetric keypair for encrypt or decrypt data or null if any key with given alias not exists
     */
    override fun getAsymmetricKeyPair(pin: String): KeyPair? {
        val privateKey = keyStore.getKey(pin, null) as PrivateKey?
        val publicKey = keyStore.getCertificate(pin)?.publicKey

        return if (privateKey != null && publicKey != null) {
            KeyPair(publicKey, privateKey)
        } else {
            null
        }
    }

    /**
     * @return secret key
     */
    override fun decryptSecretKey(pin: String, secretKey: String): String {
        val keyPair = getAsymmetricKeyPair(pin)
        return decryptData(keyPair!!, secretKey)
    }

    private fun decryptData(masterKey: KeyPair, encryptedString: String): String {
        val encryptedBytes = Base64.decode(encryptedString.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
        val inCipher = Cipher.getInstance(CIPHER_TRANSFORMATION_ALGORITHM)
        inCipher.init(Cipher.DECRYPT_MODE, masterKey.private)
        val decryptedBytes = inCipher.doFinal(encryptedBytes)

        return String(decryptedBytes)
    }

    /**
     * Clears all aliases in the Android Key Store
     */
    override fun clear() {
        val aliases = keyStore.aliases()
        aliases.iterator().forEach {
            keyStore.deleteEntry(it)
        }
    }
}