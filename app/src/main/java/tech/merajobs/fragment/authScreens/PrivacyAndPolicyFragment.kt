package tech.merajobs.fragment.authScreens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.CreateCVActivity
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.databinding.FragmentPrivacyAndPolicyBinding
import tech.merajobs.databinding.FragmentSignUpBinding
import tech.merajobs.networkModel.authNetworkPannel.AuthViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment

class PrivacyAndPolicyFragment : BaseFragment() {


    lateinit var binding : FragmentPrivacyAndPolicyBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPrivacyAndPolicyBinding.inflate(LayoutInflater.from(requireActivity()), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val bundle = Bundle()
                    bundle.putString(AppConstant.NAME, requireArguments().getString(AppConstant.NAME))
                    bundle.putString(AppConstant.MOBILE,requireArguments().getString(AppConstant.MOBILE))
                    findNavController().navigate(R.id.openSignUpScreen,bundle)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.btBack.setOnClickListener(){
            val bundle = Bundle()
            bundle.putString(AppConstant.NAME, requireArguments().getString(AppConstant.NAME))
            bundle.putString(AppConstant.MOBILE,requireArguments().getString(AppConstant.MOBILE))
            findNavController().navigate(R.id.openSignUpScreen,bundle)
        }


        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        }else{
            getCMS()
        }


    }


    // This function is used for User Login
    private fun getCMS() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            authViewModel.getCMS(
                "privacy-policy",
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        val data = checkFieldObject(jsonObjectData["data"].asJsonObject)
                        if (data != null){

                            binding.tvPageTitle.text = checkFieldSting(data["page_title"])
                            binding.tvContent.text = htmlToPlainText(checkFieldSting(data["page_content"]))

                        }
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }

        }
    }


}