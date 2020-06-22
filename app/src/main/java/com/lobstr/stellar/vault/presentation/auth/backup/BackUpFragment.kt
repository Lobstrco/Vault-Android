package com.lobstr.stellar.vault.presentation.auth.backup


import android.os.Bundle
import android.view.*
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic.MnemonicsFragment
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import kotlinx.android.synthetic.main.fragment_back_up.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class BackUpFragment : BaseFragment(), BackUpView, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = BackUpFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: BackUpPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideBackUpPresenter() = BackUpPresenter()

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = if (mView == null) inflater.inflate(
            R.layout.fragment_back_up,
            container,
            false
        ) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        btnNext.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.backup, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_info -> mPresenter.infoClicked()
        }

        return super.onOptionsItemSelected(item)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v?.id) {
            btnNext.id -> mPresenter.nextClicked()
        }
    }

    override fun showCreateMnemonicsScreen() {
        val bundle = Bundle()
        bundle.putBoolean(Constant.Bundle.BUNDLE_GENERATE_MNEMONICS, true)
        val fragment = requireParentFragment().childFragmentManager.fragmentFactory.instantiate(
            requireContext().classLoader,
            MnemonicsFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showHelpScreen(articleId: Long) {
        SupportManager.showZendeskArticle(requireContext(), articleId)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
