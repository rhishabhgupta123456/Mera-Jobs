package tech.merajobs.fragment.postJobsScreen

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.adapter.PathAdapter
import tech.merajobs.dataModel.AllFilterData
import tech.merajobs.dataModel.IndustryDataModel
import tech.merajobs.dataModel.IndustryDepartmentDataModel
import tech.merajobs.dataModel.IndustryDepartmentRoleDataModel
import tech.merajobs.dataModel.Model1
import tech.merajobs.dataModel.PathDataModel
import tech.merajobs.dataModel.SkillData
import tech.merajobs.databinding.FragmentJobDetailBinding
import tech.merajobs.networkModel.postJobViewModel.PostJobViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.PostJobSessionManager
import tech.merajobs.utility.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class JobDetailFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentJobDetailBinding
    private lateinit var sessionManager: SessionManager
    lateinit var postJobSessionManager: PostJobSessionManager
    lateinit var postJobViewModel: PostJobViewModel


    var role: String? = null
    var industry: String? = null
    var employmentType: String? = null
    var department: String? = null
    var jobLable: String? = null


    val selectedSkill = ArrayList<String>()
    var showSkillList = ArrayList<PathDataModel>()
    lateinit var pathAdapter: PathAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentJobDetailBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        postJobSessionManager = PostJobSessionManager(requireContext())
        postJobViewModel = ViewModelProvider(this)[PostJobViewModel::class.java]
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.openDirectPostJobFragment)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.btBack.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        binding.etLastDateToApply.setOnClickListener(this)

        pathAdapter = PathAdapter(showSkillList, requireContext())
        binding.selectCountryRecycleView.adapter = pathAdapter
        binding.selectCountryRecycleView.layoutManager =
            GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false)

        updateProgress()
        getSkillList()
        getIndustryList()
        getAllFilterList()

        setScreen()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btBack -> {
                findNavController().navigate(R.id.openDirectPostJobFragment)
            }

            R.id.btnClose -> {
                findNavController().navigate(R.id.openDirectPostJobFragment)
            }

            R.id.etLastDateToApply -> {
                openDatePicker { selectedDate ->
                    binding.tvLastDateToApply.text = selectedDate
                }

            }

            R.id.btnNext -> {
                if (checkValidation()) {
                    postJobSessionManager.setTitle(binding.etTitle.text.toString())
                    postJobSessionManager.setNoOFVacancy(binding.etNoOfVacancy.text.toString())
                    postJobSessionManager.setJobLabel(jobLable!!)
                    postJobSessionManager.setJobLabelLabel(binding.jobLabelSpinner.text.toString())
                    postJobSessionManager.setIndustry(industry!!)
                    postJobSessionManager.setIndustryLabel(binding.industrySpinner.text.toString())
                    postJobSessionManager.setDepartment(department!!)
                    postJobSessionManager.setDepartmentLabel(binding.departmentSpinner.text.toString())
                    postJobSessionManager.setSkill(selectedSkill)
                    postJobSessionManager.setRole(role!!)
                    postJobSessionManager.setRoleLabel(binding.roleSpinner.text.toString())
                    postJobSessionManager.setGoodToHave(binding.etGoodToHave.text.toString())
                    postJobSessionManager.setDescription(binding.etDescription.text.toString())
                    postJobSessionManager.setLastDateToApply(binding.tvLastDateToApply.text.toString())
                    findNavController().navigate(R.id.openEligibilityFragment)
                }
            }
        }
    }

    private fun openDatePicker(callback: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val formattedDate = dateFormatter.format(selectedDate.time)
                callback(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }


    private fun checkValidation(): Boolean {
        if (binding.etTitle.text.toString().isEmpty()) {
            alertErrorDialog("Please fill the title")
            return false
        } else if (binding.etNoOfVacancy.text.toString().isEmpty()) {
            alertErrorDialog("Please fill the number of vacancy")
            return false
        } else if (binding.jobLabelSpinner.text.toString() == "Select" || jobLable == null) {
            alertErrorDialog("Please fill the job label")
            return false
        } else if (industry == null || binding.industrySpinner.text == "Select") {
            alertErrorDialog("Please select the industry")
            return false
        } else if (department == null|| binding.departmentSpinner.text == "Select") {
            alertErrorDialog("Please select the department")
            return false
        } else if (role == null|| binding.roleSpinner.text == "Select") {
            alertErrorDialog("Please select the role")
            return false
        } else if (checkSkill()) {
            alertErrorDialog("Please select the skills")
            return false
        } else if (binding.etGoodToHave.text.toString().isEmpty()) {
            alertErrorDialog("Please fill the good to have")
            return false
        } else if (binding.etDescription.text.toString().isEmpty()) {
            alertErrorDialog("Please fill the description")
            return false
        } else if (binding.tvLastDateToApply.text.toString().isEmpty()) {
            alertErrorDialog("Please fill the last date to apply")
            return false
        } else {
            return true
        }
    }


    private fun setScreen() {
        binding.etTitle.setText(postJobSessionManager.getTitle())
        binding.etNoOfVacancy.setText(postJobSessionManager.getNoOFVacancy())
        binding.etGoodToHave.setText(postJobSessionManager.getGoodToHave())
        binding.etDescription.setText(postJobSessionManager.getDescription())
        binding.tvLastDateToApply.text = postJobSessionManager.getLastDateToApply()
    }

    private fun updateProgress() {
        binding.circle1.isSelected = true
        binding.circle2.isSelected = false
        binding.circle3.isSelected = false
        binding.circle4.isSelected = false
        binding.CVprogress.max = 100
        binding.CVprogress.progress = 0
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getSkillList() {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            postJobViewModel.getSkillList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE

                    val jsonObjectData = checkResponse(jsonObject)

                    if (jsonObjectData != null) {

                        try {
                            val skill = Gson().fromJson(jsonObjectData, SkillData::class.java)
                            val skillList = skill.data


                            if (postJobSessionManager.getSkill() != null) {
                                if (postJobSessionManager.getSkill()!!.isNotEmpty()) {
                                    postJobSessionManager.getSkill()!!.forEach { selectSkill ->
                                        skillList.find { it.id == selectSkill }?.apply {
                                            showSkillList.add(
                                                PathDataModel(
                                                    id,
                                                    skill_name
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            pathAdapter.notifyDataSetChanged()


                            val companyNameList: List<String> = skillList.map { it.skill_name }

                            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                                requireContext(), // or just 'this' if in an Activity
                                android.R.layout.simple_dropdown_item_1line,
                                companyNameList
                            )

                            binding.etSkillSearch.setAdapter(adapter)

                            binding.etSkillSearch.setOnItemClickListener { parent, _, position, _ ->

                                val selectedItem = parent.getItemAtPosition(position).toString()
                                var id: String? = null
                                var alreadyExist: Boolean = false

                                showSkillList.forEach { it ->
                                    if (it.pathName == selectedItem) {
                                        alreadyExist = true
                                    }
                                }

                                binding.etSkillSearch.text = null

                                if (!alreadyExist) {
                                    skillList.forEach { it ->
                                        if (it.skill_name == selectedItem) {
                                            id = it.id
                                        }
                                    }
                                    showSkillList.add(
                                        PathDataModel(
                                            id!!,
                                            selectedItem,
                                        )
                                    )

                                }


                                pathAdapter.notifyDataSetChanged()
                            }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }

                    }


                }
        }

    }

    private fun checkSkill(): Boolean {
        for (skill in showSkillList) {
            selectedSkill.add(skill.id)
        }

        return selectedSkill.isEmpty()
    }


    private fun getAllFilterList() {
        lifecycleScope.launch {
            postJobViewModel.getFilterDataList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, AllFilterData::class.java)

                            jobLabelSpinnerList(allFilterList.data.job_labels)


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun jobLabelSpinnerList(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label.toString() })




        binding.jobLabelSpinner.setOnClickListener() {
            searchableAlertDialog(
                dataList,
                "Select the Job Label ",
                requireContext()
            ) { selectedItem, _ ->

                allFilterList.map {
                    if (selectedItem == it.label) {
                        jobLable = it.value
                    }
                }

                binding.jobLabelSpinner.text = selectedItem
            }

        }



        allFilterList.map {
            if (it.value == postJobSessionManager.getSalaryRange()) {
                jobLable = it.value
                binding.jobLabelSpinner.text = it.label
            }
        }

    }


    private fun getIndustryList() {
        var start = 0

        lifecycleScope.launch {
            postJobViewModel.getIndustryList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, IndustryDataModel::class.java)

                            val dataList = mutableListOf("Select")
                            dataList.addAll(allFilterList.data.map { it.industry_name })

                            val baseFragment = BaseFragment()

                            binding.industrySpinner.setOnClickListener() {
                                baseFragment.searchableAlertDialog(
                                    dataList,
                                    "Select Industry",
                                    requireContext()
                                ) { selectedItem, position ->

                                    allFilterList.data.map {
                                        if (selectedItem == it.industry_name) {
                                            industry = it.id
                                            getIndustryDepartmentList(
                                                industry!!, 0
                                            )
                                        }
                                    }

                                    binding.industrySpinner.text = selectedItem
                                }
                            }


                            allFilterList.data.map {
                                if (it.industry_name ==  postJobSessionManager.getIndustryLabel()) {
                                    industry = it.id
                                    binding.industrySpinner.text = it.industry_name
                                    getIndustryDepartmentList(
                                        it.id,
                                        1
                                    )
                                }
                            }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun getIndustryDepartmentList(industryId: String, start: Int) {
        var start1 = 1
        lifecycleScope.launch {
            postJobViewModel.getIndustryDepartmentList(sessionManager.getBearerToken(), industryId)
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(
                                    jsonObjectData,
                                    IndustryDepartmentDataModel::class.java
                                )

                            val dataList = mutableListOf("Select")
                            dataList.addAll(allFilterList.data.map { it.department_name })


                            binding.departmentSpinner.setOnClickListener() {
                                BaseFragment().searchableAlertDialog(
                                    dataList,
                                    "Select Department",
                                    requireContext()
                                ) { selectedItem, position ->

                                    allFilterList.data.map {
                                        if (selectedItem == it.department_name) {
                                            department = it.id
                                            getIndustryDepartmentRoleList(
                                                department!!, 0
                                            )
                                        }
                                    }

                                    binding.departmentSpinner.text = selectedItem
                                }
                            }


                            if (start == 1) {
                                allFilterList.data.map {
                                    if (it.department_name == postJobSessionManager.getDepartmentLabel()) {
                                        department = it.id
                                        binding.departmentSpinner.text = it.department_name
                                        getIndustryDepartmentRoleList(
                                            it.id,
                                            1
                                        )
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun getIndustryDepartmentRoleList(departmentId: String, start: Int) {
        lifecycleScope.launch {
            postJobViewModel.getIndustryDepartmentRoleList(
                sessionManager.getBearerToken(),
                departmentId
            )
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(
                                    jsonObjectData,
                                    IndustryDepartmentRoleDataModel::class.java
                                )

                            val dataList = mutableListOf("Select")
                            dataList.addAll(allFilterList.data.map { it.role })


                            binding.roleSpinner.setOnClickListener() {
                                BaseFragment().searchableAlertDialog(
                                    dataList,
                                    "Select Role",
                                    requireContext()
                                ) { selectedItem, position ->

                                    allFilterList.data.map {
                                        if (selectedItem == it.role) {
                                            role = it.id
                                        }
                                    }

                                    binding.roleSpinner.text = selectedItem
                                }

                            }

                            if (start == 1) {
                                allFilterList.data.map {
                                    if (it.role == postJobSessionManager.getRoleLabel()) {
                                        role = it.id
                                        binding.roleSpinner.text = it.role
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }


}