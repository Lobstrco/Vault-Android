package com.lobstr.stellar.vault.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import moxy.MvpDelegate
import moxy.MvpDelegateHolder


open class BaseMvpAppCompatFragment : Fragment(), MvpDelegateHolder {
    private var mIsStateSaved: Boolean = false
    private var mMvpDelegate: MvpDelegate<out BaseMvpAppCompatFragment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mvpDelegate.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        this.mIsStateSaved = false
        this.mvpDelegate.onAttach()
    }

    override fun onResume() {
        super.onResume()
        this.mIsStateSaved = false
        this.mvpDelegate.onAttach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        this.mIsStateSaved = true
        this.mvpDelegate.onSaveInstanceState(outState)
        this.mvpDelegate.onDetach()
    }

    override fun onStop() {
        super.onStop()
        this.mvpDelegate.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.mvpDelegate.onDetach()
        this.mvpDelegate.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this.requireActivity().isFinishing) {
            this.mvpDelegate.onDestroy()
        } else if (this.mIsStateSaved) {
            this.mIsStateSaved = false
        } else {
            var anyParentIsRemoving = false

            var parent = this.parentFragment
            while (!anyParentIsRemoving && parent != null) {
                anyParentIsRemoving = parent.isRemoving
                parent = parent.parentFragment
            }

            if (this.isRemoving || anyParentIsRemoving) {
                this.mvpDelegate.onDestroy()
            }
        }
    }

    override fun getMvpDelegate(): MvpDelegate<*> {
        if (this.mMvpDelegate == null) {
            this.mMvpDelegate = MvpDelegate(this)
        }

        return this.mMvpDelegate!!
    }
}