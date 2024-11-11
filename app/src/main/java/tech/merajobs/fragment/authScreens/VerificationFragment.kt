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
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.R
import tech.merajobs.databinding.FragmentVerificationBinding
import tech.merajobs.networkModel.authNetworkPannel.AuthViewModel
import tech.merajobs.utility.SessionManager
import tech.merajobs.utility.ValidationData


class VerificationFragment : BaseFragment(), View.OnClickListener {


    private lateinit var binding: FragmentVerificationBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager
    private var confirmEye = false
    private var eye = false
    private var userType : String? = null

    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentVerificationBinding.inflate(
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
                    bundle.putString(AppConstant.NAME, requireArguments().getString(AppConstant.NAME))
                    bundle.putString(AppConstant.MOBILE,requireArguments().getString(AppConstant.MOBILE))
                    bundle.putString(AppConstant.USER_TYPE,requireArguments().getString(AppConstant.USER_TYPE))
                    bundle.putString(AppConstant.COMPANY_NAME,requireArguments().getString(AppConstant.COMPANY_NAME))
                    findNavController().navigate(R.id.openSignUpScreen,bundle)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)



        binding.tvName.text = requireArguments().getString(AppConstant.NAME)
        binding.tvPhone.text = requireArguments().getString(AppConstant.MOBILE)

        if (requireArguments().getString(AppConstant.USER_TYPE) == AppConstant.JOB_SEEKER)
        {
            userType = AppConstant.JOB_SEEKER
            binding.companyNameTitleBox.visibility = View.GONE
            binding.etCompanyNameBox.visibility = View.GONE
        }else{
            userType = AppConstant.EMPLOYER
            binding.companyNameTitleBox.visibility = View.VISIBLE
            binding.etCompanyNameBox.visibility = View.VISIBLE
            binding.tvCompanyName.text = requireArguments().getString(AppConstant.COMPANY_NAME)
        }

        binding.btBack.setOnClickListener(this)
        binding.btVerify.setOnClickListener(this)
        binding.confirmEye.setOnClickListener(this)
        binding.Eye.setOnClickListener(this)
        binding.btResendOTP.setOnClickListener(this)

        attachCounter()

        countDownTimer?.start()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btVerify -> {
                if (checkValidation()) {
                    otpVerification()
                }
            }

            R.id.btBack -> {
                val bundle = Bundle()
                bundle.putString(AppConstant.NAME, requireArguments().getString(AppConstant.NAME))
                bundle.putString(AppConstant.MOBILE,requireArguments().getString(AppConstant.MOBILE))
                bundle.putString(AppConstant.USER_TYPE,requireArguments().getString(AppConstant.USER_TYPE))
                bundle.putString(AppConstant.COMPANY_NAME,requireArguments().getString(AppConstant.COMPANY_NAME))
                findNavController().navigate(R.id.openSignUpScreen,bundle)
            }

            R.id.btResendOTP ->{
                if (binding.btResendOTP.text == getString(R.string.resend)){
                    if (!isNetworkAvailable()) {
                        alertErrorDialog(getString(R.string.no_internet))
                    }else{
                        registerUserAccount()
                    }
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
        }  else if (binding.etPassword.text.isEmpty()) {
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

    private fun otpVerification() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            authViewModel.otpVerification(
                binding.etOTP.text.toString(),
                binding.tvPhone.text.toString(),
                binding.etPassword.text.toString(),
                binding.etConfirmPassword.text.toString(),
                userType!!
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        userRegisterSuccessFullAlertBox()
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }
            }

        }

    }

    private fun userRegisterSuccessFullAlertBox() {
        val postDialog = Dialog(requireContext())
        postDialog.setContentView(R.layout.alert_dialog_successful_sign_up)
        postDialog.setCancelable(true)

        val submit: TextView = postDialog.findViewById(R.id.btn_okay)

        submit.setOnClickListener {
            postDialog.dismiss()
            findNavController().navigate(R.id.openSignInScreen)
        }

        postDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        postDialog.show()
    }

    private fun registerUserAccount() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            authViewModel.register(
                binding.tvName.text.toString(),
                binding.tvPhone.text.toString(),
                userType!!
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        countDownTimer?.start()
                        Toast.makeText(requireContext(), "Otp Send Successfully !", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }
            }

        }
    }


    private fun attachCounter() {
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
    }

}