package tech.merajobs.fragment.findJobScreens.myProfile.profileEdit

import android.annotation.SuppressLint
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import tech.merajobs.dataModel.AllFilterData
import tech.merajobs.dataModel.CourseData
import tech.merajobs.dataModel.CourseTypeData
import tech.merajobs.dataModel.EducationData
import tech.merajobs.dataModel.GradeSystemData
import tech.merajobs.dataModel.QualificationData
import tech.merajobs.dataModel.SpecializationData
import tech.merajobs.dataModel.UniversityData
import tech.merajobs.databinding.BottomSheetEducationEditDialogBinding
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager
import java.util.Calendar
import java.util.Locale

class BottomSheetEducationEditDialog(
    private var educationData: EducationData?,
    private var sourceFragment: String,
    private val callback: OnRequestAction?,
) :
    BottomSheetDialogFragment(),
    View.OnClickListener {

    lateinit var binding: BottomSheetEducationEditDialogBinding
    lateinit var sessionManager: SessionManager
    lateinit var profileViewModel: ProfileViewModel
    lateinit var baseFragment: BaseFragment


    private var qualification: String? = null
    private var course: String? = null
    private var specialoization: String? = null
    private var gradeSystem: String? = null
    private var courseType: String? = null
    private var selectJoiningYear: String? = null
    private var selectExitYear: String? = null
    private var university: String? = null

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
        binding = BottomSheetEducationEditDialogBinding.inflate(
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
        baseFragment = BaseFragment()


        binding.btnClose.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        binding.btCloseFilter.setOnClickListener(this)

        if (educationData != null) {
            binding.etGrade.setText(educationData!!.grade)
        }

        getAllFilterList()
        getQualificationList()
        getCourseTypeList()
        setYearSpinner()
        getGradeSystemSpinner()
        getUniversityList()
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
                if (checkValidation()) {
                    if (educationData != null) {
                        updateEducation()
                    } else {
                        addEducation()
                    }
                }
            }

        }
    }

    private fun checkValidation(): Boolean {

        if (binding.qualificationSpinner.text == null || binding.qualificationSpinner.text == "Select") {
            alertErrorDialog("Select the qualification")
            return false
        } else if (course == null || binding.courseSpinner.text == "Select") {
            alertErrorDialog("Select the Course")
            return false
        } else if (specialoization == null || binding.specializationSpinner.text == "Select") {
            alertErrorDialog("Select the Specialization")
            return false
        } else if (courseType == null || binding.courseSpinner.text == "Select") {
            alertErrorDialog("Select the Course Type")
            return false
        } else if (selectJoiningYear == null || binding.joiningYearSpinner.text == "Select") {
            alertErrorDialog("Select the From Year")
            return false
        } else if (selectExitYear == null || binding.exitYearSpinner0.text == "Select") {
            alertErrorDialog("Select the To Year")
            return false
        }else if (selectJoiningYear!!.toInt() >  selectExitYear!!.toInt()) {
            alertErrorDialog("Select the valid course duration")
            return false
        } else if (gradeSystem == null ) {
            alertErrorDialog("Select the Grading System")
            return false
        } else if (university == null || binding.universitySpinner.text == "Select") {
            alertErrorDialog("Select the University")
            return false
        } else if (binding.etGrade.text.isEmpty()) {
            alertErrorDialog("Fill the grade")
            return false
        } else {
            return true
        }
    }

    private fun addEducation() {
        lifecycleScope.launch {
            profileViewModel.addEducation(
                sessionManager.getBearerToken(),
                qualification!!,
                course!!,
                specialoization!!,
                courseType!!,
                university!!,
                selectJoiningYear!!,
                selectExitYear!!,
                gradeSystem!!,
                binding.etGrade.text.toString(),
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

    private fun updateEducation() {
        lifecycleScope.launch {
            profileViewModel.updateEducation(
                sessionManager.getBearerToken(),
                educationData!!.id,
                qualification!!,
                course!!,
                specialoization!!,
                courseType!!,
                university!!,
                selectJoiningYear!!,
                selectExitYear!!,
                gradeSystem!!,
                binding.etGrade.text.toString(),
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

    // This Function is used for get All Filter List from database
    private fun getAllFilterList() {
        lifecycleScope.launch {
            profileViewModel.getFilterDataList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = baseFragment.checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, AllFilterData::class.java)


                        } catch (e: Exception) {
                            baseFragment.alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun getQualificationList() {
        var start = 0

        lifecycleScope.launch {
            profileViewModel.getQualificationList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, QualificationData::class.java)

                            val dataList = mutableListOf("Select")
                            dataList.addAll(allFilterList.data.map { it.qualification })


                            binding.qualificationSpinner.text = "Select"

                            binding.qualificationSpinner.setOnClickListener() {
                                BaseFragment().searchableAlertDialog(
                                    dataList, "Select Qualification", requireContext()
                                ) { selectedItem, _ ->
                                    allFilterList.data.map {
                                        if (selectedItem == it.qualification) {
                                            qualification = it.id
                                            getCourseList(
                                                qualification!!, 0
                                            )
                                        }
                                    }
                                    binding.qualificationSpinner.text = selectedItem
                                }
                            }

                            if (educationData != null) {
                                allFilterList.data.map {
                                    if (it.qualification == educationData!!.qualification_name) {
                                        qualification = it.id
                                        binding.qualificationSpinner.text = it.qualification
                                        getCourseList(
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

    private fun getCourseList(qualificationId: String, start: Int) {
        var start1 = 0
        lifecycleScope.launch {
            profileViewModel.getCourseListList(sessionManager.getBearerToken(), qualificationId)
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, CourseData::class.java)

                            val dataList = mutableListOf("Select")
                            dataList.addAll(allFilterList.data.map { it.name })

                            binding.courseSpinner.text = "Select"


                            binding.courseSpinner.setOnClickListener() {
                                BaseFragment().searchableAlertDialog(
                                    dataList, "Select Course", requireContext()
                                ) { selectedItem, position ->
                                    allFilterList.data.map {
                                        if (selectedItem == it.name) {
                                            course = it.id
                                            getSpecializationList(
                                                course!!, 0
                                            )
                                        }
                                    }
                                    binding.courseSpinner.text = selectedItem
                                }
                            }

                            if (start == 1 && educationData != null) {
                                allFilterList.data.map {
                                    if (it.name == educationData!!.course_name) {
                                        course = it.id
                                        binding.courseSpinner.text = it.name
                                        getSpecializationList(
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

    private fun getSpecializationList(courseId: String, start: Int) {
        lifecycleScope.launch {
            profileViewModel.getSpecializationList(sessionManager.getBearerToken(), courseId)
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, SpecializationData::class.java)

                            val dataList = mutableListOf("Select")
                            dataList.addAll(allFilterList.data.map { it.specialization })

                            binding.specializationSpinner.text = "Select"

                            binding.specializationSpinner.setOnClickListener() {
                                BaseFragment().searchableAlertDialog(
                                    dataList, "Select Specialization", requireContext()
                                ) { selectedItem, _ ->
                                    allFilterList.data.map {
                                        if (selectedItem == it.specialization) {
                                            specialoization = it.id
                                        }
                                    }
                                    binding.specializationSpinner.text = selectedItem
                                }
                            }


                            if (start == 1 && educationData != null) {
                                allFilterList.data.map {
                                    if (it.specialization == educationData!!.specialization) {
                                        specialoization = it.id
                                        binding.specializationSpinner.text = it.specialization
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

    private fun getCourseTypeList() {
        lifecycleScope.launch {
            profileViewModel.getCourseTypeList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, CourseTypeData::class.java)

                            val dataList = mutableListOf("Select")
                            dataList.addAll(allFilterList.data.map { it.name })

                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                dataList
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.courseTypeSpinnerSpinner.adapter = adapter
                            binding.courseTypeSpinnerSpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View?,
                                        position: Int,
                                        id: Long,
                                    ) {
                                        courseType = if (position > 0) {
                                            allFilterList.data[position - 1].id
                                        } else {
                                            null
                                        }

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {
                                        // Do nothing
                                    }
                                }

                            if (educationData != null) {
                                val defaultIndex =
                                    dataList.indexOf(educationData!!.course_type_label)
                                Log.e("Position", defaultIndex.toString())
                                if (defaultIndex != -1) {
                                    binding.courseTypeSpinnerSpinner.setSelection(defaultIndex)
                                    courseType = if (defaultIndex > 0) {
                                        allFilterList.data[defaultIndex - 1].id
                                    } else {
                                        null
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

    private fun getGradeSystemSpinner() {
        lifecycleScope.launch {
            profileViewModel.getGradeSystemSpinner(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, GradeSystemData::class.java)

                            val dataList = mutableListOf("Select")
                            dataList.addAll(allFilterList.data.map { it.name })

                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                dataList
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.gradeSystemSpinner.adapter = adapter
                            binding.gradeSystemSpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View?,
                                        position: Int,
                                        id: Long,
                                    ) {
                                        gradeSystem = if (position > 0) {
                                            allFilterList.data[position - 1].id
                                        } else {
                                            null
                                        }

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {
                                        // Do nothing
                                    }
                                }

                            if (educationData != null) {
                                val defaultIndex =
                                    dataList.indexOf(educationData!!.grading_system_lable)
                                Log.e("Position", defaultIndex.toString())
                                if (defaultIndex != -1) {
                                    binding.gradeSystemSpinner.setSelection(defaultIndex)
                                    gradeSystem = if (defaultIndex > 0) {
                                        allFilterList.data[defaultIndex - 1].id
                                    } else {
                                        null
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

    private fun getUniversityList() {
        lifecycleScope.launch {
            profileViewModel.getUniversityList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, UniversityData::class.java)

                            val dataList = mutableListOf("Select")
                            dataList.addAll(allFilterList.data.map { it.university })


                            binding.universitySpinner.setOnClickListener() {
                                BaseFragment().searchableAlertDialog(
                                    dataList, "Select University", requireContext()
                                ) { selectedItem, _ ->
                                    allFilterList.data.map {
                                        if (selectedItem == it.university) {
                                            university = it.id
                                        }
                                    }
                                    binding.universitySpinner.text = selectedItem
                                }
                            }

                            if (educationData != null) {
                                allFilterList.data.map {
                                    if (it.university == educationData!!.university_name) {
                                        university = it.id
                                        binding.universitySpinner.text = it.university
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

    private fun setYearSpinner() {
        val spinnerList = mutableListOf("Select")
        spinnerList.addAll(getLast50Years())


        binding.joiningYearSpinner.setOnClickListener() {
            BaseFragment().searchableAlertDialog(
                spinnerList, "Select Joining Year", requireContext()
            ) { selectedItem, position ->
                selectJoiningYear = if (position > 0) {
                    spinnerList[position - 1]
                } else {
                    null
                }

                binding.joiningYearSpinner.text = selectedItem
            }
        }

        if (educationData != null) {
            spinnerList.map {
                if (it == educationData!!.from_year.toString()) {
                    selectJoiningYear = it
                    binding.joiningYearSpinner.text = it
                }
            }
        }



        binding.exitYearSpinner0.setOnClickListener() {
            BaseFragment().searchableAlertDialog(
                spinnerList, "Select Exit Year", requireContext()
            ) { selectedItem, position ->
                selectExitYear = if (position > 0) {
                    spinnerList[position - 1]
                } else {
                    null
                }

                binding.exitYearSpinner0.text = selectedItem
            }
        }

        if (educationData != null) {
            spinnerList.map {
                if (it == educationData!!.to_year.toString()) {
                    selectExitYear = it
                    binding.exitYearSpinner0.text = it
                }
            }
        }

    }

    private fun getLast50Years(): ArrayList<String> {
        val yearList = ArrayList<String>()

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)

        for (year in currentYear downTo currentYear - 49) {
            yearList.add(year.toString())
        }

        return yearList
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

}