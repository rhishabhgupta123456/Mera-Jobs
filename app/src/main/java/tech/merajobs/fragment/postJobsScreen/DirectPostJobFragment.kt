package tech.merajobs.fragment.postJobsScreen

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
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.dataModel.BranchDataModel
import tech.merajobs.databinding.FragmentDirectPostJobBinding
import tech.merajobs.networkModel.businessViewModel.BusinessViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.PostJobSessionManager
import tech.merajobs.utility.SessionManager


class DirectPostJobFragment : BaseFragment(), View.OnClickListener {


    private lateinit var binding: FragmentDirectPostJobBinding
    private lateinit var sessionManager: SessionManager
    lateinit var postJobSessionManager: PostJobSessionManager
    private lateinit var businessViewModel: BusinessViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDirectPostJobBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        postJobSessionManager = PostJobSessionManager(requireContext())
        businessViewModel = ViewModelProvider(this)[BusinessViewModel::class.java]

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.openStartPostJobFragment)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.btBack.setOnClickListener(this)
        binding.btPostAJobs.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btBack -> {
                findNavController().navigate(R.id.openStartPostJobFragment)
            }

            R.id.btPostAJobs -> {
                getBranch()
            }

        }
    }


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
                                if (educationDataModel.data.isNotEmpty()){
                                    findNavController().navigate(R.id.openJobDetailFragment)
                                }else{
                                    alertBranch()
                                }
                            }else{
                                alertBranch()
                            }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }



    private fun alertBranch() {
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

        tvTitle.text = getString(R.string.please_create_branch_first)

        btnYes.setOnClickListener {
            sessionManager.setSourceFragment(AppConstant.DIRECT_POST)
            findNavController().navigate(R.id.openBranchFragment)
            alertErrorDialog.dismiss()
        }

        btnNo.setOnClickListener {
            alertErrorDialog.dismiss()
        }

        alertErrorDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertErrorDialog.show()
    }


}