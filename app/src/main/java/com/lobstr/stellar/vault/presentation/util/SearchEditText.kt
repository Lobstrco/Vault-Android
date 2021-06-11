package com.lobstr.stellar.vault.presentation.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.lobstr.stellar.vault.R

class SearchEditText(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs), TextWatcher {

    private var listener: OnSearchEditTextListener? = null

    private var cleanSearchImgDrawable: Drawable?

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchEditText)
        val cleanSearchImgColor = typedArray.getColor(
                R.styleable.SearchEditText_cleanSearchImgColor,
                -1
        )

        cleanSearchImgDrawable = typedArray.getDrawable(R.styleable.SearchEditText_cleanSearchImg)
            ?: ContextCompat.getDrawable(context, R.drawable.ic_clean_search)

        if (cleanSearchImgColor != -1) {
            // Set clean img color.
            cleanSearchImgDrawable?.let {
                val cleanSearchImgDrawableWrapped = DrawableCompat.wrap(it).mutate()
                DrawableCompat.setTint(cleanSearchImgDrawableWrapped, cleanSearchImgColor)
                cleanSearchImgDrawable = cleanSearchImgDrawableWrapped
            }
        }

        typedArray.recycle()
        setListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        addTextChangedListener(this)
        setOnTouchListener { v, event ->
            val drawableRightPosition = 2
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (compoundDrawables[drawableRightPosition] != null &&
                        event.rawX >= (v.right - AppUtil.convertDpToPixels(context!!, 50f))) {
                    setText("")
                    listener?.onClearClicked()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // implement logic if needed.
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        listener?.onTextChanged(s.toString())
        if (!s.isNullOrEmpty()) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, cleanSearchImgDrawable, null)
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    override fun afterTextChanged(s: Editable?) {
        // implement logic if needed.
    }

    fun setSearchListener(listener: OnSearchEditTextListener) {
        this.listener = listener
    }

    /**
     * Ignore TextChangedListener.
     */
    fun setInitialText(text: String) {
        removeTextChangedListener(this)
        setText(text)
        addTextChangedListener(this)
    }

    fun removeTextChangedListener() {
        removeTextChangedListener(this)
    }

    fun clearSearch() {
        setText("")
        listener?.onClearClicked()
    }

    interface OnSearchEditTextListener {
        fun onTextChanged(query: String)
        fun onClearClicked()
    }
}