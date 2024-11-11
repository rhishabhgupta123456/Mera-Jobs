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
import tech.merajobs.dataModel.IndustryDataModel
import tech.merajobs.dataModel.IndustryDepartmentDataModel
import tech.merajobs.dataModel.IndustryDepartmentRoleDataModel
import tech.merajobs.dataModel.Model1
import tech.merajobs.databinding.BottomSheetJobPreferenceEditDialogBinding
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager
import java.util.Locale

class BottomSheetJobPreferenceEditDialogFragment(
    private var getIndustry: String,
    private var getDepartment: String,
    private var getRole: String,
    private var getDesiredJobType: String,
    private var getEmploymentType: String,
    private var expectedCtc: String,
    private val callback: OnRequestAction?,
) : BottomSheetDialogFragment(), View.OnClickListener {


    lateinit var binding: BottomSheetJobPreferenceEditDialogBinding
    lateinit var sessionManager: SessionManager
    private lateinit var profileViewModel: ProfileViewModel

    var role: String? = null
    var desiredJobType: String? = null
    var industry: String? = null
    var employmentType: String? = null
    var department: String? = null

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
        binding = BottomSheetJobPreferenceEditDialogBinding.inflate(
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



        binding.btnClose.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        binding.btCloseFilter.setOnClickListener(this)


        binding.etExpectedCTC.setText(expectedCtc)

        getAllFilterList()
        getIndustryList()
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
                    editJobPreference()
                }
            }

        }
    }

    private fun editJobPreference() {
        lifecycleScope.launch {
            profileViewModel.updateJobPreference(
                sessionManager.getBearerToken(),
                industry!!.toInt(),
                department!!.toInt(),
                role!!.toInt(),
                employmentType!!.toInt(),
                desiredJobType!!.toInt(),
                binding.etExpectedCTC.text.toString(),
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

    private fun checkValidation(): Boolean {

        if (industry == null) {
            alertErrorDialog("Select The Industry")
            return false
        } else if (binding.industrySpinner.text == "Select") {
            alertErrorDialog("Select The Industry")
            return false
        } else if (binding.departmentSpinner.text == "Select") {
            alertErrorDialog("Select The Department")
            return false
        } else if (binding.roleSpinner.text == "Select") {
            alertErrorDialog("Select The Role")
            return false
        } else if (binding.desiredJobTypeSpinner.text == "Select") {
            alertErrorDialog("Select The Desired Job Type")
            return false
        } else if (binding.employmentTypeSpinner.text == "Select") {
            alertErrorDialog("Select The Employment Type")
            return false
        } else if (binding.etExpectedCTC.text.isEmpty()) {
            alertErrorDialog("Fill the expected ctc")
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

                            setEmploymentTypeSpinnerList(allFilterList.data.employment_type)
                            setDesiredJobType(allFilterList.data.work_location_type)

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }


    private fun getIndustryList() {
        var start = 0

        lifecycleScope.launch {
            profileViewModel.getIndustryList(sessionManager.getBearerToken())
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
                                if (it.industry_name == getIndustry) {
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
            profileViewModel.getIndustryDepartmentList(sessionManager.getBearerToken(), industryId)
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
                                    if (it.department_name == getDepartment) {
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
            profileViewModel.getIndustryDepartmentRoleList(
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
                                    if (it.role == getRole) {
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

    private fun setDesiredJobType(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label })


        binding.desiredJobTypeSpinner.setOnClickListener() {
            BaseFragment().searchableAlertDialog(
                dataList,
                "Select Desired Job Type",
                requireContext()
            ) { selectedItem, position ->

                allFilterList.map {
                    if (selectedItem == it.label) {
                        desiredJobType = it.value
                    }
                }

                binding.desiredJobTypeSpinner.text = selectedItem
            }

        }


        allFilterList.map {
            if (it.label == getDesiredJobType) {
                desiredJobType = it.value
                binding.desiredJobTypeSpinner.text = it.label
            }
        }


    }

    private fun setEmploymentTypeSpinnerList(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label })

        binding.employmentTypeSpinner.setOnClickListener() {
            BaseFragment().searchableAlertDialog(
                dataList,
                "Select Employment",
                requireContext()
            ) { selectedItem, position ->

                allFilterList.map {
                    if (selectedItem == it.label) {
                        employmentType = it.value
                    }
                }


                binding.employmentTypeSpinner.text = selectedItem
            }

        }


        allFilterList.map {
            if (it.label == getEmploymentType) {
                employmentType = it.value
                binding.employmentTypeSpinner.text = it.label
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

}