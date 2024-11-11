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
import tech.merajobs.dataModel.ITSkillsData
import tech.merajobs.dataModel.SkillData
import tech.merajobs.databinding.BottomSheetITSkillEditDialogBinding
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager
import java.util.Calendar
import java.util.Locale

class BottomSheetITSkillEditDialog(
    private val itSkillsData: ITSkillsData?,
    private var sourceFragment: String,
    private val callback: OnRequestAction?,
) :
    BottomSheetDialogFragment(), View.OnClickListener {
    lateinit var binding: BottomSheetITSkillEditDialogBinding
    lateinit var sessionManager: SessionManager
    private lateinit var profileViewModel: ProfileViewModel

    var skillName: String? = null
    var lastYear: String? = null
    var expYear: String? = null
    var expMonth: String? = null

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
        binding = BottomSheetITSkillEditDialogBinding.inflate(
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


        Log.e("itSkillsData *********** ", itSkillsData.toString())

        if (itSkillsData != null) {
            binding.tvSoftwareVirson.setText(itSkillsData.version)
        }

        binding.btnClose.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        binding.btCloseFilter.setOnClickListener(this)

        getSkills()
        setYearSpinner()
        setExperienceYearSpinner()
        setExperienceMonthSpinner()
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
                    if (itSkillsData != null) {
                        updateSkill()
                    } else {
                        addSkill()
                    }
                }
            }

        }
    }

    private fun updateSkill() {
        lifecycleScope.launch {
            profileViewModel.updateSkills(
                sessionManager.getBearerToken(),
                itSkillsData!!.id.toInt(),
                skillName!!,
                binding.tvSoftwareVirson.text.toString(),
                expYear!!,
                expMonth!!,
                lastYear!!,
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

    private fun addSkill() {
        lifecycleScope.launch {
            profileViewModel.addSkills(
                sessionManager.getBearerToken(),
                skillName!!,
                binding.tvSoftwareVirson.text.toString(),
                expYear!!,
                expMonth,
                lastYear!!,
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
        if (skillName == null || binding.skillSpinner.text == "Select") {
            alertErrorDialog("Select The Skill")
            return false
        } else if (binding.tvSoftwareVirson.text.isEmpty()) {
            alertErrorDialog("Fill The Software Version")
            return false
        } else if (lastYear == null ||  binding.lastYearSpinner.text == "Select") {
            alertErrorDialog("Select The Last Used")
            return false
        } else if (expYear == null) {
            alertErrorDialog("Select The Experience")
            return false
        } else {
            return true
        }

    }

    private fun getSkills() {
        lifecycleScope.launch {
            profileViewModel.getSkills(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)

                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, SkillData::class.java)

                            val dataList = mutableListOf("Select")
                            dataList.addAll(allFilterList.data.map { it.skill_name })

                            binding.skillSpinner.setOnClickListener() {
                                BaseFragment().searchableAlertDialog(
                                    dataList, "Select Skills", requireContext()
                                ) { selectedItem, _ ->
                                    allFilterList.data.map {
                                        if (selectedItem == it.skill_name) {
                                            skillName = it.id
                                        }
                                    }
                                    binding.skillSpinner.text = selectedItem
                                }
                            }

                            if (itSkillsData != null) {
                                allFilterList.data.map {
                                    if (it.skill_name == itSkillsData.skill_name) {
                                        skillName = it.id
                                        binding.skillSpinner.text = it.skill_name
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

        binding.lastYearSpinner.setOnClickListener() {
            BaseFragment().searchableAlertDialog(
                spinnerList, "Select Last Used", requireContext()
            ) { selectedItem, _ ->
                spinnerList.map {
                    if (selectedItem == it) {
                        lastYear = it
                    }
                }
                binding.lastYearSpinner.text = selectedItem
            }
        }

        if (itSkillsData != null) {
            spinnerList.map {
                if (it == itSkillsData.last_used_year) {
                    lastYear = it
                    binding.lastYearSpinner.text = itSkillsData.last_used_year
                }
            }
        }

    }

    private fun setExperienceYearSpinner() {
        val spinnerList = mutableListOf("Year")
        spinnerList.add("1")
        spinnerList.add("2")
        spinnerList.add("3")
        spinnerList.add("4")
        spinnerList.add("6")
        spinnerList.add("6")
        spinnerList.add("7")
        spinnerList.add("8")
        spinnerList.add("9")
        spinnerList.add("10+")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerList
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.expYearSpinner.adapter = adapter
        binding.expYearSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    expYear = if (position > 0) {
                        spinnerList[position]
                    } else {
                        null
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }

        if (itSkillsData != null) {
            val defaultIndex = spinnerList.indexOf(itSkillsData.experience_years)
            Log.e("Last Year Index", defaultIndex.toString())
            if (defaultIndex != -1) {
                binding.expYearSpinner.setSelection(defaultIndex)
                expYear = if (defaultIndex > 0) {
                    spinnerList[defaultIndex - 1]
                } else {
                    null
                }
            }
        }
    }

    private fun setExperienceMonthSpinner() {
        val spinnerList = mutableListOf("Month")
        spinnerList.add("1")
        spinnerList.add("2")
        spinnerList.add("3")
        spinnerList.add("4")
        spinnerList.add("6")
        spinnerList.add("6")
        spinnerList.add("7")
        spinnerList.add("8")
        spinnerList.add("9")
        spinnerList.add("10")
        spinnerList.add("11")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerList
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.expMonthSpinner.adapter = adapter
        binding.expMonthSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    expMonth = if (position > 0) {
                        spinnerList[position]
                    } else {
                        null
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }

        if (itSkillsData != null) {
            val defaultIndex = spinnerList.indexOf(itSkillsData.experience_months)
            Log.e("Last Year Index", defaultIndex.toString())
            if (defaultIndex != -1) {
                binding.expMonthSpinner.setSelection(defaultIndex)
                expMonth = if (defaultIndex > 0) {
                    spinnerList[defaultIndex - 1]
                } else {
                    null
                }
            }
        }
    }

    private fun getLast50Years(): ArrayList<String> {
        val yearList = ArrayList<String>()

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)

        for (year in currentYear downTo currentYear - 99) {
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