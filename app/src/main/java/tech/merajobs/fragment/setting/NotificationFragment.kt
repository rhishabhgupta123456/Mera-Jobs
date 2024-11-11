package tech.merajobs.fragment.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.EmployerActivity
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.databinding.FragmentAccountBinding
import tech.merajobs.databinding.FragmentNotificationBinding
import tech.merajobs.networkModel.settingNetworkModel.SettingViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager


class NotificationFragment : BaseFragment() , View.OnClickListener {

    lateinit var binding: FragmentNotificationBinding
    lateinit var sessionManager: SessionManager
    lateinit var settingViewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNotificationBinding.inflate(
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
        binding.btSaveNotificationSetting.setOnClickListener(this)

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
        when(v!!.id){

            R.id.btBack ->{
                findNavController().navigate(R.id.openSettingScreen)
            }

            R.id.btSaveNotificationSetting ->{
                saveNotificationSetting()
            }
        }
    }

    private fun saveNotificationSetting() {
        binding.tvProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.GONE
            settingViewModel.notificationSetting(
                sessionManager.getBearerToken(),
                checkedButton(binding.careerTipsNotification),
                checkedButton(binding.jobLookingStatus),
                checkedButton(binding.recommendedJobNotification),
                checkedButton(binding.jobAlertNotification),
                checkedButton(binding.applicationStatusNotification),
                checkedButton(binding.profileViewNotification),
                checkedButton(binding.profileStrengthNotification),
                checkedButton(binding.profileVisibility),
                checkedButton(binding.whatsappNotificationEnabled),
                checkedButton(binding.promotionsNotification),
            ).observe(viewLifecycleOwner) { jsonObject ->
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
                        Toast.makeText(
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }

    }

    private fun checkedButton(checkBox : CheckBox) : Int{
        return if (checkBox.isChecked){
            1
        }else{
            0
        }
    }

}