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
import tech.merajobs.dataModel.CertificateList
import tech.merajobs.databinding.BottomSheetCertificationEditDialogBinding
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BottomSheetCertificationEditDialog(
    private var certificateList: CertificateList?,
    private var sourceFragment: String,
    private val callback: OnRequestAction?
) : BottomSheetDialogFragment(), View.OnClickListener {


    lateinit var binding: BottomSheetCertificationEditDialogBinding
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
        binding = BottomSheetCertificationEditDialogBinding.inflate(
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



        if (certificateList != null) {
            binding.etCertificate.setText(certificateList!!.certificate)
            binding.etCertificateNo.setText(certificateList!!.certificate_no)
            binding.etInsitute.setText(certificateList!!.institute)
            binding.etValidTill.setText(certificateList!!.valid_till)
        }

        binding.btnClose.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        binding.btCloseFilter.setOnClickListener(this)
        binding.tvValidTill.setOnClickListener(this)

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
                        if (certificateList != null) {
                            updatePatent()
                        } else {
                            addPatent()
                        }
                    }

                }

            }

            R.id.tvValidTill -> {
                openDatePicker { selectedDate ->
                    binding.etValidTill.text = selectedDate
                }
            }

        }
    }

    private fun checkValidation(): Boolean {
        if (binding.etCertificate.text.isEmpty()) {
            alertErrorDialog("Please fill certificate name")
            return false
        } else if (binding.etCertificateNo.text.isEmpty()) {
            alertErrorDialog("Please fill certificate number")
            return false
        } else if (binding.etInsitute.text.isEmpty()) {
            alertErrorDialog("Please Fill Institute")
            return false
        } else if (binding.etValidTill.text.isEmpty()) {
            alertErrorDialog("Please Fill Valid Till Date")
            return false
        } else {
            return true
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

    private fun updatePatent() {
        lifecycleScope.launch {
            profileViewModel.updateCertificate(
                sessionManager.getBearerToken(),
                certificateList!!.id.toInt(),
                binding.etCertificate.text.toString(),
                binding.etInsitute.text.toString(),
                binding.etValidTill.text.toString(),
                binding.etCertificateNo.text.toString(),
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

    private fun addPatent() {
        lifecycleScope.launch {
            profileViewModel.addCertificate(
                sessionManager.getBearerToken(),
                binding.etCertificate.text.toString(),
                binding.etInsitute.text.toString(),
                binding.etValidTill.text.toString(),
                binding.etCertificateNo.text.toString(),
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