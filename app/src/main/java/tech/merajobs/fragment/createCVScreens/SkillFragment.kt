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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.CreateCVActivity
import tech.merajobs.adapter.ITSkillsAdapter
import tech.merajobs.dataModel.ITSkillsData
import tech.merajobs.dataModel.ITSkillsDataModel
import tech.merajobs.databinding.FragmentSkiillBinding
import tech.merajobs.fragment.findJobScreens.myProfile.profileEdit.BottomSheetITSkillEditDialog
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager

class SkillFragment : BaseFragment(), View.OnClickListener {


    lateinit var binding: FragmentSkiillBinding
    lateinit var sessionManager: SessionManager
    lateinit var profileViewModel: ProfileViewModel


    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as CreateCVActivity
        activity.getProgressDetails(6)
        sessionManager.setSourceFragment("6")
        activity.showHideProgressbar(1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSkiillBinding.inflate(
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


        binding.btEditSkll.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)

        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getITSkills()
        }


    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btEditSkll -> {
                val bottomSheetFragment = BottomSheetITSkillEditDialog(null, AppConstant.DIRECT,
                    object : BottomSheetITSkillEditDialog.OnRequestAction {
                        override fun refresh() {
                            getITSkills()
                        }
                    })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }


            R.id.btnClose -> {
                findNavController().navigate(R.id.openAddYourInformationScreen)

            }

            R.id.btnNext -> {
                findNavController().navigate(R.id.openlanguageFragment)
            }

        }
    }

    // This Function is used for get Education
    @SuppressLint("SetTextI18n")
    private fun getITSkills() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            profileViewModel.getITSkills(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val educationDataModel = Gson().fromJson(
                                jsonObjectData,
                                ITSkillsDataModel::class.java
                            )

                            if (educationDataModel.data != null) {
                                val adapter =
                                    ITSkillsAdapter(
                                        educationDataModel.data,
                                        requireActivity(),
                                        true
                                    )
                                binding.skillecycleView.adapter = adapter
                                binding.skillecycleView.layoutManager =
                                    GridLayoutManager(
                                        requireActivity(),
                                        1,
                                        GridLayoutManager.VERTICAL,
                                        false
                                    )

                                adapter.setOnRequestAction(object :
                                    ITSkillsAdapter.OnRequestAction {
                                    override fun editItem(item: ITSkillsData) {
                                        val bottomSheetFragment =
                                            BottomSheetITSkillEditDialog(item, AppConstant.DIRECT,object : BottomSheetITSkillEditDialog.OnRequestAction {
                                                override fun refresh() {
                                                    val activity = requireActivity() as CreateCVActivity
                                                    activity.getProgressDetails(6)
                                                    getITSkills()
                                                }
                                            })
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )
                                    }

                                    override fun deleteItem(item: ITSkillsData) {
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

        title.text = "Are you want to delete this IT SKills"

        btNo.setOnClickListener {
            deleteAccountDialog.dismiss()
        }

        btYes.setOnClickListener {
            deleteAccountDialog.dismiss()
            if (!isNetworkAvailable()) {
                alertErrorDialog(getString(R.string.no_internet))
            } else {
                deleteSkills(id!!)

            }

        }

        deleteAccountDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteAccountDialog.show()
    }

    private fun deleteSkills(id: Int) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.deleteSkills(
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
                        findNavController().navigate(R.id.openSkillFragment)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }

}