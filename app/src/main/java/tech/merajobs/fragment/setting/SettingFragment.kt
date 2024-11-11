package tech.merajobs.fragment.setting

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.EmployerActivity
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.databinding.FragmentSettingBinding
import tech.merajobs.networkModel.settingNetworkModel.SettingViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager

class SettingFragment : BaseFragment(), View.OnClickListener {

    lateinit var binding: FragmentSettingBinding
    private lateinit var sessionManager: SessionManager
    lateinit var settingViewModel: SettingViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingBinding.inflate(
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
                    findNavController().navigate(R.id.openHomeScreen)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.etLanguage.text = sessionManager.getLanguage()
        binding.btOpenAccount.setOnClickListener(this)
        binding.btLogOut.setOnClickListener(this)
        binding.btnLanguage.setOnClickListener(this)
        binding.btEditPassword.setOnClickListener(this)
        binding.btBlockCompany.setOnClickListener(this)
        binding.openNotification.setOnClickListener(this)
        binding.openWhatsupNotification.setOnClickListener(this)

        binding.tvName.text = sessionManager.getUserName()
        binding.tvMobile.text = sessionManager.getUserPhone()


        binding.whatsAppNotificationButton.isChecked = sessionManager.getUserWhatsAppNotification() != "0"


        if (sessionManager.getUserProfileVerify() == "0"){
            Glide.with(requireActivity())
                .load(AppConstant.MEDIA_BASE_URL + sessionManager.getUserProfile())
                .apply(RequestOptions.bitmapTransform(BlurTransformation(AppConstant.BLUR_RADIUS)))
                .placeholder(R.drawable.demo_user)
                .into(binding.tvUserProfile)

        }else{
            Glide.with(requireActivity())
                .load(AppConstant.MEDIA_BASE_URL + sessionManager.getUserProfile())
                .placeholder(R.drawable.demo_user)
                .into(binding.tvUserProfile)
        }

        setScreen()
    }

    private fun setScreen() {
        if (sessionManager.getUserType() == AppConstant.JOB_SEEKER) {
            binding.btBlockCompany.visibility = View.VISIBLE
            binding.openNotification.visibility = View.VISIBLE
            binding.whatsAppNotificationButton.visibility = View.GONE
        } else {
            binding.openNotification.visibility = View.GONE
            binding.btBlockCompany.visibility = View.GONE
            binding.whatsAppNotificationButton.visibility = View.VISIBLE
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.btLogOut -> {
                logOutDialog()
            }

            R.id.btnLanguage -> {
                if (binding.etLanguage.text.toString() == AppConstant.ENGLISH) {
                    sessionManager.setLanguage(AppConstant.HINDI)
                    binding.etLanguage.text = AppConstant.HINDI
                } else {
                    sessionManager.setLanguage(AppConstant.ENGLISH)
                    binding.etLanguage.text = AppConstant.ENGLISH
                }
            }

            R.id.openWhatsupNotification -> {
                binding.whatsAppNotificationButton.isChecked =
                    !binding.whatsAppNotificationButton.isChecked
                changeWhatsAppNotification()
            }

            R.id.btOpenAccount -> {
                findNavController().navigate(R.id.openAccountScreen)
            }

            R.id.btEditPassword -> {
                findNavController().navigate(R.id.openUpdatePasswordScreen)
            }

            R.id.openNotification -> {
                findNavController().navigate(R.id.openNotificationScreen)

            }

            R.id.btBlockCompany -> {
                findNavController().navigate(R.id.openBlockedCompaniesScreen)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager.getUserType() == AppConstant.JOB_SEEKER) {
            val activity = requireActivity() as FindJobActivity
            activity.openProfileScreen()
            activity.showHideBottomMenu(false)
        } else {
            val activity = requireActivity() as EmployerActivity
            activity.openProfileScreen()
            activity.showHideBottomMenu(false)
        }
    }

    private fun changeWhatsAppNotification() {
        binding.tvProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.GONE
            settingViewModel.changeWhatsAppNotification(
                sessionManager.getBearerToken(),
                if (binding.whatsAppNotificationButton.isChecked) 1 else 0,
            ).observe(viewLifecycleOwner) { jsonObject ->
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {

                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }
    }


}