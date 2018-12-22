package com.lobstr.stellar.vault.presentation.auth.mnemonic

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.lobstr.stellar.vault.R
import kotlinx.android.synthetic.main.mnemonic_item_container.view.*
import java.lang.ref.WeakReference

class MnemonicsContainerView(context: Context?, attrs: AttributeSet?) : ScrollView(context, attrs) {

    companion object {
        const val ITEM_MARGIN = 16
    }

    private var isCounterEnabled: Boolean = true

    var mMnemonicList: List<String>? = null

    private var mMnemonicItemClickListener: WeakReference<MnemonicItemClickListener>? = null

    init {
        val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.MnemonicsContainerView)

        if (typedArray.hasValue(R.styleable.MnemonicsContainerView_isCounterEnabled)) {
            isCounterEnabled = typedArray.getBoolean(R.styleable.MnemonicsContainerView_isCounterEnabled, true)
        }

        typedArray.recycle()

        inflate(getContext(), R.layout.mnemonic_item_container, this)
    }

    fun setupMnemonics() {
        if (mMnemonicList != null) {
            fblMnemonicItemContainer.removeAllViews()
            for (i in mMnemonicList!!.indices) {
                val itemMnemonic = (context as Activity).layoutInflater.inflate(R.layout.item_mnemonic, null)
                val number = itemMnemonic!!.findViewById<TextView>(R.id.tvMnemonicNumber)
                val word = itemMnemonic.findViewById<TextView>(R.id.tvMnemonicWord)
                val mnemonicStr = mMnemonicList!![i]
                number.visibility = if (isCounterEnabled) View.VISIBLE else View.GONE
                number.text = (i + 1).toString()
                word.text = mnemonicStr

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(
                    ITEM_MARGIN,
                    ITEM_MARGIN,
                    ITEM_MARGIN,
                    ITEM_MARGIN
                )

                itemMnemonic.setOnClickListener {
                    mMnemonicItemClickListener?.get()?.onMnemonicItemClick(this, i, mnemonicStr)
                }

                fblMnemonicItemContainer.addView(itemMnemonic, i, layoutParams)
            }
        }
    }

    fun setMnemonicItemClickListener(listener: MnemonicItemClickListener) {
        mMnemonicItemClickListener = WeakReference(listener)
    }

    interface MnemonicItemClickListener {
        fun onMnemonicItemClick(v: View, position: Int, value: String)
    }
}