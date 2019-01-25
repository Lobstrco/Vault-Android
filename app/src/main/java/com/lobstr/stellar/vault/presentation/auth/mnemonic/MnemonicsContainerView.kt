package com.lobstr.stellar.vault.presentation.auth.mnemonic

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.mnemonic.MnemonicsContainerView.Extra.EXTRA_ITEM_POSITION
import com.lobstr.stellar.vault.presentation.auth.mnemonic.MnemonicsContainerView.Extra.EXTRA_ITEM_VALUE
import com.lobstr.stellar.vault.presentation.auth.mnemonic.MnemonicsContainerView.Extra.EXTRA_PARENT_ID
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
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

    private var itemEmptyBackground: Drawable? = null

    private var itemTextColor: Int = 0

    var mMnemonicList: List<MnemonicItem>? = null

    private var mMnemonicItemActionListener: WeakReference<MnemonicItemActionListener>? = null

    init {
        val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.MnemonicsContainerView)

        if (typedArray.hasValue(R.styleable.MnemonicsContainerView_isCounterEnabled)) {
            isCounterEnabled = typedArray.getBoolean(R.styleable.MnemonicsContainerView_isCounterEnabled, true)
            isDraggable = typedArray.getBoolean(R.styleable.MnemonicsContainerView_isDraggable, false)
            itemBackground = typedArray.getDrawable(R.styleable.MnemonicsContainerView_itemBackground)
            itemEmptyBackground = typedArray.getDrawable(R.styleable.MnemonicsContainerView_itemEmptyBackground)
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
                val mnemonicItem = mMnemonicList!![i]
                val itemMnemonic = (context as Activity).layoutInflater.inflate(R.layout.item_mnemonic, null)
                itemMnemonic.background = if (mnemonicItem.hide) itemEmptyBackground else itemBackground

                val number = itemMnemonic!!.findViewById<TextView>(R.id.tvMnemonicNumber)
                val word = itemMnemonic.findViewById<TextView>(R.id.tvMnemonicWord)

                word.setTextColor(itemTextColor)

                val mnemonicStr = mnemonicItem.value

                if (isCounterEnabled) {
                    number.visibility = View.VISIBLE
                    number.text = (i + 1).toString()
                }

                word.text = mnemonicStr

                word.visibility = if (mnemonicItem.hide) View.INVISIBLE else View.VISIBLE

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

                fblMnemonicItemContainer.addView(itemMnemonic, i, layoutParams)

                if (!mnemonicItem.hide) {
                    itemMnemonic.setOnClickListener {
                        mMnemonicItemActionListener?.get()?.onMnemonicItemClick(this, i, mnemonicStr)
                    }
                }


                if (isDraggable && !mnemonicItem.hide) {
                    itemMnemonic.setOnLongClickListener {
                        val intent = Intent()
                        intent.putExtra(EXTRA_PARENT_ID, this.id)
                        intent.putExtra(
                            EXTRA_ITEM_POSITION,
                            fblMnemonicItemContainer.indexOfChild(itemMnemonic)
                        )
                        intent.putExtra(EXTRA_ITEM_VALUE, mnemonicStr)

                        val data = ClipData.newIntent("", intent)
                        val shadowBuilder = View.DragShadowBuilder(it)
                        it.startDrag(data, shadowBuilder, null, 0)

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            it.startDrag(data, shadowBuilder, null, 0)
                        } else {
                            it.startDragAndDrop(data, shadowBuilder, null, 0)
                        }
                    }
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