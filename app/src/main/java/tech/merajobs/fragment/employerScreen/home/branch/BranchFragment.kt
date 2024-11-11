package tech.merajobs.fragment.employerScreen.home.branch

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.EmployerActivity
import tech.merajobs.adapter.BranchAdapter
import tech.merajobs.dataModel.BranchDataModel
import tech.merajobs.dataModel.BranchList
import tech.merajobs.databinding.FragmentBranchBinding
import tech.merajobs.networkModel.businessViewModel.BusinessViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager


class BranchFragment : BaseFragment(), View.OnClickListener {

    lateinit var binding: FragmentBranchBinding
    lateinit var sessionManager: SessionManager
    private lateinit var businessViewModel: BusinessViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBranchBinding.inflate(
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
                    if (sessionManager.getSourceFragment() == AppConstant.HOME_SCREEN) {
                        findNavController().navigate(R.id.openHomeScreen)
                    }else{
                        val intent = Intent(requireContext(), EmployerActivity::class.java)
                        requireActivity().startActivity(intent)
                        requireActivity().finish()
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.btAddBranch.setOnClickListener(this)
        binding.btBack.setOnClickListener(this)


    }

    override fun onResume() {
        super.onResume()
        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            if (sessionManager.getSourceFragment() == AppConstant.HOME_SCREEN) {
                val activity = requireActivity() as EmployerActivity
                activity.showHideBottomMenu(false)
            }
            getBranch()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btAddBranch -> {
                val bottomSheetFragment = BottomSheetBranchEditDialog(null,
                    object : BottomSheetBranchEditDialog.OnRequestAction {
                        override fun refresh() {
                            getBranch()
                        }
                    })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btBack -> {
                if (sessionManager.getSourceFragment() == AppConstant.HOME_SCREEN) {
                    findNavController().navigate(R.id.openHomeScreen)
                }else{
                    val intent = Intent(requireContext(), EmployerActivity::class.java)
                    requireActivity().startActivity(intent)
                    requireActivity().finish()
                }
            }

        }

    }


    // This Function is used for get Education
    private fun getBranch() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            businessViewModel.getBranch(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val educationDataModel = Gson().fromJson(
                                jsonObjectData,
                                BranchDataModel::class.java
                            )

                            if (educationDataModel.data != null) {
                                val adapter =
                                    BranchAdapter(educationDataModel.data, requireActivity(), true)
                                binding.branchRecyclerView.adapter = adapter
                                binding.branchRecyclerView.layoutManager =
                                    LinearLayoutManager(requireActivity())
                                adapter.setOnRequestAction(object :
                                    BranchAdapter.OnRequestAction {
                                    override fun editItem(item: BranchList) {
                                        val bottomSheetFragment = BottomSheetBranchEditDialog(item,
                                            object : BottomSheetBranchEditDialog.OnRequestAction {
                                                override fun refresh() {
                                                    getBranch()
                                                }
                                            })
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )

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


}