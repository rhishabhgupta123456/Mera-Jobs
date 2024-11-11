package tech.merajobs.fragment.findJobScreens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import tech.merajobs.databinding.FragmentSearchBinding
import tech.merajobs.networkModel.homeViewModel.HomeViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager

class SearchFragment : BaseFragment() {

    lateinit var binding: FragmentSearchBinding

    lateinit var sessionManager: SessionManager
    lateinit var homeViewModel: HomeViewModel

    private var workLocationType: String = ""
    private var jobLabel: String = ""
    private var employmentType: String = ""
    private var location: String = ""
    private var role: String = ""
    private var skill: String = ""
    private var salaryRange: String = ""
    private var employerId: String = ""

    private var sortOrder: String = "desc"
    private var sortField: String = "created_at"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchBinding.inflate(
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
                    if (requireArguments().getString(AppConstant.SOURCE_FRAGMENT) == AppConstant.MESSAGE) {
                        findNavController().navigate(R.id.openMessageScreen)
                    } else {
                        findNavController().navigate(R.id.openHomeScreen)
                    }

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.btBack.setOnClickListener {
            if (requireArguments().getString(AppConstant.SOURCE_FRAGMENT) == AppConstant.MESSAGE) {
                findNavController().navigate(R.id.openMessageScreen)
            } else {
                findNavController().navigate(R.id.openHomeScreen)
            }
        }

        workLocationType = try {
            if (requireArguments().getString(AppConstant.WORK_LOCATION_TYPE) != null) {
                requireArguments().getString(AppConstant.WORK_LOCATION_TYPE).toString()
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }

        jobLabel = try {
            if (requireArguments().getString(AppConstant.JOB_LABEL) != null) {
                requireArguments().getString(AppConstant.JOB_LABEL).toString()
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }

        employmentType = try {
            if (requireArguments().getString(AppConstant.EMPLOYMENT_TYPE) != null) {
                requireArguments().getString(AppConstant.EMPLOYMENT_TYPE).toString()
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }

        location = try {
            if (requireArguments().getString(AppConstant.LOCATION) != null) {
                requireArguments().getString(AppConstant.LOCATION).toString()
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }

        role = try {
            if (requireArguments().getString(AppConstant.ROLE) != null) {
                requireArguments().getString(AppConstant.ROLE).toString()
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }

        skill = try {
            if (requireArguments().getString(AppConstant.SKILL) != null) {
                requireArguments().getString(AppConstant.SKILL).toString()
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }

        salaryRange = try {
            if (requireArguments().getString(AppConstant.SALARY_RANGE) != null) {
                requireArguments().getString(AppConstant.SALARY_RANGE).toString()
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }

        employerId = try {
            if (requireArguments().getString(AppConstant.EMPLOYER_ID) != null) {
                requireArguments().getString(AppConstant.EMPLOYER_ID).toString()
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }

        binding.tvPageTitle.text = requireArguments().getString(AppConstant.PAGE_TITLE)
        binding.tvJobTitle2.text = requireArguments().getString(AppConstant.PAGE_TITLE).toString()


        binding.btOpenFilter.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(AppConstant.SOURCE_FRAGMENT, AppConstant.SEARCH_SCREEN)
            findNavController().navigate(R.id.openFilterScreen, bundle)
        }


        attachSorting()
        getJobList()

    }


    private fun attachSorting() {
        val list = mutableListOf("Select")
        list.add("Ascending by posted date")
        list.add("Descending by posted date")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            list
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sortSpinner.adapter = adapter
        binding.sortSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    sortOrder = if (position > 0) {
                        if (position == 1) {
                            "asc"
                        } else {
                            "desc"
                        }
                    } else {
                        ""
                    }
                    getJobList()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }

    }


    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as FindJobActivity
        activity.showHideBottomMenu(false)
    }


    private fun getJobList() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            homeViewModel.getJobList(
                sessionManager.getBearerToken(),
                sessionManager.getUserID(),
                workLocationType,
                jobLabel,
                employmentType,
                location,
                role,
                skill,
                salaryRange,
                sortField,
                sortOrder,
                employerId
            )
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

                                val adapter =
                                    JobAdapter(jobList, requireActivity(), AppConstant.HOME_SCREEN)

                                binding.searchJobRecyclerView.adapter = adapter
                                binding.searchJobRecyclerView.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )

                                adapter.setOnRequestAction(object : JobAdapter.OnRequestAction {
                                    override fun savedJob(item: JobList) {
                                        if (!isNetworkAvailable()) {
                                            alertErrorDialog(getString(R.string.no_internet))
                                        } else {
                                            savedTheJob(item)
                                        }

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


    private fun savedTheJob(item: JobList) {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            homeViewModel.savedTheJob(sessionManager.getBearerToken(), item.id)
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            Toast.makeText(
                                requireContext(),
                                jsonObjectData["message"].asJsonArray[0].asString,
                                Toast.LENGTH_SHORT
                            ).show()

                            getJobList()


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

            homeViewModel.unSavedJob(sessionManager.getBearerToken(), item.id)
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            Toast.makeText(
                                requireContext(),
                                jsonObjectData["message"].asJsonArray[0].asString,
                                Toast.LENGTH_SHORT
                            ).show()

                            getJobList()


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }

    }


}