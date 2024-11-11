package tech.merajobs.fragment.findJobScreens.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.adapter.CategoryAdapter
import tech.merajobs.adapter.CategoryAllAdapter
import tech.merajobs.adapter.CompanyAdapter
import tech.merajobs.adapter.CompanyAllAdapter
import tech.merajobs.adapter.IndustryAdapter
import tech.merajobs.adapter.JobAdapter
import tech.merajobs.dataModel.CategoryDataModel
import tech.merajobs.dataModel.CompanyDataModel
import tech.merajobs.dataModel.IndustryDataModel
import tech.merajobs.dataModel.JobDataModel
import tech.merajobs.dataModel.JobList
import tech.merajobs.databinding.FragmentFindJobHomeBinding
import tech.merajobs.networkModel.homeViewModel.HomeViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager

class FindJobHomeFragment : BaseFragment(), View.OnClickListener {

    lateinit var binding: FragmentFindJobHomeBinding
    lateinit var sessionManager: SessionManager
    lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFindJobHomeBinding.inflate(
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
                    closeAppAlertDialog()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)







        binding.btOpenRemoteJob.setOnClickListener(this)
        binding.btOpenAllJobs.setOnClickListener(this)
        binding.btOpenFullTimeJob.setOnClickListener(this)
        binding.btOpenPerTimeJob.setOnClickListener(this)
        binding.btOpenLiveJob.setOnClickListener(this)
        binding.btOpenCompanyJob.setOnClickListener(this)
        binding.btOpenNewJob.setOnClickListener(this)
        binding.btOpenAllCategories.setOnClickListener(this)
        binding.btOpenAllCompany.setOnClickListener(this)
        binding.btOpenSideMenu.setOnClickListener(this)
        binding.btOpenFilter.setOnClickListener(this)
        binding.btOpenAllIndustries.setOnClickListener(this)

        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
           getUserProfile()
            getJobList()
            getCategory()
            getCompany()
            getHomeCounter()
            getIndustry()
        }

    }

    override fun onClick(p0: View?) {

        when (p0!!.id) {
            R.id.btOpenRemoteJob -> {
                val bundle = Bundle()
                bundle.putString(AppConstant.PAGE_TITLE, "Remote Jobs")
                bundle.putString(AppConstant.WORK_LOCATION_TYPE, "1")
                findNavController().navigate(R.id.openSearchJobScreen, bundle)
            }

            R.id.btOpenFullTimeJob -> {
                val bundle = Bundle()
                bundle.putString(AppConstant.PAGE_TITLE, "Full Time Jobs")
                bundle.putString(AppConstant.WORK_LOCATION_TYPE, "2")
                findNavController().navigate(R.id.openSearchJobScreen, bundle)
            }

            R.id.btOpenAllJobs -> {
                val bundle = Bundle()
                bundle.putString(AppConstant.PAGE_TITLE, "Search Jobs")
                findNavController().navigate(R.id.openSearchJobScreen, bundle)
            }

            R.id.btOpenPerTimeJob -> {
                val bundle = Bundle()
                bundle.putString(AppConstant.PAGE_TITLE, "Part Time Jobs")
                bundle.putString(AppConstant.WORK_LOCATION_TYPE, "3")
                findNavController().navigate(R.id.openSearchJobScreen, bundle)
            }

            R.id.btOpenLiveJob -> {
                val bundle = Bundle()
                bundle.putString(AppConstant.PAGE_TITLE, "Live Jobs")
                findNavController().navigate(R.id.openSearchJobScreen, bundle)
            }

            R.id.btOpenCompanyJob -> {
                findNavController().navigate(R.id.openCompanyAllScreen)
            }

            R.id.btOpenNewJob -> {
                val bundle = Bundle()
                bundle.putString(AppConstant.PAGE_TITLE, "New jobs")
                findNavController().navigate(R.id.openSearchJobScreen, bundle)
            }

            R.id.btOpenAllCategories -> {
                findNavController().navigate(R.id.openCategoriesAllScreen)
            }

            R.id.btOpenAllIndustries -> {
                findNavController().navigate(R.id.openIndustryFragment)
            }

            R.id.btOpenAllCompany -> {
                findNavController().navigate(R.id.openCompanyAllScreen)
            }

            R.id.btOpenSideMenu -> {
                val activity = requireActivity() as FindJobActivity
                activity.openDrawer()
            }

            R.id.btOpenFilter -> {
                val bundle = Bundle()
                bundle.putString(AppConstant.SOURCE_FRAGMENT, AppConstant.HOME_SCREEN)
                findNavController().navigate(R.id.openFilterScreen, bundle)
            }
        }

    }

    private fun getIndustry() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            homeViewModel.getIndustry(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val companyList =
                                Gson().fromJson(jsonObjectData, IndustryDataModel::class.java)


                            binding.industriesRecyclerView.adapter =
                                IndustryAdapter(companyList.data, requireActivity())
                            binding.industriesRecyclerView.layoutManager =
                                LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }

    private fun getCompany() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            homeViewModel.getCompany(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val companyList =
                                Gson().fromJson(jsonObjectData, CompanyDataModel::class.java)


                            binding.companyRecyclerView.adapter =
                                CompanyAdapter(companyList.data, requireActivity())
                            binding.companyRecyclerView.layoutManager =
                                LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }

    private fun getHomeCounter() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            homeViewModel.getHomeCounter(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {


                            binding.tvRemoteJobCount.text = checkFieldSting(jsonObjectData["data"].asJsonObject["remote_jobs"])
                            binding.tvFullJobCount.text = checkFieldSting(jsonObjectData["data"].asJsonObject["full_time_jobs"])
                            binding.tvPartTimeJob.text = checkFieldSting(jsonObjectData["data"].asJsonObject["part_time_job"])

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as FindJobActivity
        activity.openHomeScreen()
        activity.showHideBottomMenu(true)
    }

    private fun getCategory() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            homeViewModel.getCategory(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val categoryData =
                                Gson().fromJson(jsonObjectData, CategoryDataModel::class.java)

                            binding.categoryRecyclerView.adapter =
                                CategoryAdapter(categoryData.data, requireActivity())
                            binding.categoryRecyclerView.layoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }

    fun closeAppAlertDialog() {
        val alertErrorDialog = Dialog(requireActivity())
        alertErrorDialog.setCancelable(false)
        alertErrorDialog.setContentView(R.layout.alertbox_closeapp)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertErrorDialog.window!!.attributes)

        alertErrorDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertErrorDialog.window!!.attributes = layoutParams

        val tvTitle: TextView = alertErrorDialog.findViewById(R.id.tv_title)
        val btnYes: TextView = alertErrorDialog.findViewById(R.id.btnYes)
        val btnNo: TextView = alertErrorDialog.findViewById(R.id.btnNo)
        tvTitle.text = getString(R.string.are_you_want_to_close_the_app)

        btnYes.setOnClickListener {
            requireActivity().finish()
            alertErrorDialog.dismiss()
        }

        btnNo.setOnClickListener {
            alertErrorDialog.dismiss()
        }

        alertErrorDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertErrorDialog.show()
    }


    // This Function is used for get user profile
    @SuppressLint("SetTextI18n")
    private fun getUserProfile() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            homeViewModel.getUserProfile(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            sessionManager.setUserName(checkFieldSting(jsonObjectData["data"].asJsonObject["name"]))
                            sessionManager.setUserEmail(checkFieldSting(jsonObjectData["data"].asJsonObject["email"]))
                            sessionManager.setUserPhone(checkFieldSting(jsonObjectData["data"].asJsonObject["mobile"]))
                            sessionManager.setUserProfile(
                                checkFieldSting(
                                    jsonObjectData["data"].asJsonObject.get(
                                        "photo"
                                    )
                                )
                            )


                            sessionManager.setUserProfileVerify(
                                checkFieldSting(
                                    jsonObjectData["data"].asJsonObject.get(
                                        "photo_approved"
                                    )
                                )
                            )


                            val activity = requireActivity() as FindJobActivity
                            activity.setNavigationItem(
                                checkFieldSting(jsonObjectData["data"].asJsonObject["name"]),
                                checkFieldSting(jsonObjectData["data"].asJsonObject["mobile"]),
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("photo")),
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("photo_approved"))
                            )

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }

    private fun getJobList() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            homeViewModel.getJobList(
                sessionManager.getBearerToken(),
                sessionManager.getUserID(),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                ""
            )
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val jobData = Gson().fromJson(jsonObjectData, JobDataModel::class.java)
                            val jobList = jobData.data

                            if (jobList != null) {
                                val adapter =
                                    JobAdapter(jobList, requireActivity(), AppConstant.HOME_SCREEN)

                                binding.recentJobRecyclerView.adapter = adapter
                                binding.recentJobRecyclerView.layoutManager = LinearLayoutManager(
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