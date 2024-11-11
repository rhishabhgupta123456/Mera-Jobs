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
import tech.merajobs.adapter.CertificateAdapter
import tech.merajobs.dataModel.CertificateDataModel
import tech.merajobs.dataModel.CertificateList
import tech.merajobs.databinding.FragmentCertificateBinding
import tech.merajobs.fragment.findJobScreens.myProfile.accomplishment.BottomSheetCertificationEditDialog
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager


class CertificateFragment : BaseFragment(), View.OnClickListener {


    lateinit var binding: FragmentCertificateBinding
    lateinit var sessionManager: SessionManager
    lateinit var profileViewModel: ProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCertificateBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as CreateCVActivity
        activity.getProgressDetails(8)
        sessionManager.setSourceFragment("8")
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


        binding.btAddCertification.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)

        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getCertificate()
        }


    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btAddCertification -> {
                val bottomSheetFragment =
                    BottomSheetCertificationEditDialog(null, AppConstant.DIRECT,
                        object : BottomSheetCertificationEditDialog.OnRequestAction {
                            override fun refresh() {
                                val activity = requireActivity() as CreateCVActivity
                                activity.getProgressDetails(8)
                                getCertificate()
                            }
                        })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btnClose -> {
                findNavController().navigate(R.id.openAddYourInformationScreen)

            }

            R.id.btnNext -> {
                findNavController().navigate(R.id.openOnlineProfileFragment)
            }


        }
    }


    // This Function is used for get Education
    @SuppressLint("SetTextI18n")
    private fun getCertificate() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            profileViewModel.getCertificate(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val listModel = Gson().fromJson(
                                jsonObjectData,
                                CertificateDataModel::class.java
                            )

                            if (listModel.data != null) {
                                val adapter =
                                    CertificateAdapter(listModel.data, requireActivity(), true)
                                binding.certifiacetRecylerView.adapter = adapter
                                binding.certifiacetRecylerView.layoutManager =
                                    LinearLayoutManager(
                                        requireActivity(),
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )

                                adapter.setOnRequestAction(object :
                                    CertificateAdapter.OnRequestAction {
                                    override fun editItem(item: CertificateList) {
                                        val bottomSheetFragment =
                                            BottomSheetCertificationEditDialog(
                                                item,
                                                AppConstant.DIRECT,
                                                object :
                                                    BottomSheetCertificationEditDialog.OnRequestAction {
                                                    override fun refresh() {
                                                        getCertificate()
                                                    }
                                                }
                                            )
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )
                                    }

                                    override fun deleteItem(item: CertificateList) {
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

        title.text = "Are you want to delete this Certificate ?"

        btNo.setOnClickListener {
            deleteAccountDialog.dismiss()
        }

        btYes.setOnClickListener {
            deleteAccountDialog.dismiss()
            if (!isNetworkAvailable()) {
                alertErrorDialog(getString(R.string.no_internet))
            } else {
                deleteCertificate(id!!)

            }

        }

        deleteAccountDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteAccountDialog.show()
    }

    private fun deleteCertificate(id: Int) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.deleteCertificate(
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
                        findNavController().navigate(R.id.openCertificateFragment)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }

}
