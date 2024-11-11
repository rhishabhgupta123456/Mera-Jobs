package tech.merajobs.fragment.createCVScreens

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import tech.merajobs.adapter.OnlinePresenceAdapter
import tech.merajobs.dataModel.OnlinePresenceDataModel
import tech.merajobs.dataModel.OnlinePresenceList
import tech.merajobs.databinding.FragmentOnlineProfileBinding
import tech.merajobs.fragment.findJobScreens.myProfile.accomplishment.BottomSheetOnlineProfileEditDialog
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager

class OnlineProfileFragment : BaseFragment(), View.OnClickListener {

    lateinit var binding: FragmentOnlineProfileBinding
    lateinit var sessionManager: SessionManager
    lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOnlineProfileBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as CreateCVActivity
        activity.getProgressDetails(9)
        sessionManager.setSourceFragment("9")
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


        binding.btAddOnlineProfile.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)


        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getOnlinePresence()
        }


    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btAddOnlineProfile -> {
                val bottomSheetFragment =
                    BottomSheetOnlineProfileEditDialog(null, AppConstant.DIRECT,
                        object : BottomSheetOnlineProfileEditDialog.OnRequestAction {
                            override fun refresh() {
                                getOnlinePresence()
                            }
                        })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }


            R.id.btnClose -> {
                findNavController().navigate(R.id.openAddYourInformationScreen)
            }

            R.id.btnNext -> {
                sessionManager.setSourceFragment("0")
                findNavController().navigate(R.id.openCVPreviewFragmentFragment)
            }

        }
    }

    // This Function is used for get Education
    @SuppressLint("SetTextI18n")
    private fun getOnlinePresence() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            profileViewModel.getOnlineProfile(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val listModel = Gson().fromJson(
                                jsonObjectData,
                                OnlinePresenceDataModel::class.java
                            )

                            if (listModel.data != null) {
                                val adapter =
                                    OnlinePresenceAdapter(listModel.data, requireActivity(), true)
                                binding.onlinePresenceRecyclerView.adapter = adapter
                                binding.onlinePresenceRecyclerView.layoutManager =
                                    LinearLayoutManager(
                                        requireActivity(),
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )

                                adapter.setOnRequestAction(object :
                                    OnlinePresenceAdapter.OnRequestAction {
                                    override fun editItem(item: OnlinePresenceList) {
                                        val bottomSheetFragment =
                                            BottomSheetOnlineProfileEditDialog(
                                                item,
                                                AppConstant.DIRECT,
                                                object :
                                                    BottomSheetOnlineProfileEditDialog.OnRequestAction {
                                                    override fun refresh() {
                                                        val activity = requireActivity() as CreateCVActivity
                                                        activity.getProgressDetails(9)
                                                        getOnlinePresence()
                                                    }
                                                })
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )
                                    }

                                    override fun deleteItem(item: OnlinePresenceList) {
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

        title.text = "Are you want to delete this online profile?"

        btNo.setOnClickListener {
            deleteAccountDialog.dismiss()
        }

        btYes.setOnClickListener {
            deleteAccountDialog.dismiss()
            if (!isNetworkAvailable()) {
                alertErrorDialog(getString(R.string.no_internet))
            } else {
                deleteOnlineProfile(id!!)
            }

        }

        deleteAccountDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteAccountDialog.show()
    }

    private fun deleteOnlineProfile(id: Int) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.deleteOnlineProfile(
                sessionManager.getBearerToken(),
                id
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE

                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        Toast.makeText(
                            requireContext(), jsonObjectData["message"].asString, Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openOnlineProfileFragment)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }


}