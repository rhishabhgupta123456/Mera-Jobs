package tech.merajobs.fragment.findJobScreens.home

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
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.adapter.JobAdapter
import tech.merajobs.dataModel.JobDataModel
import tech.merajobs.dataModel.JobList
import tech.merajobs.databinding.FragmentBookmarkBinding
import tech.merajobs.networkModel.homeViewModel.HomeViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager

class SavedJobFragment : BaseFragment() , View.OnClickListener{

    lateinit var binding : FragmentBookmarkBinding
    lateinit var sessionManager: SessionManager
    lateinit var homeViewModel: HomeViewModel

     var searchKeyword = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.openHomeScreen)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.btBack.setOnClickListener(this)
        binding.btOpenMenu.setOnClickListener(this)
        binding.btSearch.setOnClickListener(this)


        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getSavedJobList()
        }


    }



    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btBack -> {
                findNavController().navigate(R.id.openHomeScreen)
            }

            R.id.btOpenMenu -> {
                val activity = requireActivity() as FindJobActivity
                activity.openDrawer()
            }

            R.id.btSearch ->{
                searchKeyword = binding.etSearch.text.toString()
                getSavedJobList()
            }

        }
    }



    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as FindJobActivity
        activity.openBookMarkScreen()
        activity.showHideBottomMenu(true)
    }

    private fun getSavedJobList() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            homeViewModel.getSavedJobList(sessionManager.getBearerToken(),searchKeyword)
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val jobData = Gson().fromJson(jsonObjectData, JobDataModel::class.java)
                            val jobList = jobData.data

                            if (jobList != null){
                                val adapter =
                                    JobAdapter(jobList, requireActivity(), AppConstant.HOME_SCREEN)

                                binding.bookMarkJobRecyclerView.adapter =adapter
                                binding.bookMarkJobRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


                                adapter.setOnRequestAction(object : JobAdapter.OnRequestAction {
                                    override fun savedJob(item: JobList) {

                                    }
                                    override fun unSavedJob(item: JobList) {
                                        if (!isNetworkAvailable()) {
                                            alertErrorDialog(getString(R.string.no_internet))
                                        } else {
                                            unSavedTheJob(item)
                                        }

                                    }

                                })
                            }

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }



    private fun unSavedTheJob(item: JobList) {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            homeViewModel.unSavedJob(sessionManager.getBearerToken() , item.saved_job_id)
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            getSavedJobList()
                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }

    }



}