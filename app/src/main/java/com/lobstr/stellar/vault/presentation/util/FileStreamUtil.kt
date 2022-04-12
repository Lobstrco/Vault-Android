package com.lobstr.stellar.vault.presentation.util

import android.content.Context
import java.io.*

class FileStreamUtil(private val context: Context) {

    companion object {
        const val TRANSACTION_CONFIRMATION_STORAGE = "TRANSACTION_CONFIRMATION_STORAGE"
        const val ACCOUNT_NAME_STORAGE = "ACCOUNT_NAME_STORAGE"
        const val AUTH_TOKEN_STORAGE = "AUTH_TOKEN_STORAGE"
        const val NOTIFICATION_STORAGE = "NOTIFICATION_STORAGE"
        const val FCM_STORAGE = "FCM_STORAGE"
    }

    fun <T> write(
        fileName: String,
        data: T
    ) {
        val fos: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        val os = ObjectOutputStream(fos)
        os.writeObject(data)
        os.close()
        fos.close()
    }

    fun <T> read(fileName: String): T? {
        return try {
            val fos: FileInputStream = context.openFileInput(fileName)
            val os = ObjectInputStream(fos)
            val data = os.readObject() as T
            os.close()
            fos.close()
            data
        } catch (e: Exception) {
            null
        }
    }

    fun deleteFile(name: String): Boolean {
        val file: File = context.getFileStreamPath(name)
        return file.delete()
    }
}