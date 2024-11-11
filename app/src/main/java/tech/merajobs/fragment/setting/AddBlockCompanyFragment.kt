package tech.merajobs.fragment.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import tech.merajobs.adapter.SearchCompanyAdapter
import tech.merajobs.dataModel.EmployerDataModel
import tech.merajobs.dataModel.EmployerList
import tech.merajobs.databinding.FragmentAddBlockCompanyBinding
import tech.merajobs.networkModel.settingNetworkModel.SettingViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager


class AddBlockCompanyFragment : BaseFragment() {

    lateinit var binding: FragmentAddBlockCompanyBinding
    private lateinit var settingViewModel: SettingViewModel
    lateinit var sessionManager: SessionManager
    private lateinit var blockCompanyAdapter : SearchCompanyAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddBlockCompanyBinding.inflate(
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
                    findNavController().navigate(R.id.openBlockedCompaniesScreen)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)






        binding.btBack.setOnClickListener {
            findNavController().navigate(R.id.openBlockedCompaniesScreen)
        }

        binding.btSearch.setOnClickListener {
            if (binding.etCompanyName.text.isNotEmpty()){
                searchCompanyList(binding.etCompanyName.text.toString())
            }
        }

    }


    private fun searchCompanyList(keyWord: String) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            settingViewModel.getEmployerList(
                sessionManager.getBearerToken(),
                keyWord
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {

                        val employerLIst =
                            Gson().fromJson(jsonObjectData, EmployerDataModel::class.java)

                        if (employerLIst.data != null) {
                            blockCompanyAdapter = SearchCompanyAdapter(employerLIst.data, requireActivity())
                            binding.searchBlockList.adapter = blockCompanyAdapter
                            binding.searchBlockList.layoutManager = LinearLayoutManager(requireActivity())

                            blockCompanyAdapter.setOnRequestAction(object :SearchCompanyAdapter.OnRequestAction{
                                override fun blockedCompany(item: EmployerList, position: Int) {
                                    blockedEmployer(item,position)
                                }

                                override fun unblockedCompany(item: EmployerList, position: Int) {
                                    unBlockedEmployer(item,position)
                                }

                            } )
                        }

                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }

    }

    private fun blockedEmployer(item: EmployerList, position: Int) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            settingViewModel.addBlockCompany(
                sessionManager.getBearerToken(),
                item.id.toInt()
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        Toast.makeText(
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        blockCompanyAdapter.removeItem(position)
                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }

    }

    private fun unBlockedEmployer(item: EmployerList, position: Int) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            settingViewModel.deleteBlockCompany(
                sessionManager.getBearerToken(),
                item.id.toInt()
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        Toast.makeText(
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        blockCompanyAdapter.removeItem(position)
                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }

    }

}