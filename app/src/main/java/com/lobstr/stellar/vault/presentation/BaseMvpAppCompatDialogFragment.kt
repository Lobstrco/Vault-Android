package com.lobstr.stellar.vault.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.arellomobile.mvp.MvpDelegate


open class BaseMvpAppCompatDialogFragment : AppCompatDialogFragment() {

    private var mIsStateSaved: Boolean = false
    private var mMvpDelegate: MvpDelegate<out BaseMvpAppCompatDialogFragment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.getMvpDelegate().onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        this.mIsStateSaved = false
        this.getMvpDelegate().onAttach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        this.mIsStateSaved = true
        this.getMvpDelegate().onSaveInstanceState(outState)
        this.getMvpDelegate().onDetach()
    }

    override fun onStop() {
        super.onStop()
        this.getMvpDelegate().onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.getMvpDelegate().onDetach()
        this.getMvpDelegate().onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this.activity!!.isFinishing()) {
            this.getMvpDelegate().onDestroy()
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
                this.getMvpDelegate().onDestroy()
            }

        }
    }

    fun getMvpDelegate(): MvpDelegate<*> {
        if (this.mMvpDelegate == null) {
            this.mMvpDelegate = MvpDelegate(this)
        }

        return this.mMvpDelegate!!
    }
}