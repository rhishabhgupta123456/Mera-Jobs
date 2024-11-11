package tech.merajobs.fragment.authScreens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import tech.merajobs.activity.WelcomeActivity
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.R
import tech.merajobs.activity.CreateCVActivity
import tech.merajobs.activity.EmployerActivity
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.databinding.FragmentSignInBinding
import tech.merajobs.networkModel.authNetworkPannel.AuthViewModel
import tech.merajobs.utility.SessionManager
import tech.merajobs.utility.ValidationData


class SignInFragment : BaseFragment(), View.OnClickListener {


    private lateinit var binding: FragmentSignInBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager
    private var eye = false
    var userType = AppConstant.JOB_SEEKER

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            FragmentSignInBinding.inflate(LayoutInflater.from(requireActivity()), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.openChooseSignInMethod)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)



        setUserTypeSpinner()

        binding.btBack.setOnClickListener(this)
        binding.btOpenSignUpScreen.setOnClickListener(this)
        binding.btOpenForgetPasswordScren.setOnClickListener(this)
        binding.btLogin.setOnClickListener(this)
        binding.Eye.setOnClickListener(this)
    }

    private fun setUserTypeSpinner() {
        val spinnerList = mutableListOf(AppConstant.JOB_SEEKER_LABEL)
        spinnerList.add(AppConstant.EMPLOYER_LABEL)


        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerList
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.userTypeSpinenr.adapter = adapter
        binding.userTypeSpinenr.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {

                    userType = if (spinnerList[position] == AppConstant.JOB_SEEKER_LABEL) {
                        AppConstant.JOB_SEEKER
                    } else {
                        AppConstant.EMPLOYER
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }


    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onClick(view: View?) {

        when (view!!.id) {

            R.id.btBack -> {
                findNavController().navigate(R.id.openChooseSignInMethod)
            }

            R.id.btOpenSignUpScreen -> {
                sessionManager.setSourceFragment(AppConstant.SIGN_IN_SCREEN)
                findNavController().navigate(R.id.openSignUpScreen)
            }

            R.id.btOpenForgetPasswordScren -> {
                findNavController().navigate(R.id.openResetPasswordScreen)
            }

            R.id.btLogin -> {
                if (checkValidation()) {
                    login()
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

        }

    }

    // This function is used for check validation
    private fun checkValidation(): Boolean {
        if (binding.etPhone.text.isEmpty()) {
            alertErrorDialog(getString(R.string.fill_phone))
            binding.etPhone.requestFocus()
            return false
        } else if (!ValidationData.isPhoneNumber(binding.etPhone.text.toString())) {
            alertErrorDialog(getString(R.string.fill_valid_phone))
            binding.etPhone.requestFocus()
            return false
        } else if (binding.etPassword.text.isEmpty()) {
            alertErrorDialog(getString(R.string.fill_password))
            binding.etPassword.requestFocus()
            return false
        } else if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
            return false
        } else {
            return true
        }
    }

    // This function is used for User Login
    private fun login() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            authViewModel.login(
                binding.etPhone.text.toString(),
                binding.etPassword.text.toString(),
                userType
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        val data = checkFieldObject(jsonObjectData["data"].asJsonObject)

                        if (data != null) {
                            sessionManager.setUserType(userType)
                            sessionManager.setBearerToken(checkFieldSting(data["access_token"]))
                            sessionManager.setUserID(checkFieldSting(data["id"]).toInt())
                            Log.e("Token", sessionManager.getBearerToken())

                            if (userType == AppConstant.JOB_SEEKER) {
                                if (checkFieldSting(data["profile_complete_percent"]).toInt() < 50) {
                                    val intent =
                                        Intent(requireActivity(), CreateCVActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()

                                } else {
                                    val intent =
                                        Intent(requireActivity(), FindJobActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                            } else {
                                val intent = Intent(requireActivity(), EmployerActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }

                        }
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }

        }
    }

}