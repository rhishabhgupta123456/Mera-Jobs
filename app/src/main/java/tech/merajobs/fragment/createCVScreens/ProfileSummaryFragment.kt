package tech.merajobs.fragment.createCVScreens

import android.annotation.SuppressLint
import android.os.Bundle
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
import tech.merajobs.databinding.FragmentProfileSummaryBinding
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager

class ProfileSummaryFragment : BaseFragment(), View.OnClickListener {


    lateinit var binding: FragmentProfileSummaryBinding
    lateinit var sessionManager: SessionManager
    lateinit var profileViewModel: ProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileSummaryBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as CreateCVActivity
        activity.getProgressDetails(1)
        sessionManager.setSourceFragment("1")
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



        binding.btnSaveChange.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)

        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getUserProfile()
        }


    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btnClose -> {
                findNavController().navigate(R.id.openAddYourInformationScreen)
            }

            R.id.btnSaveChange -> {
                if (!isNetworkAvailable()) {
                    alertErrorDialog(getString(R.string.no_internet))
                } else {
                    updateProfileSummary()
                }

            }


        }
    }


    private fun updateProfileSummary() {
        lifecycleScope.launch {
            profileViewModel.updateProfileSummary(
                sessionManager.getBearerToken(),
                binding.etProfileSummary.text.toString(),
            )
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            findNavController().navigate(R.id.openPersonalDetailsScreen)

                            Toast.makeText(
                                requireContext(),
                                jsonObjectData["message"].asJsonArray[0].asString,
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }


    }

    // This Function is used for get user profile
    @SuppressLint("SetTextI18n")
    private fun getUserProfile() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            profileViewModel.getUserProfile(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            binding.etProfileSummary.setText(checkFieldSting(jsonObjectData["data"].asJsonObject["profile_summary"]))

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }

}