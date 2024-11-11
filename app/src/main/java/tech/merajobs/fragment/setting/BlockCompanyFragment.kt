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
import tech.merajobs.adapter.BlockCompanyAdapter
import tech.merajobs.dataModel.BlockCompanyList
import tech.merajobs.dataModel.BlockList
import tech.merajobs.databinding.FragmentBlockCompanyBinding
import tech.merajobs.networkModel.settingNetworkModel.SettingViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager


class BlockCompanyFragment : BaseFragment(), View.OnClickListener {

    lateinit var binding: FragmentBlockCompanyBinding
    private lateinit var settingViewModel: SettingViewModel
    lateinit var sessionManager: SessionManager
    private lateinit var blockCompanyAdapter : BlockCompanyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBlockCompanyBinding.inflate(
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
        binding.openAddNewCompany.setOnClickListener(this)

        getBlockedCompany()
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

            R.id.openAddNewCompany -> {
                findNavController().navigate(R.id.openAddBlockedCompaniesScreen)
            }

        }
    }

    private fun getBlockedCompany() {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            settingViewModel.getBlockCompanyList(
                sessionManager.getBearerToken(),).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {

                        val employerLIst =
                            Gson().fromJson(jsonObjectData, BlockCompanyList::class.java)

                        if (employerLIst.data != null) {
                            if (employerLIst.data.isNotEmpty()){
                                binding.tvNoRecordFound.visibility = View.GONE
                                blockCompanyAdapter = BlockCompanyAdapter(employerLIst.data, requireActivity(), true)
                                binding.blockListRecyclerView.adapter = blockCompanyAdapter
                                binding.blockListRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
                                blockCompanyAdapter.setOnRequestAction(object :BlockCompanyAdapter.OnRequestAction{

                                    override fun blockedCompany(item: BlockList, position: Int) {
                                        blockedEmployer(item,position)
                                    }

                                    override fun unblockedCompany(item: BlockList, position: Int) {
                                        unBlockedEmployer(item,position)
                                    }

                                } )
                            }else{
                                binding.tvNoRecordFound.visibility = View.VISIBLE
                            }

                        }else{
                            binding.tvNoRecordFound.visibility = View.VISIBLE
                        }

                    } catch (e: Exception) {
                        alertErrorDialog(e.message.toString())
                    }
                }

            }
        }

    }

    private fun blockedEmployer(item: BlockList, position: Int) {
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

    private fun unBlockedEmployer(item: BlockList, position: Int) {
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