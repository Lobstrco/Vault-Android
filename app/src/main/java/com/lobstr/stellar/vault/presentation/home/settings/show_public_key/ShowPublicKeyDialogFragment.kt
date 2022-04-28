package com.lobstr.stellar.vault.presentation.home.settings.show_public_key

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentShowPublicKeyBinding
import com.lobstr.stellar.vault.presentation.BaseBottomSheetDialog
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import net.glxn.qrgen.android.QRCode
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class ShowPublicKeyDialogFragment : BaseBottomSheetDialog(), ShowPublicKeyView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ShowPublicKeyDialogFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentShowPublicKeyBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<ShowPublicKeyPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter {
        presenterProvider.get().apply {
            publicKey = arguments?.getString(Constant.Bundle.BUNDLE_PUBLIC_KEY)!!
        }
    }

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
        _binding = FragmentShowPublicKeyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        binding.btnCopyKey.setSafeOnClickListener {
            AppUtil.closeKeyboard(activity)
            mPresenter.copyPublicKeyClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupTitle(title: String) {
        binding.tvTitle.text = title
    }

    override fun setupPublicKey(publicKey: String) {
        val qrCodeImage = QRCode.from(publicKey).withColor(
            ContextCompat.getColor(requireContext(), R.color.color_primary),
            ContextCompat.getColor(requireContext(), android.R.color.transparent)
        ).bitmap()

        binding.ivUserPublicKeyQrCode.setImageBitmap(qrCodeImage)

        binding.tvPublicKey.text = AppUtil.ellipsizeStrInMiddle(publicKey, PK_TRUNCATE_COUNT)
    }

    override fun copyToClipBoard(text: String) {
        dismiss()
        AppUtil.copyToClipboard(context, text)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}