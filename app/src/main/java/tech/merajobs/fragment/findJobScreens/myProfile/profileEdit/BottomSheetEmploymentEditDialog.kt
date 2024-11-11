package tech.merajobs.fragment.findJobScreens.myProfile.profileEdit

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.adapter.PathAdapter
import tech.merajobs.dataModel.AllFilterData
import tech.merajobs.dataModel.CompanyNameDataModel
import tech.merajobs.dataModel.Model1
import tech.merajobs.dataModel.PathDataModel
import tech.merajobs.dataModel.SkillData
import tech.merajobs.dataModel.WorkExperienceData
import tech.merajobs.databinding.BottomSheetEmploymentEditDialogBinding
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale


class BottomSheetEmploymentEditDialog(
    private var workExperienceData: WorkExperienceData?,
    private var sourceFragment : String,
    private val callback: OnRequestAction?
) :
    BottomSheetDialogFragment(), View.OnClickListener {

    lateinit var binding: BottomSheetEmploymentEditDialogBinding
    lateinit var sessionManager: SessionManager

    lateinit var profileViewModel: ProfileViewModel
    var currentlyWorking: Int? = 1
    var employementType: String? = null
    var noticePeriod: String? = null


    var showSkillList = ArrayList<PathDataModel>()
    lateinit var pathAdapter : PathAdapter

    val selectedSkill = ArrayList<Int>()

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
        binding = BottomSheetEmploymentEditDialogBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        sessionManager = SessionManager(requireContext())
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]


        pathAdapter = PathAdapter(showSkillList, requireContext())
        binding.selectCountryRecycleView.adapter = pathAdapter
        binding.selectCountryRecycleView.layoutManager =
            GridLayoutManager(requireContext(),3, LinearLayoutManager.VERTICAL, false)

        binding.btnClose.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        binding.btCloseFilter.setOnClickListener(this)
        binding.etJoiningDate.setOnClickListener(this)
        binding.etExitDate.setOnClickListener(this)

        if (workExperienceData != null) {
            binding.tvJoiningDate.text = workExperienceData!!.joning_date
            binding.tvExitDate.text = workExperienceData!!.exit_date
            binding.etJobTitle.setText(workExperienceData!!.job_title)
            binding.etCompanyName.setText(workExperienceData?.company?.company_name)
            binding.etJobProfile.setText(workExperienceData!!.job_profile)
        }

        binding.currentlyWorkingBtnGroup.setOnCheckedChangeListener { _, _ ->
            if (binding.btnYes.isChecked) {
                currentlyWorking = 1
                binding.lableExitYear.visibility = View.GONE
                binding.etExitDate.visibility = View.GONE
            } else {
                currentlyWorking = 0
                binding.lableExitYear.visibility = View.VISIBLE
                binding.etExitDate.visibility = View.VISIBLE
            }
        }

        setScreen()
        getSkillList()
        getAllFilterList()
        getCompanyNameList()
    }


    private fun getSkillList() {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.getSkillList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE

                    val jsonObjectData = checkResponse(jsonObject)

                    if (jsonObjectData != null) {

                        try {
                            val skill = Gson().fromJson(jsonObjectData, SkillData::class.java)
                            val skillList = skill.data

                            if (workExperienceData != null) {
                                if (workExperienceData!!.skills_label != null) {
                                    workExperienceData?.skills_label?.forEach { selectSkill ->
                                        skillList.find { it.skill_name == selectSkill }?.apply {
                                            showSkillList.add(
                                                PathDataModel(
                                                    id,
                                                    skill_name
                                                )
                                            )  }
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

                                if (!alreadyExist){
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


    private fun setScreen() {
        if (workExperienceData != null){
            if (workExperienceData!!.currently_working == "1") {
                currentlyWorking = 1
                binding.btnYes.isChecked = true
                binding.lableExitYear.visibility = View.GONE
                binding.etExitDate.visibility = View.GONE
            } else {
                currentlyWorking = 0
                binding.btnYes.isChecked = false
                binding.lableExitYear.visibility = View.VISIBLE
                binding.etExitDate.visibility = View.VISIBLE
            }
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

                            setEmploymentTypeSpinnerList(allFilterList)
                            setNoticePeriodSpinnerList(allFilterList.data.notice_period)

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun getCompanyNameList() {
        lifecycleScope.launch {
            profileViewModel.getCompanyNameList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, CompanyNameDataModel::class.java)

                            val companyNameList: List<String> =
                                allFilterList.data.map { it.company_name }

                            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                                requireContext(), // or just 'this' if in an Activity
                                android.R.layout.simple_dropdown_item_1line,
                                companyNameList
                            )

                            binding.etCompanyName.setAdapter(adapter)

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun setNoticePeriodSpinnerList(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label.toString() })

        binding.noticePeriodSpinner.setOnClickListener() {
            BaseFragment().searchableAlertDialog(
                dataList, "Select Notice Period", requireContext()
            ) { selectedItem, _ ->
                allFilterList.map {
                    if (selectedItem == it.label) {
                        noticePeriod = it.value
                    }
                }

                binding.noticePeriodSpinner.text = selectedItem
            }
        }

        if (workExperienceData != null){
            allFilterList.map {
                if (it.label == workExperienceData!!.notice_period_label ) {
                    noticePeriod = it.value
                    binding.noticePeriodSpinner.text = it.label
                }
            }
        }

    }

    private fun setEmploymentTypeSpinnerList(allFilterList: AllFilterData) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.data.employment_type.map { it.label.toString() })

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dataList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.employmentTypeSpinner.adapter = adapter
        binding.employmentTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    employementType = if (position > 0) {
                        allFilterList.data.employment_type[position - 1].value
                    } else {
                        ""
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }

        if (workExperienceData != null) {
            val defaultIndex =
                dataList.indexOf(workExperienceData!!.employment_type_label)
            if (defaultIndex != -1) {
                binding.employmentTypeSpinner.setSelection(defaultIndex)
                employementType = if (defaultIndex > 0) {
                    allFilterList.data.employment_type[defaultIndex - 1].value
                } else {
                    null
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btnClose -> {
                dismiss()
            }

            R.id.btCloseFilter -> {
                dismiss()
            }

            R.id.btnSaveChange -> {
                if (checkValidation()) {
                    if (workExperienceData != null) {
                        updateWorkExperience()
                    } else {
                        addWorkExperience()
                    }
                }

            }

            R.id.etJoiningDate -> {
                openDatePicker { selectedDate ->
                    binding.tvJoiningDate.text = selectedDate
                }
            }

            R.id.etExitDate -> {
                openDatePicker { selectedDate ->
                    binding.tvExitDate.text = selectedDate
                }
            }
        }
    }

    private fun checkValidation(): Boolean {
        if (binding.tvJoiningDate.text.isEmpty()) {
            alertErrorDialog(getString(R.string.select_joining_date))
            return false
        } else if (employementType == null) {
            alertErrorDialog(getString(R.string.select_the_employment_type))
            return false
        } else if (binding.tvExitDate.text.isEmpty() && currentlyWorking == 0) {
            alertErrorDialog(getString(R.string.select_exit_date))
            return false
        } else if (binding.etJobTitle.text.isEmpty()) {
            alertErrorDialog(getString(R.string.fill_the_job_title))
            return false
        } else if (checkSkill()) {
            alertErrorDialog(getString(R.string.fill_the_skills))
            return false
        } else if (binding.etCompanyName.text.isEmpty()) {
            alertErrorDialog(getString(R.string.fill_the_company_name))
            return false
        } else if (binding.etJobProfile.text.isEmpty()) {
            alertErrorDialog(getString(R.string.fill_the_job_profile))
            return false
        } else if (binding.noticePeriodSpinner.text == "Select") {
            alertErrorDialog(getString(R.string.select_the_notice_period))
            return false
        } else {
            return true
        }
    }

    private fun checkSkill(): Boolean {
        for (skill in showSkillList) {
            selectedSkill.add(skill.id.toInt())
        }

        return selectedSkill.isEmpty()
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

        Log.e("Error Message", msg.toString())
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addWorkExperience() {

        lifecycleScope.launch {
            profileViewModel.addWorkExperience(
                sessionManager.getBearerToken(),
                currentlyWorking.toString(),
                employementType!!,
                getTotalExperience(
                    binding.tvJoiningDate.text.toString(),
                    binding.tvExitDate.text.toString()
                ).first,
                getTotalExperience(
                    binding.tvJoiningDate.text.toString(),
                    binding.tvExitDate.text.toString()
                ).second,
                binding.etCompanyName.text.toString(),
                binding.etJobTitle.text.toString(),
                binding.tvJoiningDate.text.toString(),
                binding.tvExitDate.text.toString(),
                binding.etJobProfile.text.toString(),
                selectedSkill,
                noticePeriod!!
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateWorkExperience() {

        lifecycleScope.launch {
            profileViewModel.updateWorkExperience(
                sessionManager.getBearerToken(),
                workExperienceData!!.id.toInt(),
                currentlyWorking.toString(),
                employementType!!,
                getTotalExperience(
                    binding.tvJoiningDate.text.toString(),
                    binding.tvExitDate.text.toString()
                ).first,
                getTotalExperience(
                    binding.tvJoiningDate.text.toString(),
                    binding.tvExitDate.text.toString()
                ).second,
                binding.etCompanyName.text.toString(),
                binding.etJobTitle.text.toString(),
                binding.tvJoiningDate.text.toString(),
                binding.tvExitDate.text.toString(),
                binding.etJobProfile.text.toString(),
                selectedSkill,
                noticePeriod!!
            ).observe(viewLifecycleOwner) { jsonObject ->
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTotalExperience(joinDate: String, exitDate: String): Pair<String, String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val join = LocalDate.parse(joinDate, formatter)
        val exit =
            if (currentlyWorking == 1) LocalDate.now() else LocalDate.parse(exitDate, formatter)

        val years = ChronoUnit.YEARS.between(join, exit)
        val months = ChronoUnit.MONTHS.between(join, exit) % 12
        val days = ChronoUnit.DAYS.between(join, exit) % 30

        val yearAndMonth = Pair(years.toString(), months.toString())
        return yearAndMonth
    }

}