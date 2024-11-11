package tech.merajobs.utility

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import tech.merajobs.R
import tech.merajobs.activity.AuthActivity
import tech.merajobs.dataModel.BlockedCompanyDataModel
import tech.merajobs.dataModel.CategoryDataModel
import tech.merajobs.dataModel.CompanyDataModel
import tech.merajobs.networkModel.BaseResponse
import java.util.Locale

open class BaseFragment : Fragment() {


    private fun sessionEndDialog(msg: String) {
        logOutAccount()
        showToast("Login session expired")

  /*      val sessionEndDialog = Dialog(requireContext())
        sessionEndDialog.setCancelable(false)
        sessionEndDialog.setContentView(R.layout.alertbox_error)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(sessionEndDialog.window!!.attributes)

        sessionEndDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        sessionEndDialog.window!!.attributes = layoutParams
        val tvTitle: TextView = sessionEndDialog.findViewById(R.id.tv_title)
        val btnOk: TextView = sessionEndDialog.findViewById(R.id.btn_ok)
        tvTitle.text = msg

        btnOk.setOnClickListener {
            logOutAccount()
        }

        sessionEndDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sessionEndDialog.show()
  */

    }

    fun logOutDialog() {
        val sessionEndDialog = Dialog(requireContext())
        sessionEndDialog.setCancelable(false)
        sessionEndDialog.setContentView(R.layout.alert_dialog_log_out)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(sessionEndDialog.window!!.attributes)

        sessionEndDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        sessionEndDialog.window!!.attributes = layoutParams
        val btnYes: TextView = sessionEndDialog.findViewById(R.id.btn_yes)
        val btnCancel: TextView = sessionEndDialog.findViewById(R.id.btn_cancel)

        btnYes.setOnClickListener {
            logOutAccount()
            sessionEndDialog.dismiss()
        }

        btnCancel.setOnClickListener {
            sessionEndDialog.dismiss()
        }

        sessionEndDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sessionEndDialog.show()
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

    fun logOutAccount() {
        val sessionManager = SessionManager(requireContext())
        sessionManager.deleteData()
        val intent = Intent(requireActivity(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
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
                        }
                    }
                } catch (e: Exception) {
                    alertErrorDialog("${e.message}")
                }
            }
        } else {
            sessionEndDialog(jsonObject.message)
        }
        return null
    }





    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun appLog(message: String) {
        Log.e("System Error", message)
    }

    fun checkFieldSting(json: JsonElement?): String {
        return if (json != null && !json.isJsonNull) {
            json.asString
        } else {
            ""
        }
    }

    fun checkFieldObject(json: JsonElement?): JsonObject? {
        return if (json != null && !json.isJsonNull) {
            json.asJsonObject
        } else {
            null
        }
    }

    fun checkFieldArray(json: JsonElement?): JsonArray? {
        return if (json != null && !json.isJsonNull) {
            json.asJsonArray
        } else {
            null
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun htmlToPlainText(html: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(html).toString()
        }
    }

    fun hasPermissions(): Boolean {
        val postNotifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS)
        } else {
            PackageManager.PERMISSION_GRANTED
        }

        return  postNotifications == PackageManager.PERMISSION_GRANTED
    }

     fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.POST_NOTIFICATIONS
        )


        ActivityCompat.requestPermissions(
            requireActivity(),
            permissions.toTypedArray(),
            1001
        )
    }


    fun searchableAlertDialog(
        searchList: List<String>,
        head : String,
        context : Context,
        onItemSelected: (String,Int) -> Unit
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_searchable_spinner)

        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val tvHead: TextView = dialog.findViewById(R.id.tvHead)
        val etSearch: EditText = dialog.findViewById(R.id.etSearch)
        val tvListShow: ListView = dialog.findViewById(R.id.tvListShow)

        tvHead.text = head
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, searchList)
        tvListShow.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        tvListShow.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = adapter.getItem(position) ?: ""
            onItemSelected(selectedItem , position)  // Call the callback with the selected item
            dialog.dismiss()
        }
    }

}