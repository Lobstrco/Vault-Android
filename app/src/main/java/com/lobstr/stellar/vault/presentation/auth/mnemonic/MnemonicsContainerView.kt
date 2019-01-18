package com.lobstr.stellar.vault.presentation.auth.mnemonic

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.mnemonic.MnemonicsContainerView.Extra.EXTRA_ITEM_POSITION
import com.lobstr.stellar.vault.presentation.auth.mnemonic.MnemonicsContainerView.Extra.EXTRA_ITEM_VALUE
import com.lobstr.stellar.vault.presentation.auth.mnemonic.MnemonicsContainerView.Extra.EXTRA_PARENT_ID
import kotlinx.android.synthetic.main.mnemonic_item_container.view.*
import java.lang.ref.WeakReference


class MnemonicsContainerView(context: Context?, attrs: AttributeSet?) : ScrollView(context, attrs),
    View.OnDragListener {

    companion object {
        const val ITEM_MARGIN = 16
    }

    object Extra {
        const val EXTRA_PARENT_ID = "EXTRA_PARENT_ID"
        const val EXTRA_ITEM_POSITION = "EXTRA_ITEM_POSITION"
        const val EXTRA_ITEM_VALUE = "EXTRA_ITEM_VALUE"
    }

    private var isCounterEnabled: Boolean = true

    private var isDraggable: Boolean = false

    private var itemBackground: Drawable? = null

    private var itemTextColor: Int = 0

    var mMnemonicList: List<String>? = null

    private var mMnemonicItemActionListener: WeakReference<MnemonicItemActionListener>? = null

    init {
        val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.MnemonicsContainerView)

        if (typedArray.hasValue(R.styleable.MnemonicsContainerView_isCounterEnabled)) {
            isCounterEnabled = typedArray.getBoolean(R.styleable.MnemonicsContainerView_isCounterEnabled, true)
            isDraggable = typedArray.getBoolean(R.styleable.MnemonicsContainerView_isDraggable, false)
            itemBackground = typedArray.getDrawable(R.styleable.MnemonicsContainerView_itemBackground)
            itemTextColor = typedArray.getColor(
                R.styleable.MnemonicsContainerView_itemTextColor,
                ContextCompat.getColor(context, R.color.color_primary)
            )
        }

        typedArray.recycle()

        inflate(getContext(), R.layout.mnemonic_item_container, this)

        setOnDragListener(this)
    }

    fun setupMnemonics() {
        if (mMnemonicList != null) {
            fblMnemonicItemContainer.removeAllViews()
            for (i in mMnemonicList!!.indices) {
                val itemMnemonic = (context as Activity).layoutInflater.inflate(R.layout.item_mnemonic, null)
                itemMnemonic.background = itemBackground
                val number = itemMnemonic!!.findViewById<TextView>(R.id.tvMnemonicNumber)
                val word = itemMnemonic.findViewById<TextView>(R.id.tvMnemonicWord)
                word.setTextColor(itemTextColor)
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
                    mMnemonicItemActionListener?.get()?.onMnemonicItemClick(this, i, mnemonicStr)
                }

                fblMnemonicItemContainer.addView(itemMnemonic, i, layoutParams)

                if (isDraggable) {
                    itemMnemonic.setOnTouchListener(OnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_MOVE -> {
                                // pass initial data for find item mnemonic in drag event
                                val intent = Intent()
                                intent.putExtra(EXTRA_PARENT_ID, this.id)
                                intent.putExtra(
                                    EXTRA_ITEM_POSITION,
                                    fblMnemonicItemContainer.indexOfChild(itemMnemonic)
                                )
                                intent.putExtra(EXTRA_ITEM_VALUE, mnemonicStr)

                                val data = ClipData.newIntent("", intent)
                                val shadowBuilder = View.DragShadowBuilder(v)
                                v.startDrag(data, shadowBuilder, null, 0)
                                return@OnTouchListener true
                            }
                        }
                        return@OnTouchListener false
                    })
                }
            }
        }
    }

    override fun onDrag(v: View?, event: DragEvent?): Boolean {

        val clipDataIntent = event?.clipData?.getItemAt(0)?.intent

        val parentId = clipDataIntent?.getIntExtra(EXTRA_PARENT_ID, 0)
        val itemPosition = clipDataIntent?.getIntExtra(EXTRA_ITEM_POSITION, 0)
        val value = clipDataIntent?.getStringExtra(EXTRA_ITEM_VALUE)

        val handleDragEvent = parentId != id

        when (event?.action) {
            DragEvent.ACTION_DROP -> {
                if (handleDragEvent) {
                    mMnemonicItemActionListener?.get()
                        ?.onMnemonicItemDragged(
                            parentId!!,
                            itemPosition!!,
                            value!!
                        )
                }
            }
        }

        return handleDragEvent
    }

    fun setMnemonicItemActionListener(listener: MnemonicItemActionListener) {
        mMnemonicItemActionListener = WeakReference(listener)
    }

    interface MnemonicItemActionListener {
        fun onMnemonicItemClick(v: View, position: Int, value: String)
        fun onMnemonicItemDragged(@IdRes from: Int, position: Int, value: String)
    }
}