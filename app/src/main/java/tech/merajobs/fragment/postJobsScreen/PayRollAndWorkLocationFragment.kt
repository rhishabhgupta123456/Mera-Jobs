package tech.merajobs.fragment.postJobsScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.dataModel.AllFilterData
import tech.merajobs.dataModel.BranchDataModel
import tech.merajobs.dataModel.LocationDataModel
import tech.merajobs.dataModel.Model1
import tech.merajobs.databinding.FragmentPayRollAndWorkLocationBinding
import tech.merajobs.networkModel.postJobViewModel.PostJobViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.PostJobSessionManager
import tech.merajobs.utility.SessionManager

class PayRollAndWorkLocationFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentPayRollAndWorkLocationBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var postJobViewModel: PostJobViewModel
    private lateinit var postJobSessionManager: PostJobSessionManager

    private var salaryRange: String? = null
    private var salaryType: String? = null
    private var negotiable: String? = null
    private var negotiableLabel: String? = null
    private var currency: String? = null
    private var employmentType: String? = null
    private var location: String? = null
    private var workLocation: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPayRollAndWorkLocationBinding.inflate(
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
                    findNavController().navigate(R.id.openEligibilityFragment)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.btBack.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)




        updateProgress()
        getAllFilterList()
        getLocation()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btBack -> {
                findNavController().navigate(R.id.openEligibilityFragment)
            }

            R.id.btnClose -> {
                findNavController().navigate(R.id.openEligibilityFragment)
            }

            R.id.btnNext -> {
                if (checkValidation()) {
                    postJobSessionManager.setSalaryRange(salaryRange!!)
                    postJobSessionManager.setSalaryType(salaryType!!)
                    postJobSessionManager.setNegotiable(negotiable!!)
                    postJobSessionManager.setCurrency(currency!!)
                    postJobSessionManager.setEmploymentType(employmentType!!)
                    postJobSessionManager.setWorkLocationType(workLocation!!)
                    postJobSessionManager.setLocation(location!!)
                    postJobSessionManager.setSalaryRangeLabel(binding.salaryRangeSpinner.text.toString())
                    postJobSessionManager.setSalaryTypeLabel(binding.salaryTypeSpinner.text.toString())
                    postJobSessionManager.setNegotiableLabel(negotiableLabel!!)
                    postJobSessionManager.setCurrencyLabel(binding.currencySpinner.text.toString())
                    postJobSessionManager.setEmploymentTypeLabel(binding.employementTypeSpinner.text.toString())
                    postJobSessionManager.setWorkLocationTypeLabel(binding.workLocationSpinner.text.toString())
                    postJobSessionManager.setLocationLabel(binding.locationSpinner.text.toString())
                    findNavController().navigate(R.id.openJobPreviewFragment)
                }
            }
        }
    }

    private fun checkValidation(): Boolean {
        if (salaryRange == null) {
            alertErrorDialog("Please select salary range")
            return false
        } else if (salaryType == null) {
            alertErrorDialog("Please select salary type")
            return false
        } else if (negotiable == null && negotiableLabel == null) {
            alertErrorDialog("Please select negotiable")
            return false
        } else if (currency == null) {
            alertErrorDialog("Please select currency")
            return false
        } else if (employmentType == null) {
            alertErrorDialog("Please select employment type")
            return false
        } else if (workLocation == null) {
            alertErrorDialog("Please select work location")
            return false
        }else if (location == null) {
            alertErrorDialog("Please select location")
            return false
        } else {
            return true
        }

    }

    private fun updateProgress() {
        binding.circle1.isSelected = true
        binding.circle2.isSelected = true
        binding.circle3.isSelected = true
        binding.circle4.isSelected = false
        binding.CVprogress.max = 100
        binding.CVprogress.progress = 64
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

                            salaryRangeSpinnerList(allFilterList.data.salary_range)
                            salaryTypeSpinnerList(allFilterList.data.salary_type)
                            negotiableSpinnerList(allFilterList.data.yes_no)
                            currencySpinnerList(allFilterList.data.currency)
                            employmentTypeSpinnerList(allFilterList.data.employment_type)
                            workLocationTypeSpinnerList(allFilterList.data.work_location_type)


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun getLocation() {
        lifecycleScope.launch {
            postJobViewModel.getBranch(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, BranchDataModel::class.java)


                            val dataList = mutableListOf("Select")

                            if (allFilterList.data != null){
                                dataList.addAll(allFilterList.data.map { it.branch_name })

                                binding.locationSpinner.setOnClickListener() {
                                    searchableAlertDialog(
                                        dataList,
                                        "Select location",
                                        requireContext()
                                    ) { selectedItem, position ->

                                        allFilterList.data.map {
                                            if (selectedItem == it.branch_name) {
                                                location = it.id
                                            }
                                        }


                                        binding.locationSpinner.text = selectedItem
                                    }

                                }


                                allFilterList.data.map {
                                    if (it.id == postJobSessionManager.getLocation()) {
                                        location = it.id
                                        binding.locationSpinner.text = it.branch_name
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


    private fun salaryRangeSpinnerList(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label.toString() })




        binding.salaryRangeSpinner.setOnClickListener() {
            searchableAlertDialog(
                dataList,
                "Select Salary Range",
                requireContext()
            ) { selectedItem, _ ->

                allFilterList.map {
                    if (selectedItem == it.label) {
                        salaryRange = it.value
                    }
                }

                binding.salaryRangeSpinner.text = selectedItem
            }

        }



        allFilterList.map {
            if (it.value == postJobSessionManager.getSalaryRange()) {
                salaryRange = it.value
                binding.salaryRangeSpinner.text = it.label
            }
        }

    }

    private fun salaryTypeSpinnerList(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label.toString() })


        binding.salaryTypeSpinner.setOnClickListener() {
            searchableAlertDialog(
                dataList,
                "Select Salary Type",
                requireContext()
            ) { selectedItem, position ->

                allFilterList.map {
                    if (selectedItem == it.label) {
                        salaryType = it.value
                    }
                }

                binding.salaryTypeSpinner.text = selectedItem
            }
        }



        allFilterList.map {
            if (it.value == postJobSessionManager.getSalaryType()) {
                salaryType = it.value
                binding.salaryTypeSpinner.text = it.label
            }
        }


    }

    private fun currencySpinnerList(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label.toString() })


        binding.currencySpinner.setOnClickListener() {
            searchableAlertDialog(
                dataList,
                "Select Currency",
                requireContext()
            ) { selectedItem, position ->


                allFilterList.map {
                    if (selectedItem == it.label) {
                        currency = it.value
                    }
                }


                binding.currencySpinner.text = selectedItem
            }
        }


        allFilterList.map {
            if (it.value == postJobSessionManager.getCurrency()) {
                currency = it.value
                binding.currencySpinner.text = it.label
            }
        }

    }

    private fun employmentTypeSpinnerList(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label.toString() })


        binding.employementTypeSpinner.setOnClickListener() {
            searchableAlertDialog(
                dataList,
                "Select Employment Type",
                requireContext()
            ) { selectedItem, position ->


                allFilterList.map {
                    if (selectedItem == it.label) {
                        employmentType = it.value
                    }
                }

                binding.employementTypeSpinner.text = selectedItem
            }
        }


        allFilterList.map {
            if (it.value == postJobSessionManager.getEmploymentType()) {
                employmentType = it.value
                binding.employementTypeSpinner.text = it.label
            }
        }

    }

    private fun workLocationTypeSpinnerList(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label.toString() })


        binding.workLocationSpinner.setOnClickListener() {
            searchableAlertDialog(
                dataList,
                "Select Work Location",
                requireContext()
            ) { selectedItem, _ ->

                allFilterList.map {
                    if (selectedItem == it.label) {
                        workLocation = it.value
                    }
                }


                binding.workLocationSpinner.text = selectedItem
            }
        }


        allFilterList.map {
            if (it.value == postJobSessionManager.getWorkLocationType()) {
                workLocation = it.value
                binding.workLocationSpinner.text = it.label
            }
        }

    }

    private fun negotiableSpinnerList(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label.toString() })


        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dataList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.negotiableSpinner.adapter = adapter
        binding.negotiableSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    negotiable = if (position > 0) {
                        allFilterList[position - 1].value
                    } else {
                        null
                    }


                    negotiableLabel = if (position > 0) {
                        allFilterList[position - 1].label
                    } else {
                        null
                    }



                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }


        allFilterList.map {
            if (it.label == postJobSessionManager.getNegotiable()) {
                negotiable = it.value
                negotiableLabel = it.label
                val defaultIndex = dataList.indexOf(it.label)
                if (defaultIndex != -1) {
                    binding.negotiableSpinner.setSelection(defaultIndex)
                }
            }
        }
    }


}