package com.lobstr.stellar.vault.presentation.util

import androidx.annotation.NonNull
import kotlin.math.min


object AppVersionUtil {

    /**
     * Method that compare one of the app version with another.
     */
    private fun isCurrentVersionLower(@NonNull oldVersion: String,
                              @NonNull newVersion: String): Boolean {
        val oldVersionArray = oldVersion.split("\\.".toRegex()).toTypedArray()
        val newVersionArray = newVersion.split("\\.".toRegex()).toTypedArray()
        val oldArrayCapacity = oldVersionArray.size
        val newArrayCapacity = newVersionArray.size
        var i = 0
        while (i < oldArrayCapacity || i < newArrayCapacity) {
            if (i < oldArrayCapacity && i < newArrayCapacity) {
                if (oldVersionArray[i].toInt() < newVersionArray[i].toInt()) {
                    return true
                } else if (oldVersionArray[i].toInt() > newVersionArray[i].toInt()) {
                    return false
                }
            } else if (i < oldArrayCapacity) {
                if (oldVersionArray[i].toInt() != 0) {
                    return false
                }
            } else {
                if (newVersionArray[i].toInt() != 0) {
                    return true
                }
            }
            i++
        }
        return false
    }

    /**
     * Method that calculates the current version equals with specified.
     */
    private fun isCurrentVersionEquals(@NonNull deviceVersion: String,
                               @NonNull sideVersion: String): Boolean {
        val oldVersionArray = deviceVersion.split("\\.".toRegex()).toTypedArray()
        val newVersionArray = sideVersion.split("\\.".toRegex()).toTypedArray()
        val capacityDifference = min(oldVersionArray.size, newVersionArray.size)
        for (i in 0 until capacityDifference) {
            if (parseStringToInt(oldVersionArray[i]) != parseStringToInt(newVersionArray[i])) {
                return false
            }
        }
        return true
    }

    /**
     * Method that calculates the current version higher or equals with specified.
     */
    private fun isCurrentVersionHigher(@NonNull deviceVersion: String,
                               @NonNull sideVersion: String): Boolean {
        val oldVersionArray = deviceVersion.split("\\.".toRegex()).toTypedArray()
        val newVersionArray = sideVersion.split("\\.".toRegex()).toTypedArray()
        val oldArrayCapacity = oldVersionArray.size
        val newArrayCapacity = newVersionArray.size
        var i = 0
        while (i < oldArrayCapacity || i < newArrayCapacity) {
            if (i < oldArrayCapacity && i < newArrayCapacity) {
                if (oldVersionArray[i].toInt() > newVersionArray[i].toInt()) {
                    return true
                } else if (oldVersionArray[i].toInt() < newVersionArray[i].toInt()) {
                    return false
                }
            } else if (i < oldArrayCapacity) {
                if (oldVersionArray[i].toInt() != 0) {
                    return true
                }
            } else {
                if (newVersionArray[i].toInt() != 0) {
                    return false
                }
            }
            i++
        }
        return false
    }

    /**
     * Method that parses String to Integer.
     *
     * @param string String which need parsing.
     * @return Parsed value otherwise -1.
     */
    private fun parseStringToInt(string: String): Int {
        return try {
            string.toInt()
        } catch (exception: NumberFormatException) {
            -1
        }
    }

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    fun compareVersions(thisValue: String, specifiedValue: String): Int {
        if (isCurrentVersionEquals(thisValue, specifiedValue)) {
            return 0
        }
        if (isCurrentVersionLower(thisValue, specifiedValue)) {
            return -1
        }
        if (isCurrentVersionHigher(thisValue, specifiedValue)) {
            return 1
        }
        return 0
    }
}