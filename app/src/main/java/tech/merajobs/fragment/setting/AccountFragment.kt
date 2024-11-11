package tech.merajobs.fragment.setting

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.EmployerActivity
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.databinding.FragmentAccountBinding
import tech.merajobs.networkModel.settingNetworkModel.SettingViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager
import tech.merajobs.utility.ValidationData

class AccountFragment : BaseFragment(), View.OnClickListener {


    lateinit var binding: FragmentAccountBinding
    lateinit var sessionManager: SessionManager
    lateinit var settingViewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAccountBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.openSettingScreen)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.btBack.setOnClickListener(this)
        binding.btSavePhone.setOnClickListener(this)
        binding.btSaveEmail.setOnClickListener(this)
        binding.btChangeEmail.setOnClickListener(this)
        binding.btChangePhone.setOnClickListener(this)

        openDefaultScreen()
        binding.tvPhone.text = sessionManager.getUserPhone()
        binding.tvEmail.text = sessionManager.getUserEmail()
    }


    override fun onResume() {
        super.onResume()
        if (sessionManager.getUserType() == AppConstant.JOB_SEEKER) {
            val activity = requireActivity() as FindJobActivity
            activity.showHideBottomMenu(false)
        } else {
            val activity = requireActivity() as EmployerActivity
            activity.showHideBottomMenu(false)
        }
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.btBack -> {
                findNavController().navigate(R.id.openSettingScreen)
            }

            R.id.btChangePhone -> {
                openChangePhone()
            }

            R.id.btSavePhone -> {
                if (binding.etPhone.text.isEmpty()) {
                    alertErrorDialog(getString(R.string.fill_phone))
                } else if (!ValidationData.isPhoneNumber(binding.etPhone.text.toString())) {
                    alertErrorDialog(getString(R.string.fill_valid_phone))
                } else {
                    if (binding.mobileVerificationBox.visibility == View.VISIBLE) {
                       if (sessionManager.getUserType() == AppConstant.JOB_SEEKER){
                           mobileUpdate()
                       }else{
                           mobileUpdateEmployer()
                       }

                    } else {
                        if (sessionManager.getUserType() == AppConstant.JOB_SEEKER){
                            mobileUpdateRequest()
                        }else{
                            mobileUpdateRequestEmployer()
                        }
                    }
                }
            }

            R.id.btChangeEmail -> {
                openChangeEmail()
            }

            R.id.btSaveEmail -> {
                if (binding.etEmail.text.isEmpty()) {
                    alertErrorDialog(getString(R.string.fill_email))
                } else if (!ValidationData.isEmail(binding.etEmail.text.toString())) {
                    alertErrorDialog(getString(R.string.fill_valid_email))
                } else {
                    if (binding.emailVerificationBox.visibility == View.VISIBLE) {
                        if (sessionManager.getUserType() == AppConstant.JOB_SEEKER){
                            emailUpdate()
                        }else{
                            emailUpdateEmployer()
                        }
                    } else {
                        if (sessionManager.getUserType() == AppConstant.JOB_SEEKER){
                            emailUpdateRequest()
                        }else{
                            emailUpdateEmployerRequest()
                        }
                    }
                }
            }
        }
    }


    private fun mobileUpdateRequest() {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            settingViewModel.mobileUpdateRequest(
                sessionManager.getBearerToken(),
                binding.etPhone.text.toString()
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        binding.etPhone.isEnabled = false
                        binding.mobileVerificationBox.visibility = View.VISIBLE
                        binding.btSavePhone.text = getString(R.string.change_mobile_number)
                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }

    }

    private fun mobileUpdateRequestEmployer() {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            settingViewModel.mobileUpdateRequestEmployer(
                sessionManager.getBearerToken(),
                binding.etPhone.text.toString()
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        binding.etPhone.isEnabled = false
                        binding.mobileVerificationBox.visibility = View.VISIBLE
                        binding.btSavePhone.text = getString(R.string.change_mobile_number)
                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }

    }

    private fun mobileUpdate() {
        binding.tvProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.GONE
            settingViewModel.mobileUpdate(
                sessionManager.getBearerToken(),
                binding.etPhone.text.toString(),
                binding.etMobileOTP.text.toString()
            ).observe(viewLifecycleOwner) { jsonObject ->
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        binding.tvPhone.text = binding.etPhone.text.toString()
                        sessionManager.setUserPhone(binding.etPhone.text.toString())
                        openDefaultScreen()
                        binding.etPhone.text = null
                        binding.etMobileOTP.text = null
                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }
    }

    private fun mobileUpdateEmployer() {
        binding.tvProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.GONE
            settingViewModel.mobileUpdateEmployer(
                sessionManager.getBearerToken(),
                binding.etPhone.text.toString(),
                binding.etMobileOTP.text.toString()
            ).observe(viewLifecycleOwner) { jsonObject ->
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        binding.tvPhone.text = binding.etPhone.text.toString()
                        sessionManager.setUserPhone(binding.etPhone.text.toString())
                        openDefaultScreen()
                        binding.etPhone.text = null
                        binding.etMobileOTP.text = null
                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }
    }

    private fun emailUpdateRequest() {

        Log.e("Email", binding.etEmail.text.toString())

        binding.tvProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            settingViewModel.emailUpdateRequest(
                sessionManager.getBearerToken(),
                binding.etEmail.text.toString()
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        binding.etEmail.isEnabled = false
                        binding.emailVerificationBox.visibility = View.VISIBLE
                        binding.btSaveEmail.text = getString(R.string.change_email)
                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }

    }

    private fun emailUpdateEmployerRequest() {

        Log.e("Email", binding.etEmail.text.toString())

        binding.tvProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            settingViewModel.emailUpdateEmployerRequest(
                sessionManager.getBearerToken(),
                binding.etEmail.text.toString()
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        binding.etEmail.isEnabled = false
                        binding.emailVerificationBox.visibility = View.VISIBLE
                        binding.btSaveEmail.text = getString(R.string.change_email)
                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }

    }

    private fun emailUpdate() {
        binding.tvProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.GONE
            settingViewModel.emailUpdate(
                sessionManager.getBearerToken(),
                binding.etEmail.text.toString(),
                binding.etEmailOTP.text.toString()
            ).observe(viewLifecycleOwner) { jsonObject ->
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        binding.tvEmail.text = binding.etEmail.text.toString()
                        sessionManager.setUserEmail(binding.etEmail.text.toString())
                        openDefaultScreen()
                        binding.etEmail.text = null
                        binding.etEmailOTP.text = null
                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }

    }

    private fun emailUpdateEmployer() {
        binding.tvProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.GONE
            settingViewModel.emailUpdateEmployer(
                sessionManager.getBearerToken(),
                binding.etEmail.text.toString(),
                binding.etEmailOTP.text.toString()
            ).observe(viewLifecycleOwner) { jsonObject ->
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        binding.tvEmail.text = binding.etEmail.text.toString()
                        sessionManager.setUserEmail(binding.etEmail.text.toString())
                        openDefaultScreen()
                        binding.etEmail.text = null
                        binding.etEmailOTP.text = null
                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }

    }

    private fun openDefaultScreen() {
        binding.showEmailBox.visibility = View.VISIBLE
        binding.btChangeEmail.visibility = View.VISIBLE
        binding.editEmailBox.visibility = View.GONE
        binding.emailVerificationBox.visibility = View.GONE
        binding.showPhoneBox.visibility = View.VISIBLE
        binding.btChangePhone.visibility = View.VISIBLE
        binding.editPhoneBox.visibility = View.GONE
        binding.mobileVerificationBox.visibility = View.GONE
        binding.btSavePhone.text = getString(R.string.Continue)
        binding.btSaveEmail.text = getString(R.string.Continue)
    }

    private fun openChangeEmail() {
        binding.showEmailBox.visibility = View.VISIBLE
        binding.btChangeEmail.visibility = View.GONE
        binding.emailVerificationBox.visibility = View.GONE
        binding.editEmailBox.visibility = View.VISIBLE
    }

    private fun openChangePhone() {
        binding.showPhoneBox.visibility = View.VISIBLE
        binding.btChangePhone.visibility = View.GONE
        binding.mobileVerificationBox.visibility = View.GONE
        binding.editPhoneBox.visibility = View.VISIBLE
    }

}