package tech.merajobs.fragment.authScreens

import android.os.Bundle
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
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.R
import tech.merajobs.databinding.FragmentResetPasswordBinding
import tech.merajobs.networkModel.authNetworkPannel.AuthViewModel
import tech.merajobs.utility.SessionManager
import tech.merajobs.utility.ValidationData


class ResetPasswordFragment : BaseFragment() {


    private lateinit var binding: FragmentResetPasswordBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager : SessionManager
    var userType : String = AppConstant.JOB_SEEKER

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentResetPasswordBinding.inflate(
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
                    findNavController().navigate(R.id.openSignInScreen)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        try {
            binding.etPhone.setText(requireArguments().getString(AppConstant.MOBILE))
            userType = requireArguments().getString(AppConstant.USER_TYPE).toString()
        }catch (e :Exception){
            binding.etPhone.text = null

        }

        binding.btBack.setOnClickListener{
            findNavController().navigate(R.id.openSignInScreen)
        }

        binding.btOpenVerificationScreen.setOnClickListener {
            if (checkValidation()) {
                if (!isNetworkAvailable()) {
                    alertErrorDialog(getString(R.string.no_internet))
                }else{
                    forgetPassword()
                }
            }

        }

        setUserTypeSpinner()

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
                    if (spinnerList[position] == AppConstant.JOB_SEEKER_LABEL) {
                        userType = AppConstant.JOB_SEEKER
                    } else {
                        userType = AppConstant.EMPLOYER
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }


        val defaultIndex =
            spinnerList.indexOf(userType)
        if (defaultIndex != -1) {
            binding.userTypeSpinenr.setSelection(defaultIndex)
            if (spinnerList[defaultIndex] == AppConstant.JOB_SEEKER_LABEL) {
                userType = AppConstant.JOB_SEEKER
            } else {
                userType = AppConstant.EMPLOYER
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
        } else {
            return true
        }
    }

    private fun forgetPassword() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            authViewModel.forgetPassword(
                binding.etPhone.text.toString(),
                AppConstant.JOB_SEEKER
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        val bundle = Bundle()
                        bundle.putString(AppConstant.MOBILE, binding.etPhone.text.toString())
                        bundle.putString(AppConstant.USER_TYPE, userType)
                        findNavController().navigate(R.id.createPasswordScreen,bundle)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }
            }

        }
    }

}