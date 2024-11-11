package tech.merajobs.fragment.findJobScreens.myProfile.profileEdit

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.dataModel.AllFilterData
import tech.merajobs.dataModel.LanguageData
import tech.merajobs.dataModel.LanguageListData
import tech.merajobs.dataModel.Model1
import tech.merajobs.databinding.BottomSheetLanguageEditDialogBinding
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager
import java.util.Locale


class BottomSheetLanguageEditDialog(
    private var languageData: LanguageData?,
    private var sourceFragment: String,
    private val callback: OnRequestAction?,
) :
    BottomSheetDialogFragment(), View.OnClickListener {
    lateinit var binding: BottomSheetLanguageEditDialogBinding
    lateinit var sessionManager: SessionManager
    private lateinit var profileViewModel: ProfileViewModel
    val baseFragment = BaseFragment()

    var langauge: Int? = null
    private var proficiency: Int? = null

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
        binding = BottomSheetLanguageEditDialogBinding.inflate(
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


        Log.e("languageData *********** ", languageData.toString())




        binding.btnClose.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        binding.btCloseFilter.setOnClickListener(this)

        if (languageData != null) {
            binding.btRead.isChecked = languageData!!.read == "1"
            binding.btWrite.isChecked = languageData!!.write == "1"
            binding.btSpeak.isChecked = languageData!!.speak == "1"
            binding.proficiencySpinner.text = languageData!!.proficiency_level
            binding.languageSpinner.text = languageData!!.proficiency_level
        }

        getLanguageList()
        getAllSpinner()
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
                    if (languageData != null) {
                        updateLanguage()
                    } else {
                        addLanguage()
                    }
                }
            }
        }
    }

    private fun updateLanguage() {
        lifecycleScope.launch {
            profileViewModel.updateLanguage(
                sessionManager.getBearerToken(),
                languageData!!.id.toInt(),
                langauge!!,
                proficiency!!,
                checkedButton(binding.btRead),
                checkedButton(binding.btSpeak),
                checkedButton(binding.btWrite),
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

    private fun addLanguage() {
        lifecycleScope.launch {
            profileViewModel.addLanguage(
                sessionManager.getBearerToken(),
                langauge!!,
                proficiency!!,
                checkedButton(binding.btRead),
                checkedButton(binding.btSpeak),
                checkedButton(binding.btWrite),
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

    private fun checkedButton(checkBox: CheckBox): Int {
        return if (checkBox.isChecked) {
            1
        } else {
            0
        }
    }

    private fun getAllSpinner() {
        lifecycleScope.launch {
            profileViewModel.getFilterDataList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)

                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, AllFilterData::class.java)

                            setLanguageProficiency(allFilterList.data.language_proficiency)


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }


    }

    private fun setLanguageProficiency(languageProficiency: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(languageProficiency.map { it.label })

        binding.proficiencySpinner.setOnClickListener() {
            baseFragment.searchableAlertDialog(
                dataList,
                "Select Proficiency",
                requireContext()
            ) { selectedItem, position ->
                languageProficiency.map {
                    if (selectedItem == it.label) {
                        proficiency = it.value.toInt()
                    }
                }

                binding.proficiencySpinner.text = selectedItem
            }
        }

        if (languageData != null) {
            languageProficiency.map {
                if (it.label == languageData!!.proficiency_level) {
                    proficiency = it.value.toInt()
                    binding.languageSpinner.text = languageData!!.proficiency_level
                }
            }
        }


    }


    private fun checkValidation(): Boolean {
        if (langauge == null || binding.languageSpinner.text == "Select") {
            alertErrorDialog("Select The Language")
            return false
        } else if (proficiency == null || binding.proficiencySpinner.text == "Select") {
            alertErrorDialog("Select The Proficiency")
            return false
        } else {
            return true
        }

    }

    private fun getLanguageList() {
        lifecycleScope.launch {
            profileViewModel.getLanguageList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)

                    if (jsonObjectData != null) {
                        try {
                            val languageList =
                                Gson().fromJson(jsonObjectData, LanguageListData::class.java)

                            val dataList = mutableListOf("Select")
                            dataList.addAll(languageList.data.map { it.language })

                            binding.languageSpinner.setOnClickListener() {
                                baseFragment.searchableAlertDialog(
                                    dataList,
                                    "Select Language",
                                    requireContext()
                                ) { selectedItem, _ ->

                                    languageList.data.map {
                                        if (selectedItem == it.language) {
                                            langauge = it.id.toInt()
                                        }
                                    }

                                    binding.languageSpinner.text = selectedItem
                                }
                            }

                            if (languageData != null) {
                                languageList.data.map {
                                    if (it.language == languageData!!.language) {
                                        langauge = it.id.toInt()
                                        binding.languageSpinner.text = languageData!!.language
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

}