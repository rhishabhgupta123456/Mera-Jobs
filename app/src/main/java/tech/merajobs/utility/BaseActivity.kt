package tech.merajobs.utility

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import tech.merajobs.R
import tech.merajobs.SplashActivity
import tech.merajobs.activity.AuthActivity
import tech.merajobs.networkModel.BaseResponse

open class BaseActivity : AppCompatActivity(){

    private fun sessionEndDialog(msg: String) {

        logOutAccount()
        showToast(msg)

        /*  val sessionEndDialog = Dialog(this)
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    fun logOutDialog() {
        val sessionEndDialog = Dialog(this)
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
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
        val alertErrorDialog = Dialog(this)
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
        tvTitle.text = msg

        btnOk.setOnClickListener {
            alertErrorDialog.dismiss()
        }

        alertErrorDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertErrorDialog.show()
    }

    private fun logOutAccount() {
        val sessionManager = SessionManager(this)
        sessionManager.deleteData()
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
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


}