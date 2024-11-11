package tech.merajobs.fragment.findJobScreens.home

import android.os.Bundle
import android.util.Log
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
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.dataModel.DepartmentRoleDataModel
import tech.merajobs.dataModel.EmploymentTypeDataModel
import tech.merajobs.dataModel.JobLabelDataModel
import tech.merajobs.dataModel.LocationDataModel
import tech.merajobs.dataModel.SalaryDataModel
import tech.merajobs.dataModel.SkillData
import tech.merajobs.dataModel.WorkLocationTypeDataModel
import tech.merajobs.databinding.FragmentFilterBinding
import tech.merajobs.networkModel.homeViewModel.HomeViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager

class FilterFragment : BaseFragment(), View.OnClickListener {


    lateinit var binding: FragmentFilterBinding

    lateinit var sessionManager: SessionManager
    lateinit var homeViewModel: HomeViewModel
    private var workLocationType: String = ""
    private var jobLoable: String = ""
    private var employmentType: String = ""
    private var location: String = ""
    private var role: String = ""
    private var skill: String = ""
    private var salaryRange: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFilterBinding.inflate(
            LayoutInflater.from(requireActivity()), container, false
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
                    if (requireArguments().getString(AppConstant.SOURCE_FRAGMENT) == AppConstant.HOME_SCREEN) {
                        findNavController().navigate(R.id.openHomeScreen)
                    } else if (requireArguments().getString(AppConstant.SOURCE_FRAGMENT) == AppConstant.SEARCH_SCREEN) {
                        findNavController().navigate(R.id.openHomeScreen)
                    } else {
                        findNavController().navigate(R.id.openHomeScreen)
                    }

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.btShowResult.setOnClickListener(this)
        binding.btClear.setOnClickListener(this)
        binding.btBack.setOnClickListener(this)

        getWorkLocationType()
        getJobLabel()
        getEmploymentType()
        getLocation()
        getDepartmentRole()
        getSkill()
        getSalaryRange()
    }


    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as FindJobActivity
        activity.showHideBottomMenu(false)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.btBack -> {
                if (requireArguments().getString(AppConstant.SOURCE_FRAGMENT) == AppConstant.HOME_SCREEN) {
                    findNavController().navigate(R.id.openHomeScreen)
                } else if (requireArguments().getString(AppConstant.SOURCE_FRAGMENT) == AppConstant.SEARCH_SCREEN) {
                    findNavController().navigate(R.id.openHomeScreen)
                } else {
                    findNavController().navigate(R.id.openHomeScreen)
                }
            }

            R.id.btShowResult -> {

                Log.e("WORK_LOCATION_TYPE", workLocationType)
                Log.e("JOB_LABLE", jobLoable)
                Log.e("employmentType", employmentType)
                Log.e("location", location)
                Log.e("role", role)
                Log.e("skill", skill)
                Log.e("SALARY_RANGE", salaryRange)

                val bundle = Bundle()
                bundle.putString(AppConstant.PAGE_TITLE, "Filter")
                bundle.putString(AppConstant.WORK_LOCATION_TYPE, workLocationType)
                bundle.putString(AppConstant.JOB_LABEL, jobLoable)
                bundle.putString(AppConstant.EMPLOYMENT_TYPE, employmentType)
                bundle.putString(AppConstant.LOCATION, location)
                bundle.putString(AppConstant.ROLE, role)
                bundle.putString(AppConstant.SKILL, skill)
                bundle.putString(AppConstant.SALARY_RANGE, salaryRange)
                findNavController().navigate(R.id.openSearchJobScreen, bundle)
            }

            R.id.btClear -> {
                try {
                    binding.workLocationTypeSpinner.setSelection(0)
                    binding.jobLableSpinner.setSelection(0)
                    binding.employmentSpinner.setSelection(0)
                    binding.locationSpinner.setSelection(0)
                    binding.roleSpinner.setSelection(0)
                    binding.skillSpinner.setSelection(0)
                    binding.salaryRangeSpinner.setSelection(0)

                }catch (e :Exception){
                    Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT).show()
                }
             }


        }
    }


    private fun getWorkLocationType() {
        lifecycleScope.launch {

            homeViewModel.getWorkLocationType(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(
                                    jsonObjectData,
                                    WorkLocationTypeDataModel::class.java
                                )


                            val list = mutableListOf("Select")
                            list.addAll(allFilterList.data.map { it.name.toString() })

                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                list
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.workLocationTypeSpinner.adapter = adapter
                            binding.workLocationTypeSpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View?,
                                        position: Int,
                                        id: Long,
                                    ) {
                                        workLocationType = if (position > 0) {
                                            allFilterList.data[position - 1].id
                                        } else {
                                            ""
                                        }

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {
                                        // Do nothing
                                    }
                                }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun getJobLabel() {
        lifecycleScope.launch {

            homeViewModel.getJobLabel(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(
                                    jsonObjectData,
                                    JobLabelDataModel::class.java
                                )


                            val list = mutableListOf("Select")
                            list.addAll(allFilterList.data.map { it.label.toString() })

                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                list
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.jobLableSpinner.adapter = adapter
                            binding.jobLableSpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View?,
                                        position: Int,
                                        id: Long,
                                    ) {
                                        jobLoable = if (position > 0) {
                                            allFilterList.data[position - 1].label
                                        } else {
                                            ""
                                        }

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {
                                        // Do nothing
                                    }
                                }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun getEmploymentType() {
        lifecycleScope.launch {

            homeViewModel.getEmploymentType(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(
                                    jsonObjectData,
                                    EmploymentTypeDataModel::class.java
                                )


                            val list = mutableListOf("Select")
                            list.addAll(allFilterList.data.map { it.name.toString() })

                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                list
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.employmentSpinner.adapter = adapter
                            binding.employmentSpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View?,
                                        position: Int,
                                        id: Long,
                                    ) {
                                        employmentType = if (position > 0) {
                                            allFilterList.data[position - 1].id
                                        } else {
                                            ""
                                        }

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {
                                        // Do nothing
                                    }
                                }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun getLocation() {
        lifecycleScope.launch {

            homeViewModel.getLocation(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(
                                    jsonObjectData,
                                    LocationDataModel::class.java
                                )


                            val list = mutableListOf("Select")
                            list.addAll(allFilterList.data.map { it.location.toString() })

                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                list
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.locationSpinner.adapter = adapter
                            binding.locationSpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View?,
                                        position: Int,
                                        id: Long,
                                    ) {
                                        location = if (position > 0) {
                                            allFilterList.data[position - 1].location_id
                                        } else {
                                            ""
                                        }

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {
                                        // Do nothing
                                    }
                                }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun getDepartmentRole() {
        lifecycleScope.launch {

            homeViewModel.getDepartmentRole(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(
                                    jsonObjectData,
                                    DepartmentRoleDataModel::class.java
                                )


                            val list = mutableListOf("Select")
                            list.addAll(allFilterList.data.map { it.role.toString() })

                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                list
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.roleSpinner.adapter = adapter
                            binding.roleSpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View?,
                                        position: Int,
                                        id: Long,
                                    ) {
                                        role = if (position > 0) {
                                            allFilterList.data[position - 1].role_id
                                        } else {
                                            ""
                                        }

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {
                                        // Do nothing
                                    }
                                }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun getSkill() {
        lifecycleScope.launch {

            homeViewModel.getSkill(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(
                                    jsonObjectData,
                                    SkillData::class.java
                                )


                            val list = mutableListOf("Select")
                            list.addAll(allFilterList.data.map { it.skill_name.toString() })

                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                list
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.skillSpinner.adapter = adapter
                            binding.skillSpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View?,
                                        position: Int,
                                        id: Long,
                                    ) {
                                        skill = if (position > 0) {
                                            allFilterList.data[position - 1].id
                                        } else {
                                            ""
                                        }

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {
                                        // Do nothing
                                    }
                                }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun getSalaryRange() {
        lifecycleScope.launch {

            homeViewModel.getSalaryRange(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(
                                    jsonObjectData,
                                    SalaryDataModel::class.java
                                )


                            val list = mutableListOf("Select")
                            list.addAll(allFilterList.data.map { it.name.toString() })

                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                list
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.salaryRangeSpinner.adapter = adapter
                            binding.salaryRangeSpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View?,
                                        position: Int,
                                        id: Long,
                                    ) {
                                        salaryRange = if (position > 0) {
                                            allFilterList.data[position - 1].id
                                        } else {
                                            ""
                                        }

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {
                                        // Do nothing
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