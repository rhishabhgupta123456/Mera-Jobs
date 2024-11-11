package tech.merajobs.fragment.authScreens

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import tech.merajobs.databinding.FragmentSignUpBinding
import tech.merajobs.networkModel.authNetworkPannel.AuthViewModel
import tech.merajobs.utility.SessionManager
import tech.merajobs.utility.ValidationData


class SignUpFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    var userType : String = AppConstant.JOB_SEEKER

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding =
            FragmentSignUpBinding.inflate(LayoutInflater.from(requireActivity()), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    if (sessionManager.getSourceFragment() == AppConstant.CHOOSE_SIGN_IN_FRAGMENT) {
                        findNavController().navigate(R.id.openChooseSignInMethod)
                    } else if (sessionManager.getSourceFragment() == AppConstant.SIGN_IN_SCREEN) {
                        findNavController().navigate(R.id.openSignInScreen)
                    } else {
                        Toast.makeText(requireContext(), "Path Not Found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)



        binding.btBack.setOnClickListener(this)
        binding.btOpenSignInScreen.setOnClickListener(this)
        binding.btOpenVerificationScreen.setOnClickListener(this)

        binding.companyNameTitleBox.visibility = View.GONE
        binding.etCompanyNameBox.visibility = View.GONE

        try {
            binding.etFullName.setText(requireArguments().getString(AppConstant.NAME))
            binding.etPhone.setText(requireArguments().getString(AppConstant.MOBILE))
            binding.etCompanyName.setText(requireArguments().getString(AppConstant.COMPANY_NAME))
            userType = requireArguments().getString(AppConstant.USER_TYPE).toString()
        }catch (e :Exception){
            Log.e("Exception",e.toString())
        }

        privacyAndPolicyButtonClickable()
        setUserTypeSpinner()
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.btBack ->{
                if (sessionManager.getSourceFragment() == AppConstant.CHOOSE_SIGN_IN_FRAGMENT) {
                    findNavController().navigate(R.id.openChooseSignInMethod)
                } else if (sessionManager.getSourceFragment() == AppConstant.SIGN_IN_SCREEN) {
                    findNavController().navigate(R.id.openSignInScreen)
                } else {
                    Toast.makeText(requireContext(), "Path Not Found", Toast.LENGTH_SHORT)
                        .show()
                }

            }

            R.id.btOpenSignInScreen -> {
                findNavController().navigate(R.id.openSignInScreen)
            }

            R.id.btOpenVerificationScreen -> {
                if (checkValidation()) {
                    if (userType == AppConstant.JOB_SEEKER){
                        registerUserAccount()
                    }else{
                        employerRegister()
                    }
                }
            }
        }

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
                        binding.companyNameTitleBox.visibility = View.GONE
                        binding.etCompanyNameBox.visibility = View.GONE
                    } else {
                        userType = AppConstant.EMPLOYER
                        binding.companyNameTitleBox.visibility = View.VISIBLE
                        binding.etCompanyNameBox.visibility = View.VISIBLE
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
                binding.companyNameTitleBox.visibility = View.GONE
                binding.etCompanyNameBox.visibility = View.GONE
            } else {
                userType = AppConstant.EMPLOYER
                binding.companyNameTitleBox.visibility = View.VISIBLE
                binding.etCompanyNameBox.visibility = View.VISIBLE
            }
        }



    }

    private fun checkValidation(): Boolean {
        if (binding.etFullName.text.isEmpty()) {
            alertErrorDialog(getString(R.string.fill_name))
            binding.etFullName.requestFocus()
            return false
        } else if (binding.etPhone.text.isEmpty()) {
            alertErrorDialog(getString(R.string.fill_email))
            binding.etPhone.requestFocus()
            return false
        } else if (!ValidationData.isPhoneNumber(binding.etPhone.text.toString())) {
            alertErrorDialog(getString(R.string.fill_valid_phone))
            binding.etPhone.requestFocus()
            return false
        } else if (!binding.checkPolicy.isChecked) {
            alertErrorDialog(getString(R.string.please_checked_privacy_and_policy))
            return false
        } else if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
            return false
        }else{
            return true
        }
    }

    private fun registerUserAccount() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            authViewModel.register(
                binding.etFullName.text.toString(),
                binding.etPhone.text.toString(),
                AppConstant.JOB_SEEKER
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        val bundle = Bundle()
                        bundle.putString(AppConstant.NAME, binding.etFullName.text.toString())
                        bundle.putString(AppConstant.MOBILE, binding.etPhone.text.toString())
                        bundle.putString(AppConstant.COMPANY_NAME, binding.etCompanyName.text.toString())
                        bundle.putString(AppConstant.USER_TYPE, userType)
                        findNavController().navigate(R.id.openVerificationScreen, bundle)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }
            }

        }
    }

    private fun employerRegister() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            authViewModel.employerRegister(
                binding.etFullName.text.toString(),
                binding.etPhone.text.toString(),
                binding.etCompanyName.text.toString(),
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        val bundle = Bundle()
                        bundle.putString(AppConstant.NAME, binding.etFullName.text.toString())
                        bundle.putString(AppConstant.MOBILE, binding.etPhone.text.toString())
                        bundle.putString(AppConstant.COMPANY_NAME, binding.etCompanyName.text.toString())
                        bundle.putString(AppConstant.USER_TYPE, userType)
                        findNavController().navigate(R.id.openVerificationScreen, bundle)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }
            }

        }
    }

    // This function is used for Privacy Policy Button Clickable
    private fun privacyAndPolicyButtonClickable() {
        val spannedString = SpannableString(getString(R.string.privacy_policy_line))

        val pageStart = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val bundle = Bundle()
                bundle.putString(AppConstant.NAME, binding.etFullName.text.toString())
                bundle.putString(AppConstant.MOBILE, binding.etPhone.text.toString())
                findNavController().navigate(R.id.openPrivacyAndPolicyScreen,bundle)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireContext(), R.color.mainTextColor)
                ds.isUnderlineText = false
            }

        }


        val pageStartIndex = spannedString.toString().indexOf(getString(R.string.privacy_policy))
        val pageEndIndex = pageStartIndex + getString(R.string.privacy_policy).length - 1

        spannedString.setSpan(
            pageStart,
            pageStartIndex,
            pageEndIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.checkPolicy.text = spannedString
        binding.checkPolicy.movementMethod = LinkMovementMethod.getInstance()
    }



}