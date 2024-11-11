package tech.merajobs.fragment.employerScreen.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.EmployerActivity
import tech.merajobs.databinding.FragmentEmployerHomeBinding
import tech.merajobs.networkModel.businessViewModel.BusinessViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager

class EmployerHomeFragment : BaseFragment(), View.OnClickListener {


    lateinit var binding: FragmentEmployerHomeBinding
    lateinit var sessionManager: SessionManager
    lateinit var businessViewModel: BusinessViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEmployerHomeBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        businessViewModel = ViewModelProvider(this)[BusinessViewModel::class.java]

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    closeAppAlertDialog()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.btOpenSideMenu.setOnClickListener(this)
        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getUserProfile()
        }


    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as EmployerActivity
        activity.showHideBottomMenu(true)
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.btOpenSideMenu -> {
                val activity = requireActivity() as EmployerActivity
                activity.openDrawer()
            }
        }
    }

    private fun getUserProfile() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            businessViewModel.getEmployerProfile(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            sessionManager.setUserName(checkFieldSting(jsonObjectData["data"].asJsonObject["name"]))
                            sessionManager.setUserEmail(checkFieldSting(jsonObjectData["data"].asJsonObject["email"]))
                            sessionManager.setUserPhone(checkFieldSting(jsonObjectData["data"].asJsonObject["mobile"]))
                            sessionManager.setUserWhatsAppNotification(checkFieldSting(jsonObjectData["data"].asJsonObject["whatsapp_notification_enabled"]))
                            sessionManager.setUserProfile(
                                checkFieldSting(
                                    jsonObjectData["data"].asJsonObject.get(
                                        "photo"
                                    )
                                )
                            )


                            sessionManager.setUserProfileVerify(
                                checkFieldSting(
                                    jsonObjectData["data"].asJsonObject.get(
                                        "photo_approved"
                                    )
                                )
                            )



                            val activity = requireActivity() as EmployerActivity
                            activity.setNavigationItem(
                                checkFieldSting(jsonObjectData["data"].asJsonObject["name"]),
                                checkFieldSting(jsonObjectData["data"].asJsonObject["mobile"]),
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("photo")),
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("photo_approved"))
                            )

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }

    fun closeAppAlertDialog() {
        val alertErrorDialog = Dialog(requireActivity())
        alertErrorDialog.setCancelable(false)
        alertErrorDialog.setContentView(R.layout.alertbox_closeapp)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertErrorDialog.window!!.attributes)

        alertErrorDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertErrorDialog.window!!.attributes = layoutParams

        val tvTitle: TextView = alertErrorDialog.findViewById(R.id.tv_title)
        val btnYes: TextView = alertErrorDialog.findViewById(R.id.btnYes)
        val btnNo: TextView = alertErrorDialog.findViewById(R.id.btnNo)
        tvTitle.text = getString(R.string.are_you_want_to_close_the_app)

        btnYes.setOnClickListener {
            requireActivity().finish()
            alertErrorDialog.dismiss()
        }

        btnNo.setOnClickListener {
            alertErrorDialog.dismiss()
        }

        alertErrorDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertErrorDialog.show()
    }


}