package com.lobstr.stellar.vault.presentation.vault_auth.signer_info


import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.faq.FaqFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.vault_auth.recheck_signer.RecheckSignerFragment
import kotlinx.android.synthetic.main.fragment_signer_info.*
import net.glxn.qrgen.android.QRCode

class SignerInfoFragment : BaseFragment(),
    SignerInfoView, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = SignerInfoFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: SignerInfoPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideSignerInfoPresenter() = SignerInfoPresenter()

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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_signer_info, container, false) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        btnCopyUserPk.setOnClickListener(this)
        btnNext.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.signer_info, menu)
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
        when (v!!.id) {

            R.id.btnCopyUserPk -> mPresenter.copyUserPublicKey(tvUserPublicKey.text.toString())

            R.id.btnNext -> mPresenter.btnNextClicked()
        }
    }

    override fun setupUserPublicKey(userPublicKey: String?) {
        val qrCodeImage = QRCode.from(userPublicKey).withColor(
            ContextCompat.getColor(context!!, R.color.color_primary),
            ContextCompat.getColor(context!!, android.R.color.transparent)
        ).bitmap()

        ivUserPublicKeyQrCode.setImageBitmap(qrCodeImage)

        tvUserPublicKey.text = userPublicKey
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text)
    }

    override fun showRecheckSingerScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            parentFragment!!.childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, RecheckSignerFragment::class.qualifiedName!!),
            R.id.fl_container
        )
    }

    override fun showHelpScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            parentFragment!!.childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, FaqFragment::class.qualifiedName!!),
            R.id.fl_container
        )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
