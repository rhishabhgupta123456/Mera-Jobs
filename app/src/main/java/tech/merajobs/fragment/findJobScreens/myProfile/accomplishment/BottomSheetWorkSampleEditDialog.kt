package tech.merajobs.fragment.findJobScreens.myProfile.accomplishment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.AuthActivity
import tech.merajobs.dataModel.WorkSampleList
import tech.merajobs.databinding.BottomSheetWorkSampleEditDialogBinding
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.SessionManager
import java.util.Locale

class BottomSheetWorkSampleEditDialog(
    private var workSampleList: WorkSampleList?,
    private val callback: OnRequestAction?,
) : BottomSheetDialogFragment(), View.OnClickListener {

    lateinit var binding: BottomSheetWorkSampleEditDialogBinding
    lateinit var sessionManager: SessionManager
    private lateinit var profileViewModel: ProfileViewModel


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
        binding = BottomSheetWorkSampleEditDialogBinding.inflate(
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


        if (workSampleList != null) {
            binding.etWorkTitle.setText(workSampleList!!.title)
            binding.etURL.setText(workSampleList!!.url)
            binding.etDescription.setText(workSampleList!!.description)
        }

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
                    if (!isNetworkAvailable()) {
                        alertErrorDialog(getString(R.string.no_internet))
                    } else {
                        if (workSampleList != null) {
                            updateWorkSample()
                        } else {
                            addWorkSample()
                        }
                    }


                }
            }

        }
    }


    private fun checkValidation(): Boolean {
        if (binding.etWorkTitle.text.isEmpty()) {
            alertErrorDialog("Please fill work title")
            return false
        } else if (binding.etURL.text.isEmpty()) {
            alertErrorDialog("Please fill the url")
            return false
        } else if (expYear == null && expMonth == null) {
            alertErrorDialog("Select the duration")
            return false
        } else {
            return true
        }
    }

    private fun updateWorkSample() {
        lifecycleScope.launch {
            profileViewModel.updateWorkSample(
                sessionManager.getBearerToken(),
                workSampleList!!.id.toInt(),
                binding.etWorkTitle.text.toString(),
                binding.etURL.text.toString(),
                binding.etDescription.text.toString(),
                expYear!!,
                expMonth!!,
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

    private fun addWorkSample() {
        lifecycleScope.launch {
            profileViewModel.addWorkSample(
                sessionManager.getBearerToken(),
                binding.etWorkTitle.text.toString(),
                binding.etURL.text.toString(),
                binding.etDescription.text.toString(),
                expYear!!,
                expMonth!!,
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

    private fun setExperienceYearSpinner() {
        val spinnerList = mutableListOf("Select Year")
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

        if (workSampleList != null) {
            val defaultIndex = spinnerList.indexOf(workSampleList!!.duration_year)
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
        val spinnerList = mutableListOf("Select Month")
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

        if (workSampleList != null) {
            val defaultIndex = spinnerList.indexOf(workSampleList!!.duration_months)
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
            val sessionManager = SessionManager(requireContext())
            sessionManager.deleteData()
            val intent = Intent(requireActivity(), AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            Toast.makeText(requireContext(), jsonObject.message, Toast.LENGTH_SHORT).show()
        }
        return null
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
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