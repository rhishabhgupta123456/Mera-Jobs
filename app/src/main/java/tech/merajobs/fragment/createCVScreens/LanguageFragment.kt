package tech.merajobs.fragment.createCVScreens

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.CreateCVActivity
import tech.merajobs.adapter.LanguageAdapter
import tech.merajobs.dataModel.LanguageData
import tech.merajobs.dataModel.LanguageDataModel
import tech.merajobs.databinding.FragmentLanguageBinding
import tech.merajobs.fragment.findJobScreens.myProfile.profileEdit.BottomSheetLanguageEditDialog
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager

class LanguageFragment : BaseFragment(), View.OnClickListener {


    lateinit var binding: FragmentLanguageBinding
    lateinit var sessionManager: SessionManager
    lateinit var profileViewModel: ProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLanguageBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as CreateCVActivity
        sessionManager.setSourceFragment("7")
        activity.getProgressDetails(7)
        activity.showHideProgressbar(1)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.openAddYourInformationScreen)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.btEditLanguage.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)

        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getLanguage()
        }

    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btEditLanguage -> {
                val bottomSheetFragment = BottomSheetLanguageEditDialog(null, AppConstant.DIRECT,
                    object : BottomSheetLanguageEditDialog.OnRequestAction {
                        override fun refresh() {
                            getLanguage()
                        }
                    })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btnClose -> {
                findNavController().navigate(R.id.openAddYourInformationScreen)

            }

            R.id.btnNext -> {
                findNavController().navigate(R.id.openCertificateFragment)
            }


        }
    }

    @SuppressLint("SetTextI18n")
    private fun getLanguage() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            profileViewModel.getLanguage(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val languageDataModel = Gson().fromJson(
                                jsonObjectData,
                                LanguageDataModel::class.java
                            )

                            if (languageDataModel.data != null) {
                                val adapter =
                                    LanguageAdapter(languageDataModel.data, requireActivity(), true)
                                binding.languageRecycleView.adapter = adapter
                                binding.languageRecycleView.layoutManager =
                                    LinearLayoutManager(requireActivity())
                                adapter.setOnRequestAction(object :
                                    LanguageAdapter.OnRequestAction {
                                    override fun editItem(item: LanguageData) {
                                        val bottomSheetFragment =
                                            BottomSheetLanguageEditDialog(
                                                item,
                                                AppConstant.DIRECT,
                                                object :
                                                    BottomSheetLanguageEditDialog.OnRequestAction {
                                                    override fun refresh() {
                                                        val activity = requireActivity() as CreateCVActivity
                                                        activity.getProgressDetails(7)
                                                        getLanguage()
                                                    }
                                                })
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )
                                    }

                                    override fun deleteItem(item: LanguageData) {
                                        deleteItemAlertBox(item.id.toInt())
                                    }


                                })
                            }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }

    }

    // This Function is used for show delete Item alert box
    @SuppressLint("SetTextI18n")
    private fun deleteItemAlertBox(id: Int?) {
        val deleteAccountDialog = Dialog(requireContext())
        deleteAccountDialog.setContentView(R.layout.delete_item_alert_dialog)
        deleteAccountDialog.setCancelable(true)

        val btYes: TextView = deleteAccountDialog.findViewById(R.id.yes)
        val btNo: TextView = deleteAccountDialog.findViewById(R.id.no)
        val title: TextView = deleteAccountDialog.findViewById(R.id.title)

        title.text = "Are you want to delete this Language?"

        btNo.setOnClickListener {
            deleteAccountDialog.dismiss()
        }

        btYes.setOnClickListener {
            deleteAccountDialog.dismiss()
            if (!isNetworkAvailable()) {
                alertErrorDialog(getString(R.string.no_internet))
            } else {
                deleteLanguage(id!!)

            }

        }

        deleteAccountDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteAccountDialog.show()
    }


    private fun deleteLanguage(id: Int) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.deleteLanguage(
                sessionManager.getBearerToken(),
                id
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE

                val jsonObjectData = checkResponse(jsonObject)
                Log.e("Edit Profile", "True")
                if (jsonObjectData != null) {
                    try {
                        Toast.makeText(
                            requireContext(), jsonObjectData["message"].asString, Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openlanguageFragment)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }

}