package tech.merajobs.fragment.findJobScreens.myProfile.profileEdit

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.adapter.PathAdapter
import tech.merajobs.dataModel.AllFilterData
import tech.merajobs.dataModel.CityDataModel
import tech.merajobs.dataModel.CountryDataModel
import tech.merajobs.dataModel.CountryList
import tech.merajobs.dataModel.Model1
import tech.merajobs.dataModel.PathDataModel
import tech.merajobs.dataModel.StateDataModel
import tech.merajobs.databinding.BottomSheetPersonalDetailsEditDialogBinding
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BottomSheetPersonalDetailsEditDialog(
    val getMaritalStatus: String,
    val dob: String,
    val getCastCategory: String,
    val getUsaWorkPermit: String,
    val getSelectCountryList: JsonArray?,
    val getSelectedCountry: String,
    val getSelectedState: String,
    val getSelectedCity: String,
    val getAddress: String,
    val getZip: String,
    private val callback: OnRequestAction?,
) : BottomSheetDialogFragment(), View.OnClickListener {


    lateinit var binding: BottomSheetPersonalDetailsEditDialogBinding
    lateinit var sessionManager: SessionManager
    private lateinit var profileViewModel: ProfileViewModel

    var showCountryList = ArrayList<PathDataModel>()
    lateinit var pathAdapter: PathAdapter

    var maritalStatus: Int? = null
    var castCategory: Int? = null
    var usaWorkPermit: Int? = null
    var selectCityId: Int? = null
    var selectedStateId: Int? = null
    var selectedCountryId: Int? = null


    val selectedCountryList = ArrayList<Int>()

    interface OnRequestAction {
        fun refresh()
    }

    private fun notifyRefresh() {
        callback?.refresh()
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        notifyRefresh()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.background = resources.getDrawable(R.drawable.bottom_white_layout, null)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isHideable = false
            behavior.isDraggable = false
        }
        dialog.setCancelable(false)
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetPersonalDetailsEditDialogBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        sessionManager = SessionManager(requireContext())
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]


        pathAdapter = PathAdapter(showCountryList, requireContext())
        binding.selectCountryRecycleView.adapter = pathAdapter
        binding.selectCountryRecycleView.layoutManager =
            GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false)


        binding.tvDob.text = dob
        binding.etAddress.setText(getAddress)
        binding.etZIPCode.setText(getZip)

        binding.btnClose.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        binding.btCloseFilter.setOnClickListener(this)
        binding.etdob.setOnClickListener(this)

        getCountryListFromDataBase()
        getAllFilterList()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btnClose -> {
                dismiss()
            }

            R.id.btCloseFilter -> {
                dismiss()
            }

            R.id.etdob -> {
                openDatePicker { selectedDate ->
                    binding.tvDob.text = selectedDate
                }
            }

            R.id.btnSaveChange -> {
                if (checkValidation()) {
                    updatePersonalInformation()
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
                binding.tvDob.text.toString(),
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
                            dismiss()

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
        } else if (binding.tvDob.text.isEmpty()) {
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


    fun checkResponse(jsonObject: BaseResponse<JsonObject>): JsonObject? {
        if (!jsonObject.isIsError) {
            if (jsonObject.response != null) {
                try {
                    val jsonObjectData: JsonObject = jsonObject.response!!
                    val status = jsonObjectData["success"].asBoolean

                    if (status) {
                        return jsonObjectData
                    } else {
                        if (jsonObjectData["message"].asJsonArray != null && !jsonObjectData["message"].asJsonArray.isEmpty) {
                            alertErrorDialog(jsonObjectData["message"].asJsonArray[0].asString)
                        } else {
                            alertErrorDialog("No Error Message From Server!")
                        }
                    }
                } catch (e: Exception) {
                    alertErrorDialog("Exception : $e")
                }

            }
        } else {
            alertErrorDialog(jsonObject.message)
        }
        return null
    }

    fun alertErrorDialog(msg: String) {
        val alertErrorDialog = Dialog(requireContext())
        alertErrorDialog.setCancelable(false)
        alertErrorDialog.setContentView(R.layout.alertbox_error)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertErrorDialog.window!!.attributes)

        Log.e("Error Message", msg)
        alertErrorDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertErrorDialog.window!!.attributes = layoutParams

        val tvTitle: TextView = alertErrorDialog.findViewById(R.id.tv_title)
        val btnOk: TextView = alertErrorDialog.findViewById(R.id.btn_ok)
        tvTitle.text = msg.lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

        btnOk.setOnClickListener {
            alertErrorDialog.dismiss()
        }

        alertErrorDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertErrorDialog.show()
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
