package tech.merajobs.fragment.createCVScreens

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonArray
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.CreateCVActivity
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.adapter.CertificateAdapter
import tech.merajobs.adapter.EducationAdapter
import tech.merajobs.adapter.ITSkillsAdapter
import tech.merajobs.adapter.LanguageAdapter
import tech.merajobs.adapter.OnlinePresenceAdapter
import tech.merajobs.adapter.WorkingExperienceAdapter
import tech.merajobs.dataModel.CertificateDataModel
import tech.merajobs.dataModel.EducationDataModel
import tech.merajobs.dataModel.ITSkillsDataModel
import tech.merajobs.dataModel.LanguageDataModel
import tech.merajobs.dataModel.OnlinePresenceDataModel
import tech.merajobs.dataModel.WorkingExperienceDataModel
import tech.merajobs.databinding.FragmentCVPrivewFragmentBinding
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.FileDownloader
import tech.merajobs.utility.SessionManager
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class CVPreviewFragmentFragment : BaseFragment(), View.OnClickListener {

    lateinit var binding: FragmentCVPrivewFragmentBinding
    lateinit var sessionManager: SessionManager
    lateinit var profileViewModel: ProfileViewModel


    var resumeURL: String? = null
    var otherCountryListObject: JsonArray? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCVPrivewFragmentBinding.inflate(
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
                    when (sessionManager.getSourceFragment()) {
                        "0" -> {
                            findNavController().navigate(R.id.openAddYourInformationScreen)
                        }
                        "1" -> {
                            findNavController().navigate(R.id.openProfileSummaryFragment)
                        }
                        "2" -> {
                            findNavController().navigate(R.id.openPersonalDetailsScreen)
                        }
                        "3" -> {
                            findNavController().navigate(R.id.openEmploymentHistoryFragment)
                        }
                        "4" -> {
                            findNavController().navigate(R.id.openEducationFragment)
                        }
                        "5" -> {
                            findNavController().navigate(R.id.openResumeHeadingFragment)
                        }
                        "6" -> {
                            findNavController().navigate(R.id.openSkillFragment)
                        }
                        "7" -> {
                            findNavController().navigate(R.id.openlanguageFragment)
                        }
                        "8" -> {
                            findNavController().navigate(R.id.openCertificateFragment)
                        }
                        "9" -> {
                            findNavController().navigate(R.id.openOnlineProfileFragment)
                        }

                        else ->{
                            findNavController().navigate(R.id.openAddYourInformationScreen)
                        }
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.btOpenDashBoard.setOnClickListener(this)
        binding.btDownloadPdfResume.setOnClickListener(this)
        binding.btDownloadWordResume.setOnClickListener(this)



        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getUserProfile()
            getWorkingExperience()
            getEducation()
            getITSkills()
            getOnlinePresence()
            getCertificate()
            getLanguage()
        }

    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btOpenDashBoard -> {
                val intent = Intent(requireActivity(), FindJobActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

            R.id.btDownloadWordResume -> {
                if (hasPermissions()) {
                    FileDownloader.downloadFile(
                        AppConstant.API_BASE_URL + "web/generate-resume-word?jobseeker_id=" + sessionManager.getUserID(),
                        requireContext()
                    )
                } else {
                    requestPermissions()
                }
            }

            R.id.btDownloadPdfResume -> {
                if (hasPermissions()) {
                    FileDownloader.downloadFile(
                        AppConstant.API_BASE_URL + "web/generate-resume-pdf?jobseeker_id=" + sessionManager.getUserID(),
                        requireContext()
                    )
                } else {
                    requestPermissions()
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


                            sessionManager.setUserName(checkFieldSting(jsonObjectData["data"].asJsonObject["name"]))
                            sessionManager.setUserEmail(checkFieldSting(jsonObjectData["data"].asJsonObject["email"]))
                            sessionManager.setUserPhone(checkFieldSting(jsonObjectData["data"].asJsonObject["mobile"]))
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

                            binding.tvUserName.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject["name"])

                            binding.tvCVPercantageText.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject["profile_complete_percent"])

                            if (checkFieldSting(jsonObjectData["data"].asJsonObject["profile_complete_percent"]) != "") {
                                binding.tvCVPercantage.progress =
                                    checkFieldSting(jsonObjectData["data"].asJsonObject["profile_complete_percent"]).toInt()
                            }

                            if (checkFieldSting(jsonObjectData["data"].asJsonObject["mobile"]) != "") {
                                binding.tvMobileEmail.text =
                                    checkFieldSting(jsonObjectData["data"].asJsonObject["mobile"])
                            }

                            if (checkFieldSting(jsonObjectData["data"].asJsonObject["email"]) != "") {
                                binding.tvMobileEmail.text =
                                    binding.tvMobileEmail.text.toString() + "," +
                                            checkFieldSting(jsonObjectData["data"].asJsonObject["email"])
                            }



                            binding.tvProfileSummary.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject["profile_summary"])

                            binding.tvResumeHeading.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject["resume_headline"])

                            resumeURL =
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("resume"))


                            if (checkFieldSting(
                                    jsonObjectData["data"].asJsonObject.get(
                                        "photo_approved"
                                    )
                                ) == "0"
                            ) {
                                Glide.with(requireActivity())
                                    .load(
                                        AppConstant.MEDIA_BASE_URL + checkFieldSting(
                                            jsonObjectData["data"].asJsonObject.get(
                                                "photo"
                                            )
                                        )
                                    )
                                    .apply(RequestOptions.bitmapTransform(BlurTransformation(AppConstant.BLUR_RADIUS)))
                                    .placeholder(R.drawable.demo_user)
                                    .into(binding.userImage)
                            } else {
                                Glide.with(requireActivity())
                                    .load(
                                        AppConstant.MEDIA_BASE_URL + checkFieldSting(
                                            jsonObjectData["data"].asJsonObject.get(
                                                "photo"
                                            )
                                        )
                                    )
                                    .placeholder(R.drawable.demo_user)
                                    .into(binding.userImage)
                            }




                            // Personal Details
                            binding.tvMaritalStatus.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("marital_status_label"))
                            binding.tvDob.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("dob"))
                            binding.tvCategory.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("cast_category_label"))
                            binding.tvUSAWorkPermit.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("usa_work_permit_label"))
                            binding.tvAddress.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("address"))
                            binding.tvZipCode.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("zip"))

                            val country =
                                checkFieldObject(jsonObjectData["data"].asJsonObject.get("country"))
                            if (country != null) {
                                binding.tvCountry.text = checkFieldSting(
                                    country.asJsonObject.get(
                                        "country_name"
                                    )
                                )
                            }

                            val state =
                                checkFieldObject(jsonObjectData["data"].asJsonObject.get("state"))
                            if (state != null) {
                                binding.tvState.text = checkFieldSting(
                                    state.asJsonObject.get(
                                        "state_name"
                                    )
                                )
                            }

                            val city =
                                checkFieldObject(jsonObjectData["data"].asJsonObject.get("city"))
                            if (city != null) {
                                binding.tvCity.text = checkFieldSting(
                                    city.asJsonObject.get(
                                        "city_name"
                                    )
                                )
                            }

                            otherCountryListObject =
                                checkFieldArray(jsonObjectData["data"].asJsonObject.get("other_countries_work_permit_label"))


                            if (otherCountryListObject != null) {
                                val countries =
                                    otherCountryListObject!!.joinToString(", ") { it.asString }
                                binding.tvOtherCountryPermit.text = countries
                            }


                            binding.tvView.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("profile_view_count"))

                            binding.tvDownlaod.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("resume_download_count"))


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
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

                                binding.workExperienceRecyclerview.adapter =
                                    WorkingExperienceAdapter(
                                        workExperienceData.data,
                                        requireActivity(),
                                        false
                                    )
                                binding.workExperienceRecyclerview.layoutManager =
                                    LinearLayoutManager(requireActivity())

                            }

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }

    // This Function is used for get Education
    @SuppressLint("SetTextI18n")
    private fun getEducation() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            profileViewModel.getEducation(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val educationDataModel = Gson().fromJson(
                                jsonObjectData,
                                EducationDataModel::class.java
                            )

                            if (educationDataModel.data != null) {
                                binding.educationRecyclerview.adapter =
                                    EducationAdapter(
                                        educationDataModel.data,
                                        requireActivity(),
                                        false
                                    )
                                binding.educationRecyclerview.layoutManager =
                                    LinearLayoutManager(requireActivity())
                            }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
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
                                        false
                                    )
                                binding.skillecycleView.adapter = adapter
                                binding.skillecycleView.layoutManager =
                                    GridLayoutManager(
                                        requireActivity(),
                                        2,
                                        GridLayoutManager.VERTICAL,
                                        false
                                    )

                            }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
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
                                    OnlinePresenceAdapter(listModel.data, requireActivity(), false)
                                binding.onlinePresenceRecyclerView.adapter = adapter
                                binding.onlinePresenceRecyclerView.layoutManager =
                                    LinearLayoutManager(
                                        requireActivity(),
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )

                            }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
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
                                    CertificateAdapter(listModel.data, requireActivity(), false)
                                binding.certifiacetRecylerView.adapter = adapter
                                binding.certifiacetRecylerView.layoutManager =
                                    LinearLayoutManager(
                                        requireActivity(),
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )

                            }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }

    // This Function is used for get Education
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
                                    LanguageAdapter(
                                        languageDataModel.data,
                                        requireActivity(),
                                        false
                                    )
                                binding.languageRecycleView.adapter = adapter
                                binding.languageRecycleView.layoutManager =
                                    LinearLayoutManager(requireActivity())
                            }

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }


    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as CreateCVActivity
        activity.getProgressDetails(10)
        activity.showHideProgressbar(0)
    }


}