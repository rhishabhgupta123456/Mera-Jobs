package tech.merajobs.fragment.authScreens

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import tech.merajobs.utility.BaseFragment
import tech.merajobs.R
import tech.merajobs.databinding.FragmentCreateNewPasswordBinding
import tech.merajobs.networkModel.authNetworkPannel.AuthViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.SessionManager
import tech.merajobs.utility.ValidationData


class CreateNewPasswordFragment : BaseFragment(), View.OnClickListener {


    lateinit var binding: FragmentCreateNewPasswordBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager
    private var confirmEye = false
    private var eye = false
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCreateNewPasswordBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val bundle = Bundle()
                    bundle.putString(
                        AppConstant.MOBILE,
                        requireArguments().getString(AppConstant.MOBILE)
                    )
                    bundle.putString(
                        AppConstant.USER_TYPE,
                        requireArguments().getString(AppConstant.USER_TYPE)
                    )
                    findNavController().navigate(R.id.openResetPasswordScreen, bundle)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.tvPhone.text = requireArguments().getString(AppConstant.MOBILE)

        binding.btBack.setOnClickListener(this)
        binding.btPasswordCreate.setOnClickListener(this)
        binding.confirmEye.setOnClickListener(this)
        binding.Eye.setOnClickListener(this)
        binding.btResendOTP.setOnClickListener(this)
        startCountDownTimer()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btBack -> {
                val bundle = Bundle()
                bundle.putString(
                    AppConstant.MOBILE,
                    requireArguments().getString(AppConstant.MOBILE)
                )
                bundle.putString(
                    AppConstant.USER_TYPE,
                    requireArguments().getString(AppConstant.USER_TYPE)
                )
                findNavController().navigate(R.id.openResetPasswordScreen, bundle)
            }

            R.id.btPasswordCreate -> {
                if (checkValidation()) {
                    resetPassword()
                }
            }

            R.id.Eye -> {
                if (eye) {
                    eye = false
                    binding.Eye.setImageResource(R.drawable.hidepass_icon)
                    binding.etPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                } else {
                    eye = true
                    binding.Eye.setImageResource(R.drawable.showpass_icon)
                    binding.etPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                }

            }

            R.id.btResendOTP -> {
                if (binding.btResendOTP.text == getString(R.string.resend)) {
                    if (!isNetworkAvailable()) {
                        alertErrorDialog(getString(R.string.no_internet))
                    } else {
                        forgetPassword()
                    }

                }
            }

            R.id.confirmEye -> {
                if (confirmEye) {
                    confirmEye = false
                    binding.confirmEye.setImageResource(R.drawable.hidepass_icon)
                    binding.etConfirmPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()

                } else {
                    confirmEye = true
                    binding.confirmEye.setImageResource(R.drawable.showpass_icon)
                    binding.etConfirmPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                }

            }


        }
    }

    private fun forgetPassword() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            authViewModel.forgetPassword(
                binding.tvPhone.text.toString(),
                requireArguments().getString(AppConstant.USER_TYPE).toString()
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        startCountDownTimer()
                        Toast.makeText(
                            requireContext(),
                            "Otp Send Successfully !",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }
            }

        }
    }


    private fun resetPassword() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            authViewModel.resetPassword(
                binding.tvPhone.text.toString(),
                binding.etOTP.text.toString(),
                binding.etPassword.text.toString(),
                binding.etConfirmPassword.text.toString(),
                requireArguments().getString(AppConstant.USER_TYPE).toString()
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        passwordChangeAlertBox()
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }
            }

        }
    }

    // This function is used for open change password alert box
    private fun passwordChangeAlertBox() {
        val postDialog = Dialog(requireContext())
        postDialog.setContentView(R.layout.alert_dialog_password_change)
        postDialog.setCancelable(true)

        val submitBtn: TextView = postDialog.findViewById(R.id.pass_btn_okay)

        submitBtn.setOnClickListener {
            postDialog.dismiss()
            findNavController().navigate(R.id.openSignInScreen)
        }
        postDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        postDialog.show()

    }

    // This function is use for check validation in all screen field
    private fun checkValidation(): Boolean {
        if (binding.etOTP.text.toString().isEmpty()) {
            alertErrorDialog(getString(R.string.fill_otp))
            binding.etOTP.requestFocus()
            return false
        } else if (binding.etOTP.text.toString().length < 6) {
            alertErrorDialog(getString(R.string.fill_complete_otp))
            binding.etOTP.requestFocus()
            return false
        } else if (binding.etPassword.text.isEmpty()) {
            alertErrorDialog(getString(R.string.fill_password))
            binding.etPassword.requestFocus()
            return false
        } else if (!ValidationData.passCheck(binding.etPassword.text.toString())) {
            alertErrorDialog(getString(R.string.password_validation_text))
            return false
        } else if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
            alertErrorDialog(getString(R.string.confirm_password_validation_text))
            return false
        } else if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
            return false
        } else {
            return true
        }
    }


    private fun startCountDownTimer() {
        binding.btResendOTP.isEnabled = false
        binding.btResendOTP.setTextColor(resources.getColor(R.color.RedTextColor))
        countDownTimer = object : CountDownTimer(120000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val f = android.icu.text.DecimalFormat("00")
                val min = (millisUntilFinished / 60000) % 60
                val sec = (millisUntilFinished / 1000) % 60
                binding.btResendOTP.text = "${f.format(min)}:${f.format(sec)}"
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.btResendOTP.isEnabled = true
                binding.btResendOTP.setTextColor(resources.getColor(R.color.mainColor))
                binding.btResendOTP.text = getString(R.string.resend)
            }
        }
        countDownTimer?.start()
    }

}