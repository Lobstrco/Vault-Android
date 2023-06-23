package com.lobstr.stellar.vault.presentation.tangem.dialog

import android.animation.Animator
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentTangemDialogBinding
import com.lobstr.stellar.vault.presentation.BaseBottomSheetDialog
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.tangem.dialog.TangemDialogPresenter.TangemOperation.NO_ACTION
import com.lobstr.stellar.vault.presentation.util.*
import com.lobstr.stellar.vault.presentation.util.VibrateType
import com.lobstr.stellar.vault.presentation.util.tangem.CustomCardManagerDelegate
import com.lobstr.stellar.vault.presentation.util.tangem.customInit
import com.tangem.TangemSdk
import com.tangem.common.CompletionResult
import com.tangem.common.card.EllipticCurve
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class TangemDialogFragment : BaseBottomSheetDialog(), TangemDialogView,
    AlertDialogFragment.OnDefaultAlertDialogListener,
    CustomCardManagerDelegate.CustomCardManagerDelegateListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentTangemDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var tangemSdk: TangemSdk

    @Inject
    lateinit var presenterProvider: Provider<TangemDialogPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter {
        presenterProvider.get().apply {
            tangemInfo = arguments?.parcelable(Constant.Extra.EXTRA_TANGEM_INFO)
            tangemOperationType = arguments?.getInt(Constant.Extra.EXTRA_TANGEM_OPERATION_TYPE)
                ?: NO_ACTION
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
        _binding = FragmentTangemDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // Set Wrap Content behavior for BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                BottomSheetBehavior.from(sheet).peekHeight = sheet.height
                sheet.parent.parent.requestLayout()
            }
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tangemSdk = TangemSdk.customInit(requireActivity(), this)
        setListeners()
    }

    override fun startScanTangemCard() {
        tangemSdk.scanCard { result ->
            when (result) {
                is CompletionResult.Success -> {
                    // Handle returned card data
                    // Switch to UI thread to show results in UI
                    this.activity?.runOnUiThread {
                        mPresenter.processDataAfterScanningCard(result.data)
                    }
                }
                is CompletionResult.Failure -> {
                    // Handle other errors
                    this.activity?.runOnUiThread {
                        mPresenter.processErrorCompletion(result.error)
                    }
                }
            }
        }
    }

    override fun startSignPendingTransaction(
        arrayHashes: Array<ByteArray>,
        walletPublicKey: ByteArray,
        cardId: String
    ) {
        tangemSdk.sign(
            hashes = arrayHashes,
            walletPublicKey = walletPublicKey,
            cardId = cardId
        ) { result ->
            when (result) {
                is CompletionResult.Success -> {
                    this.activity?.runOnUiThread {
                        mPresenter.processSignedData(result.data)
                    }
                }
                is CompletionResult.Failure -> {
                    this.activity?.runOnUiThread {
                        mPresenter.processErrorCompletion(result.error)
                    }
                }
            }
        }
    }

    override fun startCreateWallet(curve: EllipticCurve, cardId: String) {
        tangemSdk.createWallet(
            curve,
            cardId
        ) { result ->
            when (result) {
                is CompletionResult.Failure -> {
                    this.activity?.runOnUiThread {
                        mPresenter.processErrorCompletion(result.error)
                    }
                }
                is CompletionResult.Success -> this.activity?.runOnUiThread {
                    mPresenter.processDataAfterCreateWallet(result.data)
                }
            }
        }
    }

    override fun setGravity(gravity: Int) {
        val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
        )

        params.gravity = gravity
        params.bottomMargin = AppUtil.convertDpToPixels(requireActivity(), 13f).toInt()
        binding.ivCardHorizontalFr.layoutParams = params
    }

    override fun changeActionContainerVisibility(show: Boolean) {
        binding.llActionContainer.isInvisible = !show
    }

    override fun changeErrorContainerVisibility(show: Boolean) {
        binding.llErrorContainer.isInvisible = !show
    }

    override fun setErrorContainerData(errorTitle: String, errorDescription: String) {
        binding.tvErrorTitleFr.text = errorTitle
        binding.tvErrorMessageFr.text = errorDescription
    }

    override fun showSuccessAnimation() {
        binding.idNFCChipLocationContainer.isInvisible = true
        binding.idSuccessContainer.isVisible = true
        binding.ivScanStatus.playAnimation()
    }

    private fun setListeners() {
        binding.btnTryAgainFr.setSafeOnClickListener { mPresenter.tryAgainClicked() }
        binding.btnCancel.setSafeOnClickListener { mPresenter.cancelClicked() }
        binding.ivScanStatus.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                mPresenter.successAnimationFinished()
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationStart(animation: Animator) {
                mPresenter.successAnimationStarted()
            }
        })
    }

    override fun onSessionStarted() {

    }

    override fun onSessionStopped() {
        this.activity?.runOnUiThread {
            mPresenter.onTagSessionStoppedTangem()
        }
    }

    override fun onTagConnected() {
        this.activity?.runOnUiThread {
            mPresenter.onTagConnectedTangem()
        }
    }

    override fun onTagLost() {
        this.activity?.runOnUiThread {
            mPresenter.onTagLostsTangem()
        }
    }

    override fun onLostCard() {
        requireActivity().runOnUiThread {
            mPresenter.interruptionOfSignatureOperation()
        }
    }

    override fun onWrongCard() {
        this.activity?.runOnUiThread {
            mPresenter.onWrongCard()
        }
    }

    override fun onSecurityDelay(ms: Int, totalDurationSeconds: Int) {
        this.activity?.runOnUiThread {
            mPresenter.onSecurityDelayTangem(ms, totalDurationSeconds)
        }
    }

    override fun setStateTitle(state: String?) {
        binding.tvStateTitleFr.text = state
    }

    override fun setDescriptionMessage(message: String?) {
        binding.tvMessageTitleFr.text = message
    }

    override fun setTimerDescription(message: String?) {
        binding.tvTimerDescription.text = message
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun showNfcCheckDialog() {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(R.string.nfc_enable_title)
            .setMessage(getString(R.string.nfc_enable_description))
            .setNegativeBtnText(R.string.cancel_action)
            .setPositiveBtnText(R.string.ok_action)
            .create()
            .show(
                childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.NFC_INFO_DIALOG
            )
    }

    override fun showNfcDeviceSettings() {
        startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
    }

    override fun showMessage(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun successfullyCompleteOperation(tangemInfo: TangemInfo?) {
        // Notify target about success.
        ((parentFragment ?: activity) as? OnTangemDialogListener)?.success(tangemInfo)
        dismiss()
    }

    override fun cancelOperation() {
        // Notify target about cancellation.
        ((parentFragment ?: activity) as? OnTangemDialogListener)?.cancel()
    }

    override fun onPositiveBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPresenter.onAlertDialogPositiveButtonClicked(tag)
    }

    override fun onNegativeBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun onNeutralBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun onCancel(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun finishScreen() {
        cancelOperation()
        dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        // Handle dialog cancellation (back press and etc.).
        cancelOperation()
        super.onCancel(dialog)
    }

    override fun vibrate(type: VibrateType) {
        VibratorUtil.vibrate(requireContext(), type)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    interface OnTangemDialogListener {
        fun success(tangemInfo: TangemInfo?)
        fun cancel()
    }
}
