package com.lobstr.stellar.vault.presentation.util

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.LayoutCopyClipboardBinding
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.util.ClipboardView.Type.TYPE_ALL
import com.lobstr.stellar.vault.presentation.util.ClipboardView.Type.TYPE_PK
import com.lobstr.stellar.vault.presentation.util.ClipboardView.Type.TYPE_XDR
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@AndroidEntryPoint
class ClipboardView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs),
    DefaultLifecycleObserver {

    @Inject
    lateinit var stellarRepo: StellarRepository

    companion object {
        const val BUNDLE_STATE = "BUNDLE_STATE"
        const val BUNDLE_DATA = "BUNDLE_DATA"
        const val BUNDLE_IS_FIRST_LOAD = "BUNDLE_IS_FIRST_LOAD"
        const val BUNDLE_IN_USE_DATA = "BUNDLE_IN_USE_DATA"
    }

    object Type {
        const val TYPE_ALL = 0
        const val TYPE_XDR = 1
        const val TYPE_PK = 2
    }

    private var binding: LayoutCopyClipboardBinding

    private val type: Int // Clipboard data type.
    private val checkInUseData: Boolean // Check used external text.
    private var isFirstLoad = true // Load Clipboard data without any delay by first.

    var data: String? = null
    var inUseData: String? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClipboardView)
        checkInUseData = typedArray.getBoolean(R.styleable.ClipboardView_checkInUse, true)
        type = typedArray.getInt(R.styleable.ClipboardView_clipDataType, TYPE_ALL)
        typedArray.recycle()
        isVisible = false
        binding = LayoutCopyClipboardBinding.inflate(LayoutInflater.from(context), this, true)
    }


    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        getClipboardData(
            if (isFirstLoad) {
                isFirstLoad = false
                0
            } else {
                500
            }
        )
    }

    fun getClipboardData(delay: Long = 0L) {
        postDelayed({
            try {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = clipboard.primaryClip?.getItemAt(0)
                val text = clipData?.text?.toString()

                if (checkInUseData) {
                    if (!inUseData.isNullOrEmpty() && inUseData == text?.trim()) {
                        isVisible = false
                        return@postDelayed
                    }
                }

                checkReceivedData(text)
            } catch (e: Exception) {
                e.printStackTrace()
                changeState(null, false)
            }
        }, delay)
    }

    fun setClickListener(listener: (text: String) -> Unit) {
        setSafeOnClickListener {
            inUseData = data
            data?.let { listener(it) }
            isVisible = false
        }
    }

    fun updateInUseData(data: String?) {
        if (checkInUseData) {
            inUseData = data
        }
    }

    private fun checkReceivedData(text: String?) {
        if (text.isNullOrEmpty()) {
            changeState(null, false)
            return
        }

        when (type) {
            TYPE_ALL -> {
                changeState(text)
            }
            TYPE_XDR -> {
                stellarRepo.createTransaction(text.trim())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        changeState(text)
                    }, {
                        changeState(null, false)
                    })
            }
            TYPE_PK -> {
                if (AppUtil.isValidAccount(text.trim())) {
                    changeState(text.trim())
                } else {
                    changeState(null, false)
                }
            }
        }
    }

    private fun changeState(text: String?, visible: Boolean = true) {
        data = text?.trim()
        isVisible = visible
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return Bundle().apply {
            putParcelable(BUNDLE_STATE, super.onSaveInstanceState())
            putString(BUNDLE_DATA, data)
            putString(BUNDLE_IN_USE_DATA, inUseData)
            putBoolean(BUNDLE_IS_FIRST_LOAD, isFirstLoad)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var newState = state
        if (state is Bundle) {
            data = state.getString(BUNDLE_DATA)
            inUseData = state.getString(BUNDLE_IN_USE_DATA)
            isFirstLoad = state.getBoolean(BUNDLE_IS_FIRST_LOAD, false)
            newState = state.parcelable(BUNDLE_STATE)
        }
        super.onRestoreInstanceState(newState)
    }
}