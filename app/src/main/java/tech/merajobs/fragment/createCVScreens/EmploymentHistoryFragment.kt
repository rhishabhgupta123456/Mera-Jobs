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
import tech.merajobs.adapter.WorkingExperienceAdapter
import tech.merajobs.dataModel.WorkExperienceData
import tech.merajobs.dataModel.WorkingExperienceDataModel
import tech.merajobs.databinding.FragmentEmployementHistoryBinding
import tech.merajobs.fragment.findJobScreens.myProfile.profileEdit.BottomSheetEmploymentEditDialog
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager


class EmploymentHistoryFragment : BaseFragment(), View.OnClickListener {


    lateinit var binding: FragmentEmployementHistoryBinding
    lateinit var sessionManager: SessionManager
    lateinit var profileViewModel: ProfileViewModel


    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as CreateCVActivity
        activity.getProgressDetails(3)
        sessionManager.setSourceFragment("3")
        activity.showHideProgressbar(1)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEmployementHistoryBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
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


        binding.btEditEmployment.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)

        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getWorkingExperience()
        }


    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btEditEmployment -> {
                val bottomSheetFragment =
                    BottomSheetEmploymentEditDialog(null, AppConstant.DIRECT,
                        object : BottomSheetEmploymentEditDialog.OnRequestAction {
                            override fun refresh() {
                                getWorkingExperience()
                            }
                        })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btnClose -> {
                findNavController().navigate(R.id.openAddYourInformationScreen)

            }

            R.id.btnNext -> {
                findNavController().navigate(R.id.openEducationFragment)
            }

        }
    }

    // This Function is used for get Working Experience
    @SuppressLint("SetTextI18n")
    private fun getWorkingExperience() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            profileViewModel.getWorkingExperience(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val workExperienceData = Gson().fromJson(
                                jsonObjectData,
                                WorkingExperienceDataModel::class.java
                            )

                            if (workExperienceData.data != null) {
                                val adapter =
                                    WorkingExperienceAdapter(
                                        workExperienceData.data,
                                        requireActivity(),
                                        true
                                    )
                                binding.workExperienceRecyclerview.adapter = adapter
                                binding.workExperienceRecyclerview.layoutManager =
                                    LinearLayoutManager(requireActivity())


                                adapter.setOnRequestAction(object :
                                    WorkingExperienceAdapter.OnRequestAction {
                                    override fun editItem(item: WorkExperienceData) {
                                        val bottomSheetFragment =
                                            BottomSheetEmploymentEditDialog(
                                                item,
                                                AppConstant.DIRECT,
                                                object :
                                                    BottomSheetEmploymentEditDialog.OnRequestAction {
                                                    override fun refresh() {
                                                        val activity = requireActivity() as CreateCVActivity
                                                        activity.getProgressDetails(3)
                                                        getWorkingExperience()
                                                    }
                                                }
                                            )
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )
                                    }

                                    override fun deleteItem(item: WorkExperienceData) {
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

        title.text = "Are you want to delete this employment history ?"

        btNo.setOnClickListener {
            deleteAccountDialog.dismiss()
        }

        btYes.setOnClickListener {
            deleteAccountDialog.dismiss()
            if (!isNetworkAvailable()) {
                alertErrorDialog(getString(R.string.no_internet))
            } else {
                deleteWorkExperience(id!!)

            }

        }

        deleteAccountDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteAccountDialog.show()
    }


    private fun deleteWorkExperience(id: Int) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.deleteWorkExperience(
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
                        findNavController().navigate(R.id.openEmploymentHistoryFragment)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }


}