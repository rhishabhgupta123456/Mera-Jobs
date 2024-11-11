package tech.merajobs.fragment.employerScreen.home.branch

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
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.dataModel.BranchList
import tech.merajobs.dataModel.CityDataModel
import tech.merajobs.dataModel.CountryDataModel
import tech.merajobs.dataModel.CountryList
import tech.merajobs.dataModel.StateDataModel
import tech.merajobs.databinding.BottomSheetBranchEditDialogBinding
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.businessViewModel.BusinessViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class BottomSheetBranchEditDialog(
    private val branchList: BranchList?,
    private val callback: OnRequestAction?
) : BottomSheetDialogFragment(), View.OnClickListener {

    lateinit var binding: BottomSheetBranchEditDialogBinding
    lateinit var sessionManager: SessionManager
    lateinit var businessViewModel: BusinessViewModel

    var selectCityId: Int? = null
    var selectedStateId: Int? = null
    var selectedCountryId: Int? = null

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
        binding = BottomSheetBranchEditDialogBinding.inflate(
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
        businessViewModel = ViewModelProvider(this)[BusinessViewModel::class.java]



        if (branchList != null) {
            binding.etBranchName.setText(branchList.branch_name)
            binding.etAddress.setText(branchList.address)
            binding.etZIPCode.setText(branchList.zip)

        }

        binding.btnClose.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        binding.btCloseFilter.setOnClickListener(this)

        getCountryListFromDataBase()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btnClose -> {
                dismiss()
            }

            R.id.btCloseFilter -> {
                dismiss()
            }


            R.id.btnSaveChange -> {
                dismiss()
                if (checkValidation()) {
                    if (branchList != null) {
                        updateBranch()
                    } else {
                        addBranch()
                    }

                }
            }
        }
    }

    private fun updateBranch() {
        lifecycleScope.launch {
            businessViewModel.updateBranch(
                sessionManager.getBearerToken(),
                branchList!!.id.toInt(),
                binding.etBranchName.text.toString(),
                binding.etAddress.text.toString(),
                binding.etZIPCode.text.toString().toInt(),
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

    private fun addBranch() {
        lifecycleScope.launch {
            businessViewModel.addBranch(
                sessionManager.getBearerToken(),
                binding.etBranchName.text.toString(),
                binding.etAddress.text.toString(),
                binding.etZIPCode.text.toString().toInt(),
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
        if (selectedCountryId == null) {
            alertErrorDialog("Please select the country!")
            return false
        } else if (selectedStateId == null) {
            alertErrorDialog("Please select the state!")
            return false
        } else if (selectCityId == null) {
            alertErrorDialog("Please select the city!")
            return false
        } else if (binding.etAddress.text.isEmpty()) {
            alertErrorDialog("Please fill the address!")
            return false
        } else if (binding.etBranchName.text.isEmpty()) {
            alertErrorDialog("Please fill the branch name!")
            return false
        } else if (binding.etZIPCode.text.isEmpty()) {
            alertErrorDialog("Please fill the zip code!")
            return false
        } else {
            return true
        }
    }

    private fun getCountryListFromDataBase() {

        lifecycleScope.launch {
            businessViewModel.getAllCountry(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->

                    val jsonObjectData = checkResponse(jsonObject)

                    if (jsonObjectData != null) {

                        try {
                            val country =
                                Gson().fromJson(jsonObjectData, CountryDataModel::class.java)

                            val countryList = country.data

                            setCountrySpinner(countryList)


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

        if (branchList != null){
            countryList.map {
                if (it.country_name == branchList.country_name) {
                    selectedCountryId = it.id.toInt()
                    binding.countrySpinner.text = it.country_name
                    getStateList(
                        it.id, 1
                    )
                }
            }
        }



    }

    private fun getStateList(countryId: String, first: Int) {
        var start = 0

        lifecycleScope.launch {

            businessViewModel.getStateList(sessionManager.getBearerToken(), countryId)
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

                            if (branchList != null){
                                if (first == 1) {
                                    stateList.data.map {
                                        if (it.state_name == branchList.state_name) {
                                            selectedStateId = it.id.toInt()
                                            binding.spinnerState.text = it.state_name
                                            getCityList(
                                                it.id, 1
                                            )
                                        }
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

            businessViewModel.getCityList(sessionManager.getBearerToken(), stateId)
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

                            if (branchList != null){
                                if (start == 1 ) {
                                    cityList.data.map {
                                        if (it.city_name == branchList.city_name) {
                                            selectCityId = it.id.toInt()
                                            binding.spinnerCity.text = it.city_name
                                        }
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