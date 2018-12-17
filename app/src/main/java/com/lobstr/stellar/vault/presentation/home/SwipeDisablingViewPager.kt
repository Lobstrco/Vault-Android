package com.lobstr.stellar.vault.presentation.home

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.lobstr.stellar.vault.R


class SwipeDisablingViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private var isSwipeEnabled: Boolean = false

    init {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeDisablingViewPager)

        if (typedArray.hasValue(R.styleable.SwipeDisablingViewPager_isSwipeEnabled)) {
            isSwipeEnabled = typedArray.getBoolean(R.styleable.SwipeDisablingViewPager_isSwipeEnabled, true)
        }

        typedArray.recycle()
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return isSwipeEnabled && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return isSwipeEnabled && super.onInterceptTouchEvent(ev)
    }
}