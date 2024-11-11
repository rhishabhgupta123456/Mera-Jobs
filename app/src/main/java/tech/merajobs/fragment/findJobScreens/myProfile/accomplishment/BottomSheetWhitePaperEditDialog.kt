package tech.merajobs.fragment.findJobScreens.myProfile.accomplishment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import tech.merajobs.dataModel.WhitePaperList
import tech.merajobs.databinding.BottomSheetWhitePaperEditDialogBinding
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BottomSheetWhitePaperEditDialog(
    private var whitePaperList: WhitePaperList?,
    private val callback: OnRequestAction?,
) :
    BottomSheetDialogFragment(), View.OnClickListener {

    lateinit var binding: BottomSheetWhitePaperEditDialogBinding
    lateinit var sessionManager: SessionManager
    private lateinit var profileViewModel: ProfileViewModel

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
        binding = BottomSheetWhitePaperEditDialogBinding.inflate(
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


        if (whitePaperList != null) {
            binding.etTitle.setText(whitePaperList!!.title)
            binding.etURL.setText(whitePaperList!!.url)
            binding.etPublishDate.setText(whitePaperList!!.published_date)
            binding.etDescription.setText(whitePaperList!!.description)
        }

        binding.btnClose.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        binding.btCloseFilter.setOnClickListener(this)
        binding.tvPublishDate.setOnClickListener(this)

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
                        if (whitePaperList != null) {
                            updateWhitePaper()
                        } else {
                            addWhitePaper()
                        }
                    }

                }

            }

            R.id.tvPublishDate -> {
                openDatePicker { selectedDate ->
                    binding.etPublishDate.text = selectedDate
                }
            }

        }
    }


    private fun checkValidation(): Boolean {
        if (binding.etTitle.text.isEmpty()) {
            alertErrorDialog("Please fill work title")
            return false
        } else if (binding.etURL.text.isEmpty()) {
            alertErrorDialog("Please fill the url")
            return false
        } else if (binding.etPublishDate.text.isEmpty()) {
            alertErrorDialog("Select the Publish Date")
            return false
        } else {
            return true
        }
    }

    private fun updateWhitePaper() {
        lifecycleScope.launch {
            profileViewModel.updateWhitePaper(
                sessionManager.getBearerToken(),
                whitePaperList!!.id.toInt(),
                binding.etTitle.text.toString(),
                binding.etURL.text.toString(),
                binding.etDescription.text.toString(),
                binding.etPublishDate.text.toString(),
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

    private fun addWhitePaper() {
        lifecycleScope.launch {
            profileViewModel.addWhitePaper(
                sessionManager.getBearerToken(),
                binding.etTitle.text.toString(),
                binding.etURL.text.toString(),
                binding.etDescription.text.toString(),
                binding.etPublishDate.text.toString(),

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