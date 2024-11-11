package tech.merajobs.fragment.employerScreen.home

import android.os.Bundle
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
import tech.merajobs.adapter.PostedJobAdapter
import tech.merajobs.dataModel.JobDataModel
import tech.merajobs.databinding.FragmentPostedJobBinding
import tech.merajobs.networkModel.businessViewModel.BusinessViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager


class PostedJobFragment : BaseFragment() {

    lateinit var binding: FragmentPostedJobBinding
    lateinit var sessionManager: SessionManager
    private lateinit var businessViewModel: BusinessViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPostedJobBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        businessViewModel = ViewModelProvider(this)[BusinessViewModel::class.java]
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.openHomeScreen)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.btBack.setOnClickListener {
            findNavController().navigate(R.id.openHomeScreen)
        }

        binding.btOpenMenu.setOnClickListener {
            val activity = requireActivity() as EmployerActivity
            activity.openDrawer()
        }



        getJobList()

    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as EmployerActivity
        activity.showHideBottomMenu(true)
    }


    private fun getJobList() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            businessViewModel.getPostedJobList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val jobData = Gson().fromJson(jsonObjectData, JobDataModel::class.java)
                            val jobList = jobData.data

                            if (jobList == null) {
                                binding.tvNoRecordFound.visibility = View.VISIBLE
                            } else if (jobList.isEmpty()) {
                                binding.tvNoRecordFound.visibility = View.VISIBLE
                            } else {
                                binding.tvNoRecordFound.visibility = View.GONE

                                val adapter = PostedJobAdapter(
                                    jobList,
                                    requireActivity(),
                                    AppConstant.HOME_SCREEN
                                )

                                binding.searchJobRecyclerView.adapter = adapter
                                binding.searchJobRecyclerView.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )


                            }

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }


}