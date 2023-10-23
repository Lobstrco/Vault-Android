package com.lobstr.stellar.vault.presentation.util

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.content.IntentCompat
import androidx.core.os.BundleCompat
import java.io.Serializable

// TODO Use Compat version in future (https://issuetracker.google.com/issues/242048899) for the old implementations.
//  Don't use the new getXXX(key, class) on Android 13 - crashes. Fixed in the Compat versions (https://issuetracker.google.com/issues/240585930).

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? =
    BundleCompat.getParcelable(this, key, T::class.java)

inline fun <reified T : Parcelable> Bundle.parcelableArray(key: String): Array<out Parcelable>? =
    BundleCompat.getParcelableArray(this, key, T::class.java)

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? =
    BundleCompat.getParcelableArrayList(this, key, T::class.java)

inline fun <reified T : Parcelable> Bundle.sparseParcelableArray(key: String): SparseArray<T>? =
    BundleCompat.getSparseParcelableArray(this, key, T::class.java)

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    SDK_INT > Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}
//inline fun <reified T : Serializable> Bundle.serializable(key: String): T? =
//    BundleCompat.getSerializable(this, key, T::class.java)


inline fun <reified T : Parcelable> Intent.parcelableExtra(key: String): T? =
    IntentCompat.getParcelableExtra(this, key, T::class.java)

inline fun <reified T : Parcelable> Intent.parcelableArrayExtra(key: String): Array<out Parcelable>? =
    IntentCompat.getParcelableArrayExtra(this, key, T::class.java)

inline fun <reified T : Parcelable> Intent.parcelableArrayListExtra(key: String): ArrayList<T>? =
    IntentCompat.getParcelableArrayListExtra(this, key, T::class.java)

inline fun <reified T : Serializable> Intent.serializableExtra(key: String): T? = when {
    SDK_INT > Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}
//inline fun <reified T : Serializable> Intent.serializableExtra(key: String): T? =
//    IntentCompat.getSerializableExtra(this, key, T::class.java)