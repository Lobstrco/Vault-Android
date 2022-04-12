package com.lobstr.stellar.vault.presentation.auth.mnemonic

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.MnemonicItemContainerBinding
import com.lobstr.stellar.vault.presentation.auth.mnemonic.MnemonicsContainerView.Extra.EXTRA_ITEM_POSITION
import com.lobstr.stellar.vault.presentation.auth.mnemonic.MnemonicsContainerView.Extra.EXTRA_ITEM_VALUE
import com.lobstr.stellar.vault.presentation.auth.mnemonic.MnemonicsContainerView.Extra.EXTRA_PARENT_ID
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import java.lang.ref.WeakReference


class MnemonicsContainerView(context: Context?, attrs: AttributeSet?) : ScrollView(context, attrs),
    View.OnDragListener {

    object Extra {
        const val EXTRA_PARENT_ID = "EXTRA_PARENT_ID"
        const val EXTRA_ITEM_POSITION = "EXTRA_ITEM_POSITION"
        const val EXTRA_ITEM_VALUE = "EXTRA_ITEM_VALUE"
    }

    private var binding: MnemonicItemContainerBinding

    private var isCounterEnabled: Boolean

    private var isDraggable: Boolean

    private var itemBackground: Drawable?

    private var itemEmptyBackground: Drawable?

    private var itemCounterTextColor: Int

    private var itemTextColor: Int

    private var itemMarginStart: Float
    private var itemMarginEnd: Float
    private var itemMarginTop: Float
    private var itemMarginBottom: Float

    private val mnemonicsColumnCount: Int

    private val mnemonicsOuterMargin: Float

    var mMnemonicList: List<MnemonicItem>? = null

    private var mMnemonicItemActionListener: WeakReference<MnemonicItemActionListener>? = null

    init {
        val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.MnemonicsContainerView)

        isCounterEnabled =
            typedArray.getBoolean(R.styleable.MnemonicsContainerView_isCounterEnabled, false)
        isDraggable = typedArray.getBoolean(R.styleable.MnemonicsContainerView_isDraggable, false)
        itemBackground = typedArray.getDrawable(R.styleable.MnemonicsContainerView_itemBackground)
        itemEmptyBackground =
            typedArray.getDrawable(R.styleable.MnemonicsContainerView_itemEmptyBackground)
        itemCounterTextColor = typedArray.getColor(
            R.styleable.MnemonicsContainerView_itemCounterTextColor,
            ContextCompat.getColor(context, R.color.color_primary)
        )
        itemTextColor = typedArray.getColor(
            R.styleable.MnemonicsContainerView_itemTextColor,
            ContextCompat.getColor(context, R.color.color_primary)
        )
        mnemonicsColumnCount = typedArray.getInt(R.styleable.MnemonicsContainerView_mnemonicsColumnCount, 4)
        itemMarginStart = typedArray.getDimension(R.styleable.MnemonicsContainerView_itemMarginStart, 4f)
        itemMarginEnd = typedArray.getDimension(R.styleable.MnemonicsContainerView_itemMarginEnd, 4f)
        itemMarginTop = typedArray.getDimension(R.styleable.MnemonicsContainerView_itemMarginTop, 6f)
        itemMarginBottom = typedArray.getDimension(R.styleable.MnemonicsContainerView_itemMarginBottom, 6f)
        mnemonicsOuterMargin = typedArray.getDimension(R.styleable.MnemonicsContainerView_mnemonicsOuterMargin, 16f)

        typedArray.recycle()

        binding = MnemonicItemContainerBinding.inflate(LayoutInflater.from(context), this, true)

        setOnDragListener(this)
    }

    fun setupMnemonics() {
        if (mMnemonicList != null) {
            binding.glMnemonicItemContainer.removeAllViews()
            binding.glMnemonicItemContainer.columnCount = mnemonicsColumnCount
            for (i in mMnemonicList!!.indices) {
                val mnemonicItem = mMnemonicList!![i]
                val itemMnemonic = inflate(context, R.layout.item_mnemonic, null)

                val itemWidth: Float = calculateItemWidth(binding.glMnemonicItemContainer, mnemonicsColumnCount)

                itemMnemonic.background =
                    if (mnemonicItem.hide) itemEmptyBackground?.constantState?.newDrawable()
                    else itemBackground?.constantState?.newDrawable()

                val number = itemMnemonic!!.findViewById<TextView>(R.id.tvMnemonicNumber)
                val word = itemMnemonic.findViewById<TextView>(R.id.tvMnemonicWord)

                word.setTextColor(itemTextColor)

                val mnemonicStr = mnemonicItem.value

                if (isCounterEnabled) {
                    number.setTextColor(itemCounterTextColor)
                    number.isVisible = true
                    number.text = (i + 1).toString()
                }

                word.text = mnemonicStr

                word.visibility = if (mnemonicItem.hide) View.INVISIBLE else View.VISIBLE

                val layoutParams = GridLayout.LayoutParams()
                layoutParams.width = AppUtil.convertDpToPixels(context, itemWidth)

                layoutParams.setMargins(
                    AppUtil.convertDpToPixels(context, itemMarginStart),
                    AppUtil.convertDpToPixels(context, itemMarginTop),
                    AppUtil.convertDpToPixels(context, itemMarginEnd),
                    AppUtil.convertDpToPixels(context, itemMarginBottom)
                )

                binding.glMnemonicItemContainer.addView(itemMnemonic, i, layoutParams)

                if (!mnemonicItem.hide) {
                    itemMnemonic.setSafeOnClickListener {
                        mMnemonicItemActionListener?.get()
                            ?.onMnemonicItemClick(this, i, mnemonicStr)
                    }
                }

                if (isDraggable && !mnemonicItem.hide) {
                    itemMnemonic.setOnLongClickListener {
                        val intent = Intent()
                        intent.putExtra(EXTRA_PARENT_ID, this.id)
                        intent.putExtra(
                            EXTRA_ITEM_POSITION,
                            binding.glMnemonicItemContainer.indexOfChild(itemMnemonic)
                        )
                        intent.putExtra(EXTRA_ITEM_VALUE, mnemonicStr)

                        val data = ClipData.newIntent("", intent)
                        val shadowBuilder = DragShadowBuilder(it)

                        ViewCompat.startDragAndDrop(it, data, shadowBuilder, null, 0)
                    }
                }
            }
        }
    }

    private fun calculateItemWidth(container: ViewGroup, itemsCount:Int): Float {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val containerSize = dpWidth - (AppUtil.convertPixelsToDp(context, container.marginStart) + AppUtil.convertPixelsToDp(context, container.marginEnd))
        val itemWidthWithMargins = (containerSize - mnemonicsOuterMargin * 2) / itemsCount

        return itemWidthWithMargins - (itemMarginStart + itemMarginEnd)
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
                        ?.onMnemonicItemDrag(
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
        fun onMnemonicItemDrag(@IdRes from: Int, position: Int, value: String)
    }
}