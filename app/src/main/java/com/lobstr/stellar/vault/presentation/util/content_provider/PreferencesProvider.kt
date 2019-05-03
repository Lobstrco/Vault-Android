package com.lobstr.stellar.vault.presentation.util.content_provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.util.PrefsUtil

/**
 * Used for pass Public Shared Preferences data to other apps
 * Use URI pattern: content://public.shared.data.preference/{path}
 */
class PreferencesProvider : ContentProvider() {

    companion object {
        const val PUBLIC_KEY_PATH = "public_key"
        const val PUBLIC_KEY_CODE = 1
    }

    private lateinit var prefsUtil: PrefsUtil

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    override fun onCreate(): Boolean {
        prefsUtil = PrefsUtil(android.preference.PreferenceManager.getDefaultSharedPreferences(context))
        // see authority in product flavor resValue
        uriMatcher.addURI(context?.getString(R.string.authority), PUBLIC_KEY_PATH, PUBLIC_KEY_CODE)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            PUBLIC_KEY_CODE -> {
                // user was authorized when token!=null and public key was created
                if (prefsUtil.authToken.isNullOrEmpty()) {
                    return null
                }

                val matrixCursor = MatrixCursor(arrayOf(PUBLIC_KEY_PATH))
                matrixCursor.addRow(arrayOf(prefsUtil.publicKey))
                matrixCursor
            }
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}