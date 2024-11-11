package tech.merajobs.fragment.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.EmployerActivity
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.adapter.BlockCompanyAdapter
import tech.merajobs.dataModel.BlockCompanyList
import tech.merajobs.dataModel.BlockList
import tech.merajobs.databinding.FragmentBlockCompanyBinding
import tech.merajobs.databinding.FragmentCMSBinding
import tech.merajobs.networkModel.settingNetworkModel.SettingViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager

class CMSFragment : BaseFragment(), View.OnClickListener {


    lateinit var binding: FragmentCMSBinding
    private lateinit var settingViewModel: SettingViewModel
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCMSBinding.inflate(
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


        binding.btBack.setOnClickListener(this)

        when (sessionManager.getSourceFragment()) {
            AppConstant.TERM_CONDITION -> {
                getCMS("terms-conditions")
            }

            AppConstant.PRIVACY_POLICY -> {
                getCMS("privacy-policy")
            }

            AppConstant.ABOUT_US -> {
                getCMS("about-us")
            }
        }


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
                findNavController().navigate(R.id.openHomeScreen)
            }

            R.id.openAddNewCompany -> {
                findNavController().navigate(R.id.openAddBlockedCompaniesScreen)
            }

        }
    }

    private fun getCMS(cmKey: String) {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            settingViewModel.getCMS(
                cmKey,
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        val data = checkFieldObject(jsonObjectData["data"].asJsonObject)
                        if (data != null) {

                            binding.tvPageTitle.text = checkFieldSting(data["page_title"])
                            binding.tvContent.text =
                                htmlToPlainText(checkFieldSting(data["page_content"]))

                        }
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }

        }
    }


}