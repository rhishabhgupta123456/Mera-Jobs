package tech.merajobs.fragment.createCVScreens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.CreateCVActivity
import tech.merajobs.adapter.PathAdapter
import tech.merajobs.adapter.SelectCountryAdapter
import tech.merajobs.dataModel.AllFilterData
import tech.merajobs.dataModel.CityDataModel
import tech.merajobs.dataModel.CountryDataModel
import tech.merajobs.dataModel.CountryList
import tech.merajobs.dataModel.Model1
import tech.merajobs.dataModel.PathDataModel
import tech.merajobs.dataModel.StateDataModel
import tech.merajobs.databinding.FragmentPersonalDetailsBinding
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PersonalDetailsFragment : BaseFragment() , View.OnClickListener{

    lateinit var binding: FragmentPersonalDetailsBinding

    lateinit var sessionManager: SessionManager
    private lateinit var profileViewModel: ProfileViewModel

    var showCountryList = ArrayList<PathDataModel>()
    lateinit var pathAdapter: PathAdapter

    private var getMaritalStatus: String = ""
    private var getCastCategory: String = ""
    private var getUsaWorkPermit: String = ""
    private var getSelectCountryList: JsonArray? = null
    private var getSelectedCountry: String = ""
    private var getSelectedState: String =""
    private var getSelectedCity: String =  ""


    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as CreateCVActivity
        activity.getProgressDetails(2)
        sessionManager.setSourceFragment("2")
        activity.showHideProgressbar(1)
    }


    var maritalStatus: Int? = null
    var castCategory: Int? = null
    var usaWorkPermit: Int? = null
    var selectCityId: Int? = null
    var selectedStateId: Int? = null
    var selectedCountryId: Int? = null


    private val selectedCountryList = ArrayList<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPersonalDetailsBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.openAddYourInformationScreen)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        sessionManager = SessionManager(requireContext())
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        pathAdapter = PathAdapter(showCountryList, requireContext())
        binding.selectCountryRecycleView.adapter = pathAdapter
        binding.selectCountryRecycleView.layoutManager =
            GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false)



        binding.btnClose.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        binding.etdob.setOnClickListener(this)


        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getUserProfile()
        }

    }


    override fun onClick(v: View?) {
        when (v!!.id) {


            R.id.btnClose -> {
                findNavController().navigate(R.id.openAddYourInformationScreen)
            }

            R.id.etdob -> {
                openDatePicker { selectedDate ->
                    binding.tvdob.text = selectedDate
                }
            }

            R.id.btnSaveChange -> {
                if (checkValidation()) {
                    if (!isNetworkAvailable()) {
                        alertErrorDialog(getString(R.string.no_internet))
                    } else {
                        updatePersonalInformation()
                    }
                }
            }
        }
    }

    // This Function is used for get user profile
    @SuppressLint("SetTextI18n")
    private fun getUserProfile() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            profileViewModel.getUserProfile(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            binding.tvdob.text =  checkFieldSting(jsonObjectData["data"].asJsonObject.get("dob"))
                            binding.etAddress.setText(checkFieldSting(jsonObjectData["data"].asJsonObject.get("address")))
                            binding.etZIPCode.setText(checkFieldSting(jsonObjectData["data"].asJsonObject.get("zip")))

                            getMaritalStatus =  checkFieldSting(jsonObjectData["data"].asJsonObject.get("marital_status_label"))
                            getCastCategory =  checkFieldSting(jsonObjectData["data"].asJsonObject.get("cast_category_label"))
                            getUsaWorkPermit =  checkFieldSting(jsonObjectData["data"].asJsonObject.get("usa_work_permit_label"))


                            val country =
                                checkFieldObject(jsonObjectData["data"].asJsonObject.get("country"))
                            if (country != null) {
                                getSelectedCountry = checkFieldSting(
                                    country.asJsonObject.get(
                                        "country_name"
                                    )
                                )
                            }

                            val state =
                                checkFieldObject(jsonObjectData["data"].asJsonObject.get("state"))
                            if (state != null) {
                                getSelectedState = checkFieldSting(
                                    state.asJsonObject.get(
                                        "state_name"
                                    )
                                )
                            }

                            val city =
                                checkFieldObject(jsonObjectData["data"].asJsonObject.get("city"))
                            if (city != null) {
                                getSelectedCity = checkFieldSting(
                                    city.asJsonObject.get(
                                        "city_name"
                                    )
                                )
                            }

                            getSelectCountryList =
                                checkFieldArray(jsonObjectData["data"].asJsonObject.get("other_countries_work_permit_label"))

                            if (!isNetworkAvailable()) {
                                alertErrorDialog(getString(R.string.no_internet))
                            } else {
                                getCountryListFromDataBase()
                                getAllFilterList()
                            }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }


    private fun updatePersonalInformation() {
        lifecycleScope.launch {
            profileViewModel.updatePersonalInformation(
                sessionManager.getBearerToken(),
                maritalStatus!!,
                usaWorkPermit!!,
                getSelectCountry(),
                binding.tvdob.text.toString(),
                binding.etAddress.text.toString(),
                binding.etZIPCode.text.toString().toInt(),
                castCategory!!,
                selectedCountryId!!,
                selectedStateId!!,
                selectCityId!!,
            )
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            Toast.makeText(
                                requireContext(),
                                jsonObjectData["message"].asJsonArray[0].asString,
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.openEmploymentHistoryFragment)

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    fun checkValidation(): Boolean {
        if (maritalStatus == null) {
            alertErrorDialog("Please select the marital status!")
            return false
        } else if (binding.tvdob.text.isEmpty()) {
            alertErrorDialog("Please fill the date of birth")
            return false
        } else if (binding.castCategorySpinner.text == "Select") {
            alertErrorDialog("Please select the cast category!")
            return false
        } else if (binding.maritalStatusSpinner.text == "Select") {
            alertErrorDialog("Please select the marital status!")
            return false
        } else if (binding.USAWorkPermitSpinner.text == "Select") {
            alertErrorDialog("Please select the USA work permit!")
            return false
        } else if (binding.countrySpinner.text == "Select") {
            alertErrorDialog("Please select the country!")
            return false
        } else if (binding.spinnerState.text == "Select") {
            alertErrorDialog("Please select the state!")
            return false
        } else if (binding.spinnerCity.text == "Select") {
            alertErrorDialog("Please select the city!")
            return false
        } else if (binding.etAddress.text.isEmpty()) {
            alertErrorDialog("Please fill the address!")
            return false
        } else if (binding.etZIPCode.text.isEmpty()) {
            alertErrorDialog("Please fill the zip code!")
            return false
        } else {
            return true
        }
    }

    // This Function is used for get All Filter List from database
    private fun getAllFilterList() {
        lifecycleScope.launch {
            profileViewModel.getFilterDataList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, AllFilterData::class.java)

                            setMaritalStatusSpinner(allFilterList.data.marital_status)
                            setCastCategorySpinner(allFilterList.data.cast_category)
                            setUsaWorkPermitSpinner(allFilterList.data.usa_work_permit)

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun setMaritalStatusSpinner(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label.toString() })


        binding.maritalStatusSpinner.setOnClickListener() {
            BaseFragment().searchableAlertDialog(
                dataList,
                "Select Marital Status",
                requireContext()
            ) { selectedItem, _ ->
                allFilterList.map {
                    if (selectedItem == it.label) {
                        maritalStatus = it.value.toInt()
                    }
                }
                binding.maritalStatusSpinner.text = selectedItem
            }

        }

        allFilterList.map {
            if (it.label == getMaritalStatus) {
                maritalStatus = it.value.toInt()
                binding.maritalStatusSpinner.text = it.label
            }
        }


    }

    private fun setCastCategorySpinner(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label.toString() })



        binding.castCategorySpinner.setOnClickListener() {
            BaseFragment().searchableAlertDialog(
                dataList,
                "Select Cast Category",
                requireContext()
            ) { selectedItem, _ ->
                allFilterList.map {
                    if (selectedItem == it.label) {
                        castCategory = it.value.toInt()
                    }
                }

                binding.castCategorySpinner.text = selectedItem
            }

        }

        allFilterList.map {
            if (it.label == getCastCategory) {
                castCategory = it.value.toInt()
                binding.castCategorySpinner.text = it.label
            }
        }


    }

    private fun setUsaWorkPermitSpinner(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label.toString() })


        binding.USAWorkPermitSpinner.setOnClickListener() {
            BaseFragment().searchableAlertDialog(
                dataList,
                "Select USA Work Permit",
                requireContext()
            ) { selectedItem, _ ->
                allFilterList.map {
                    if (selectedItem == it.label) {
                        usaWorkPermit = it.value.toInt()
                    }
                }

                binding.USAWorkPermitSpinner.text = selectedItem
            }

        }

        allFilterList.map {
            if (it.label == getUsaWorkPermit) {
                usaWorkPermit = it.value.toInt()
                binding.USAWorkPermitSpinner.text = it.label
            }
        }


    }

    private fun getSelectCountry(): List<Int> {
        for (country in showCountryList) {
            selectedCountryList.add(country.id.toInt())
        }

        return selectedCountryList
    }

    private fun getCountryListFromDataBase() {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.getAllCountry(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE

                    val jsonObjectData = checkResponse(jsonObject)

                    if (jsonObjectData != null) {

                        try {
                            val country =
                                Gson().fromJson(jsonObjectData, CountryDataModel::class.java)

                            val countryList = country.data

                            setCountrySpinner(countryList)

                            getSelectCountryList?.forEach { selectSkill ->
                                countryList.find { it.country_name == selectSkill.asString }
                                    ?.apply {
                                        showCountryList.add(
                                            PathDataModel(
                                                id,
                                                country_name
                                            )
                                        )
                                    }
                            }

                            pathAdapter.notifyDataSetChanged()


                            val companyNameList: List<String> = countryList.map { it.country_name }

                            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                                requireContext(), // or just 'this' if in an Activity
                                android.R.layout.simple_dropdown_item_1line,
                                companyNameList
                            )

                            binding.etCountrySearch.setAdapter(adapter)


                            binding.etCountrySearch.setOnItemClickListener { parent, _, position, _ ->

                                val selectedItem = parent.getItemAtPosition(position).toString()
                                var id: String? = null
                                var alreadyExist: Boolean = false

                                showCountryList.forEach { it ->
                                    if (it.pathName == selectedItem) {
                                        alreadyExist = true
                                    }
                                }

                                binding.etCountrySearch.text = null

                                if (!alreadyExist) {
                                    countryList.forEach { it ->
                                        if (it.country_name == selectedItem) {
                                            id = it.id
                                        }
                                    }
                                    showCountryList.add(
                                        PathDataModel(
                                            id!!,
                                            selectedItem,
                                        )
                                    )

                                }


                                pathAdapter.notifyDataSetChanged()
                            }

                            Log.e("Country List", countryList.toString())
                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }

                    }


                }
        }

    }

    private fun setCountrySpinner(countryList: ArrayList<CountryList>) {
        var start = 0
        val dataList = mutableListOf("Select")
        dataList.addAll(countryList.map { it.country_name.toString() })



        binding.countrySpinner.text = "Select"

        binding.countrySpinner.setOnClickListener() {
            BaseFragment().searchableAlertDialog(
                dataList, "Select Country", requireContext()
            ) { selectedItem, _ ->

                countryList.map {
                    if (selectedItem == it.country_name) {
                        selectedCountryId = it.id.toInt()
                        getStateList(
                            selectedCountryId.toString(), 0
                        )
                    }
                }

                binding.countrySpinner.text = selectedItem
            }
        }

        countryList.map {
            if (it.country_name == getSelectedCountry) {
                selectedCountryId = it.id.toInt()
                binding.countrySpinner.text = it.country_name
                getStateList(
                    it.id, 1
                )
            }
        }


    }

    private fun getStateList(countryId: String, first: Int) {
        var start = 0

        lifecycleScope.launch {

            profileViewModel.getStateList(sessionManager.getBearerToken(), countryId)
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val stateList =
                                Gson().fromJson(jsonObjectData, StateDataModel::class.java)

                            val stateNames = mutableListOf("Select")
                            stateNames.addAll(stateList.data.map { it.state_name.toString() })


                            binding.spinnerState.text = "Select"


                            binding.spinnerState.setOnClickListener() {
                                BaseFragment().searchableAlertDialog(
                                    stateNames, "Select State", requireContext()
                                ) { selectedItem, _ ->
                                    stateList.data.map {
                                        if (selectedItem == it.state_name) {
                                            selectedStateId = it.id.toInt()
                                            getCityList(
                                                selectedStateId.toString(), 0
                                            )
                                        }
                                    }
                                    binding.spinnerState.text = selectedItem
                                }
                            }

                            if (first == 1 && getSelectedState != "") {
                                stateList.data.map {
                                    if (it.state_name == getSelectedState) {
                                        selectedStateId = it.id.toInt()
                                        binding.spinnerState.text = it.state_name
                                        getCityList(
                                            it.id, 1
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

    private fun getCityList(stateId: String, start: Int) {
        lifecycleScope.launch {

            Log.e("State Id", stateId)

            profileViewModel.getCityList(sessionManager.getBearerToken(), stateId)
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val cityList =
                                Gson().fromJson(jsonObjectData, CityDataModel::class.java)


                            val cityNames = mutableListOf("Select")
                            cityNames.addAll(cityList.data.map { it.city_name.toString() })



                            binding.spinnerCity.text = "Select"

                            binding.spinnerCity.setOnClickListener() {
                                BaseFragment().searchableAlertDialog(
                                    cityNames, "Select City", requireContext()
                                ) { selectedItem, _ ->
                                    cityList.data.map {
                                        if (selectedItem == it.city_name) {
                                            selectCityId = it.id.toInt()
                                        }
                                    }
                                    binding.spinnerCity.text = selectedItem
                                }
                            }

                            if (start == 1 && getSelectedCity != "") {
                                cityList.data.map {
                                    if (it.city_name == getSelectedCity) {
                                        selectCityId = it.id.toInt()
                                        binding.spinnerCity.text = it.city_name
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



    // This Function is used for open date picker
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


}