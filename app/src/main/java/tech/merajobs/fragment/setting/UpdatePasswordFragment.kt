package tech.merajobs.fragment.setting

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.EmployerActivity
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.databinding.FragmentUpdatePasswordBinding
import tech.merajobs.networkModel.settingNetworkModel.SettingViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager
import tech.merajobs.utility.ValidationData

class UpdatePasswordFragment : BaseFragment(), View.OnClickListener {

    lateinit var binding: FragmentUpdatePasswordBinding
    lateinit var sessionManager: SessionManager
    lateinit var settingViewModel: SettingViewModel

    private var confirmEye = false
    private var newEye = false
    private var eye = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUpdatePasswordBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
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
        binding.UpdatePassword.setOnClickListener(this)
        binding.confirmEye.setOnClickListener(this)
        binding.newEye.setOnClickListener(this)
        binding.Eye.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btBack -> {
                findNavController().navigate(R.id.openSettingScreen)
            }

            R.id.UpdatePassword -> {
                if (checkValidation()) {
                    if (sessionManager.getUserType() == AppConstant.JOB_SEEKER) {
                        changePassword()
                    } else {
                        changePasswordEmployer()
                    }
                }
            }

            R.id.Eye -> {
                if (eye) {
                    eye = false
                    binding.Eye.setImageResource(R.drawable.hidepass_icon)
                    binding.etOldPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                } else {
                    eye = true
                    binding.Eye.setImageResource(R.drawable.showpass_icon)
                    binding.etOldPassword.transformationMethod =
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

            R.id.newEye -> {
                if (newEye) {
                    newEye = false
                    binding.newEye.setImageResource(R.drawable.hidepass_icon)
                    binding.etNewPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()

                } else {
                    newEye = true
                    binding.newEye.setImageResource(R.drawable.showpass_icon)
                    binding.etNewPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                }

            }

        }
    }

    private fun changePassword() {
        binding.tvProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.GONE
            settingViewModel.changePassword(
                sessionManager.getBearerToken(),
                binding.etOldPassword.text.toString(),
                binding.etNewPassword.text.toString(),
                binding.etConfirmPassword.text.toString()
            ).observe(viewLifecycleOwner) { jsonObject ->
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        passwordChangeAlertBox()
                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }

    }

    private fun changePasswordEmployer() {
        binding.tvProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.GONE
            settingViewModel.changePasswordEmployer(
                sessionManager.getBearerToken(),
                binding.etOldPassword.text.toString(),
                binding.etNewPassword.text.toString(),
                binding.etConfirmPassword.text.toString()
            ).observe(viewLifecycleOwner) { jsonObject ->
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        passwordChangeAlertBox()
                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
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
            logOutAccount()
            postDialog.dismiss()
        }
        postDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        postDialog.show()

    }

    // This function is use for check validation in all screen field
    private fun checkValidation(): Boolean {

        if (binding.etOldPassword.text.isEmpty()) {
            alertErrorDialog(getString(R.string.please_fill_old_password))
            binding.etOldPassword.requestFocus()
            return false
        } else if (binding.etNewPassword.text.isEmpty()) {
            alertErrorDialog(getString(R.string.please_fill_mew_password))
            binding.etNewPassword.requestFocus()
            return false
        } else if (!ValidationData.passCheck(binding.etNewPassword.text.toString())) {
            alertErrorDialog(getString(R.string.password_validation_text))
            binding.etNewPassword.requestFocus()
            return false
        } else if (binding.etNewPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
            alertErrorDialog(getString(R.string.new_confirm_password_validation_text))
            return false
        } else if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
            return false
        } else {
            return true
        }
    }


}