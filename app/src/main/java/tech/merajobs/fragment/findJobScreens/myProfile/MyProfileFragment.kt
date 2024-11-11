package tech.merajobs.fragment.findJobScreens.myProfile

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import tech.merajobs.R
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.adapter.CertificateAdapter
import tech.merajobs.adapter.EducationAdapter
import tech.merajobs.adapter.ITSkillsAdapter
import tech.merajobs.adapter.LanguageAdapter
import tech.merajobs.adapter.OnlinePresenceAdapter
import tech.merajobs.adapter.PatentAdapter
import tech.merajobs.adapter.PresentationAdapter
import tech.merajobs.adapter.WhitePaperAdapter
import tech.merajobs.adapter.WorkSampleAdapter
import tech.merajobs.adapter.WorkingExperienceAdapter
import tech.merajobs.dataModel.CertificateDataModel
import tech.merajobs.dataModel.CertificateList
import tech.merajobs.dataModel.EducationData
import tech.merajobs.dataModel.EducationDataModel
import tech.merajobs.dataModel.ITSkillsData
import tech.merajobs.dataModel.ITSkillsDataModel
import tech.merajobs.dataModel.LanguageData
import tech.merajobs.dataModel.LanguageDataModel
import tech.merajobs.dataModel.OnlinePresenceDataModel
import tech.merajobs.dataModel.OnlinePresenceList
import tech.merajobs.dataModel.PatentDataModel
import tech.merajobs.dataModel.PatentList
import tech.merajobs.dataModel.PresentationDataModel
import tech.merajobs.dataModel.PresentationList
import tech.merajobs.dataModel.WhitePaperDataModel
import tech.merajobs.dataModel.WhitePaperList
import tech.merajobs.dataModel.WorkExperienceData
import tech.merajobs.dataModel.WorkSampleDataModel
import tech.merajobs.dataModel.WorkSampleList
import tech.merajobs.dataModel.WorkingExperienceDataModel
import tech.merajobs.databinding.FragmentMyProfileBinding
import tech.merajobs.fragment.findJobScreens.myProfile.accomplishment.BottomSheetCertificationEditDialog
import tech.merajobs.fragment.findJobScreens.myProfile.accomplishment.BottomSheetOnlineProfileEditDialog
import tech.merajobs.fragment.findJobScreens.myProfile.accomplishment.BottomSheetPatentEditDialog
import tech.merajobs.fragment.findJobScreens.myProfile.accomplishment.BottomSheetPresentationEditDialog
import tech.merajobs.fragment.findJobScreens.myProfile.accomplishment.BottomSheetWhitePaperEditDialog
import tech.merajobs.fragment.findJobScreens.myProfile.accomplishment.BottomSheetWorkSampleEditDialog
import tech.merajobs.fragment.findJobScreens.myProfile.profileEdit.BottomSheetEducationEditDialog
import tech.merajobs.fragment.findJobScreens.myProfile.profileEdit.BottomSheetEmploymentEditDialog
import tech.merajobs.fragment.findJobScreens.myProfile.profileEdit.BottomSheetITSkillEditDialog
import tech.merajobs.fragment.findJobScreens.myProfile.profileEdit.BottomSheetJobPreferenceEditDialogFragment
import tech.merajobs.fragment.findJobScreens.myProfile.profileEdit.BottomSheetLanguageEditDialog
import jp.wasabeef.glide.transformations.BlurTransformation
import tech.merajobs.fragment.findJobScreens.myProfile.profileEdit.BottomSheetPersonalDetailsEditDialog
import tech.merajobs.fragment.findJobScreens.myProfile.profileEdit.BottomSheetProfileSummaryEditDialog
import tech.merajobs.fragment.findJobScreens.myProfile.profileEdit.BottomSheetResumeHeadingEditFragment
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.MediaUtility
import tech.merajobs.utility.SessionManager
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MyProfileFragment : BaseFragment(), View.OnClickListener {

    lateinit var binding: FragmentMyProfileBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var profileViewModel: ProfileViewModel
    lateinit var countryList: ArrayList<String>

    var resumeURL: String? = null
    var otherCountryListObject: JsonArray? = null

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        try {
            val data = result.data
            if (data != null) {
                val pdfUri: Uri? = data.data!!
                Log.e("PDF", pdfUri.toString())
                if (pdfUri != null) {
                    val pdfFile = getFileFromUri(requireContext(), pdfUri)
                    Log.e("Pdf File ", pdfFile!!.name.toString())
                    Log.e("Pdf File Path ", pdfFile.path.toString())
                    if (!isNetworkAvailable()) {
                        alertErrorDialog(getString(R.string.no_internet))
                    } else {
                        updateResume(pdfFile)
                    }

                } else {
                    Log.e("PDF", "No PDF selected")
                }

            }
        } catch (e: Exception) {
            alertErrorDialog(e.toString())
        }
    }


    // This Function is used for convert URI in FILE
    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        inputStream?.let {
            val tempFile = File(context.cacheDir, "tempFile.pdf")
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            return tempFile
        }
        return null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMyProfileBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as FindJobActivity
        activity.openProfileScreen()
        activity.showHideBottomMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.openHomeScreen)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.btBack.setOnClickListener(this)
        binding.btOpenMenu.setOnClickListener(this)
        binding.btEditResumeHeading.setOnClickListener(this)
        binding.btEditEmployment.setOnClickListener(this)
        binding.btEditEducation.setOnClickListener(this)
        binding.btEditITSkill.setOnClickListener(this)
        binding.btEditJobPreferences.setOnClickListener(this)
        binding.btEditProfileSummary.setOnClickListener(this)
        binding.btRemoveResume.setOnClickListener(this)
        binding.btUploadResume.setOnClickListener(this)
        binding.btUploadProfilePicture.setOnClickListener(this)
        binding.btEditLanguage.setOnClickListener(this)
        binding.btEditBasicDetails.setOnClickListener(this)
        binding.btOpenEditOnlineProfile.setOnClickListener(this)
        binding.btOpenEditWorkSample.setOnClickListener(this)
        binding.btOpenEditWhitePaper.setOnClickListener(this)
        binding.btOpenEditPresentation.setOnClickListener(this)
        binding.btOpenEditPatents.setOnClickListener(this)
        binding.btOpenEditCertification.setOnClickListener(this)


        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getUserProfile()
            getWorkingExperience()
            getEducation()
            getITSkills()
            getOnlinePresence()
            getWorkSample()
            getWhitePaper()
            getPresentation()
            getPatent()
            getCertificate()
            getLanguage()
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btBack -> {
                findNavController().navigate(R.id.openHomeScreen)
            }

            R.id.btOpenMenu -> {
                val activity = requireActivity() as FindJobActivity
                activity.openDrawer()
            }

            R.id.btOpenEditOnlineProfile -> {
                val bottomSheetFragment =
                    BottomSheetOnlineProfileEditDialog(
                        null,
                        AppConstant.FROM_PROFILE,
                        object : BottomSheetOnlineProfileEditDialog.OnRequestAction {
                            override fun refresh() {
                                getOnlinePresence()
                            }
                        })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)

            }

            R.id.btOpenEditWorkSample -> {
                val bottomSheetFragment = BottomSheetWorkSampleEditDialog(null,
                    object : BottomSheetWorkSampleEditDialog.OnRequestAction {
                        override fun refresh() {
                            getWorkSample()
                        }
                    })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btOpenEditWhitePaper -> {
                val bottomSheetFragment = BottomSheetWhitePaperEditDialog(null,
                    object : BottomSheetWhitePaperEditDialog.OnRequestAction {
                        override fun refresh() {
                            getWhitePaper()
                        }
                    })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btOpenEditPresentation -> {
                val bottomSheetFragment = BottomSheetPresentationEditDialog(null,
                    object : BottomSheetPresentationEditDialog.OnRequestAction {
                        override fun refresh() {
                            getPresentation()
                        }
                    })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btOpenEditPatents -> {
                val bottomSheetFragment = BottomSheetPatentEditDialog(null,
                    object : BottomSheetPatentEditDialog.OnRequestAction {
                        override fun refresh() {
                            getPatent()
                        }
                    })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btOpenEditCertification -> {
                val bottomSheetFragment =
                    BottomSheetCertificationEditDialog(
                        null,
                        AppConstant.FROM_PROFILE,
                        object : BottomSheetCertificationEditDialog.OnRequestAction {
                            override fun refresh() {
                                getCertificate()
                            }
                        })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)

            }

            R.id.btEditJobPreferences -> {
                val bottomSheetFragment = BottomSheetJobPreferenceEditDialogFragment(
                    binding.tvCurrentIndustry.text.toString(),
                    binding.tvDepartment.text.toString(),
                    binding.tvRole.text.toString(),
                    binding.tvWorkMode.text.toString(),
                    binding.tvEmploymentType.text.toString(),
                    binding.tvExpectedCTC.text.toString(),
                    object : BottomSheetJobPreferenceEditDialogFragment.OnRequestAction {
                        override fun refresh() {
                            getUserProfile()
                        }
                    }
                )
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btEditBasicDetails -> {
                val bottomSheetFragment = BottomSheetPersonalDetailsEditDialog(
                    binding.tvMaritalStatus.text.toString(),
                    binding.tvDob.text.toString(),
                    binding.tvCategory.text.toString(),
                    binding.tvUSAWorkPermit.text.toString(),
                    otherCountryListObject,
                    binding.tvCountry.text.toString(),
                    binding.tvState.text.toString(),
                    binding.tvCity.text.toString(),
                    binding.tvAddress.text.toString(),
                    binding.tvZipCode.text.toString(),
                    object : BottomSheetPersonalDetailsEditDialog.OnRequestAction {
                        override fun refresh() {
                            getUserProfile()
                        }
                    }
                )
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btEditResumeHeading -> {
                val bottomSheetFragment = BottomSheetResumeHeadingEditFragment(
                    binding.tvUserName.text.toString(),
                    binding.tvResumeHeading.text.toString(),
                    object : BottomSheetResumeHeadingEditFragment.OnRequestAction {
                        override fun refresh() {
                            getUserProfile()
                        }
                    }
                )
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btEditEmployment -> {
                val bottomSheetFragment =
                    BottomSheetEmploymentEditDialog(
                        null,
                        AppConstant.FROM_PROFILE,
                        object : BottomSheetEmploymentEditDialog.OnRequestAction {
                            override fun refresh() {
                                getWorkingExperience()
                            }
                        })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btEditEducation -> {
                val bottomSheetFragment =
                    BottomSheetEducationEditDialog(
                        null,
                        AppConstant.FROM_PROFILE,
                        object : BottomSheetEducationEditDialog.OnRequestAction {
                            override fun refresh() {
                                getEducation()
                            }
                        })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)

            }

            R.id.btEditITSkill -> {
                val bottomSheetFragment =
                    BottomSheetITSkillEditDialog(
                        null,
                        AppConstant.FROM_PROFILE,
                        object : BottomSheetITSkillEditDialog.OnRequestAction {
                            override fun refresh() {
                                getITSkills()
                            }
                        })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btEditProfileSummary -> {
                val bottomSheetFragment =
                    BottomSheetProfileSummaryEditDialog(binding.tvProfileSummary.text.toString(),
                        object : BottomSheetProfileSummaryEditDialog.OnRequestAction {
                            override fun refresh() {
                                getUserProfile()
                            }
                        })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            R.id.btEditLanguage -> {
                val bottomSheetFragment =
                    BottomSheetLanguageEditDialog(
                        null,
                        AppConstant.FROM_PROFILE,
                        object : BottomSheetLanguageEditDialog.OnRequestAction {
                            override fun refresh() {
                                getLanguage()
                            }
                        })
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)

            }

            R.id.btUploadResume -> {
                selectPDF()
            }

            R.id.btRemoveResume -> {
                deleteItemAlertBox(1, null)
            }

            R.id.btUploadProfilePicture -> {
                browseCameraAndGallery()
            }

        }
    }

    // This Function is used for select PDF
    private fun selectPDF() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        Intent.createChooser(intent, "Select PDF")
        resultLauncher.launch(intent)
    }

    // this function is used for open the menu for opening gallery of camera
    private fun browseCameraAndGallery() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose Image", "Delete Profile", "Cancel")
        val builder = AlertDialog.Builder(
            requireContext()
        )
        builder.setTitle("Choose File")
        builder.setItems(
            items
        ) { dialog: DialogInterface, item: Int ->
            if (items[item] == "Take Photo") {
                try {
                    cameraIntent()
                } catch (e: java.lang.Exception) {
                    Log.v("Exception", e.message!!)
                }
            } else if (items[item] == "Choose Image") {
                try {
                    galleryIntent()
                } catch (e: java.lang.Exception) {
                    Log.v("Exception", e.message!!)
                }
            } else if (items[item] == "Delete Profile") {
                try {
                    deleteProfilePicture()
                } catch (e: java.lang.Exception) {
                    Log.v("Exception", e.message!!)
                }
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    // this function is used for open the camera
    private fun cameraIntent() {
        ImagePicker.with(this).crop(150f, 150f).cameraOnly().compress(1024).maxResultSize(
            1080, 1080
        ).start()
    }

    // this function is used for open the gallery
    private fun galleryIntent() {
        ImagePicker.with(this).crop(150f, 150f).galleryOnly().compress(1024).maxResultSize(
            1080, 1080
        ).start()
    }

    @Deprecated("Deprecated in Java")
    // this function is used for to get image from camera or gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ImagePicker.REQUEST_CODE) {
            data?.let {
                onSelectFromGalleryResultant(it)
            }
        }


    }

    // this function is used for to get image from gallery
    private fun onSelectFromGalleryResultant(data: Intent) {
        try {
            if (!isNetworkAvailable()) {
                alertErrorDialog(getString(R.string.no_internet))
            } else {
                updateProfilePicture(File(MediaUtility.getPath(requireContext(), data.data!!)))
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1001 -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Permission granted, proceed with your action
                    Toast.makeText(requireActivity(), "Permissions granted", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(
                        requireActivity(),
                        "Permissions required to save files and show notifications",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPDF()
                } else {
                    alertErrorDialog("Permission Denied")
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

                            if (resumeURL != "") {
                                binding.tvFileName.text =
                                    resumeURL!!.substring(resumeURL!!.lastIndexOf('/') + 1)
                                binding.tvUploadDate.text =
                                    checkFieldSting(jsonObjectData["data"].asJsonObject.get("resume_uploaded_at"))
                                binding.resumeBox.visibility = View.VISIBLE
                            } else {
                                binding.resumeBox.visibility = View.GONE
                            }


                            val activity = requireActivity() as FindJobActivity
                            activity.setNavigationItem(
                                checkFieldSting(jsonObjectData["data"].asJsonObject["name"]),
                                checkFieldSting(jsonObjectData["data"].asJsonObject["mobile"]),
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("photo")),
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("photo_approved"))
                            )


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
                                    .into(binding.tvUserProfile)
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
                                    .into(binding.tvUserProfile)

                            }


                            // Job Preference
                            val industry =
                                checkFieldObject(jsonObjectData["data"].asJsonObject.get("industry"))
                            if (industry != null) {
                                binding.tvCurrentIndustry.text = checkFieldSting(
                                    industry.asJsonObject.get(
                                        "industry_name"
                                    )
                                )

                            }

                            val department =
                                checkFieldObject(jsonObjectData["data"].asJsonObject.get("department"))
                            if (department != null) {
                                binding.tvDepartment.text = checkFieldSting(
                                    department.asJsonObject.get(
                                        "department_name"
                                    )
                                )

                            }

                            val departmentRole =
                                checkFieldObject(jsonObjectData["data"].asJsonObject.get("department_role"))
                            if (departmentRole != null) {
                                binding.tvRole.text = checkFieldSting(
                                    departmentRole.asJsonObject.get(
                                        "role"
                                    )
                                )
                            }

                            binding.tvEmploymentType.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("employment_type_label"))
                            binding.tvWorkMode.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("work_mode_label"))
                            binding.tvExpectedCTC.text =
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("expected_ctc"))

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
                                                AppConstant.FROM_PROFILE,
                                                object :
                                                    BottomSheetEmploymentEditDialog.OnRequestAction {
                                                    override fun refresh() {
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
                                        deleteItemAlertBox(2, item.id.toInt())
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
                                val adapter =
                                    EducationAdapter(
                                        educationDataModel.data,
                                        requireActivity(),
                                        true
                                    )
                                binding.educationRecyclerview.adapter = adapter
                                binding.educationRecyclerview.layoutManager =
                                    LinearLayoutManager(requireActivity())
                                adapter.setOnRequestAction(object :
                                    EducationAdapter.OnRequestAction {
                                    override fun editItem(item: EducationData) {
                                        val bottomSheetFragment =
                                            BottomSheetEducationEditDialog(
                                                item,
                                                AppConstant.FROM_PROFILE,
                                                object :
                                                    BottomSheetEducationEditDialog.OnRequestAction {
                                                    override fun refresh() {
                                                        getEducation()
                                                    }
                                                }
                                            )
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )

                                    }

                                    override fun deleteItem(item: EducationData) {
                                        deleteItemAlertBox(3, item.id)
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
                                        val bottomSheetFragment = BottomSheetITSkillEditDialog(
                                            item,
                                            AppConstant.FROM_PROFILE,
                                            object : BottomSheetITSkillEditDialog.OnRequestAction {
                                                override fun refresh() {
                                                    getITSkills()
                                                }
                                            }
                                        )
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )
                                    }

                                    override fun deleteItem(item: ITSkillsData) {
                                        deleteItemAlertBox(4, item.id.toInt())
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
                                    OnlinePresenceAdapter(
                                        listModel.data,
                                        requireActivity(),
                                        true
                                    )
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
                                                AppConstant.FROM_PROFILE,
                                                object :
                                                    BottomSheetOnlineProfileEditDialog.OnRequestAction {
                                                    override fun refresh() {
                                                        getOnlinePresence()
                                                    }
                                                }
                                            )
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )
                                    }

                                    override fun deleteItem(item: OnlinePresenceList) {
                                        deleteItemAlertBox(6, item.id.toInt())
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

    // This Function is used for get Education
    @SuppressLint("SetTextI18n")
    private fun getWorkSample() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            profileViewModel.getWorkSample(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val listModel = Gson().fromJson(
                                jsonObjectData,
                                WorkSampleDataModel::class.java
                            )

                            if (listModel.data != null) {
                                val adapter =
                                    WorkSampleAdapter(listModel.data, requireActivity())
                                binding.workSampleRecyclerView.adapter = adapter
                                binding.workSampleRecyclerView.layoutManager =
                                    LinearLayoutManager(
                                        requireActivity(),
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )

                                adapter.setOnRequestAction(object :
                                    WorkSampleAdapter.OnRequestAction {
                                    override fun editItem(item: WorkSampleList) {
                                        val bottomSheetFragment =
                                            BottomSheetWorkSampleEditDialog(
                                                item,
                                                object :
                                                    BottomSheetWorkSampleEditDialog.OnRequestAction {
                                                    override fun refresh() {
                                                        getWorkSample()
                                                    }
                                                })
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )
                                    }

                                    override fun deleteItem(item: WorkSampleList) {
                                        deleteItemAlertBox(7, item.id.toInt())
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


    // This Function is used for get Education
    @SuppressLint("SetTextI18n")
    private fun getWhitePaper() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            profileViewModel.getWhitePaper(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val listModel = Gson().fromJson(
                                jsonObjectData,
                                WhitePaperDataModel::class.java
                            )

                            if (listModel.data != null) {
                                val adapter =
                                    WhitePaperAdapter(listModel.data, requireActivity())
                                binding.whitePaperRecyclerVIew.adapter = adapter
                                binding.whitePaperRecyclerVIew.layoutManager =
                                    LinearLayoutManager(
                                        requireActivity(),
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )

                                adapter.setOnRequestAction(object :
                                    WhitePaperAdapter.OnRequestAction {
                                    override fun editItem(item: WhitePaperList) {
                                        val bottomSheetFragment =
                                            BottomSheetWhitePaperEditDialog(
                                                item,
                                                object :
                                                    BottomSheetWhitePaperEditDialog.OnRequestAction {
                                                    override fun refresh() {
                                                        getWhitePaper()
                                                    }
                                                })
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )
                                    }

                                    override fun deleteItem(item: WhitePaperList) {
                                        deleteItemAlertBox(8, item.id.toInt())
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

    // This Function is used for get Education
    @SuppressLint("SetTextI18n")
    private fun getPresentation() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            profileViewModel.getPresentation(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val listModel = Gson().fromJson(
                                jsonObjectData,
                                PresentationDataModel::class.java
                            )

                            if (listModel.data != null) {
                                val adapter =
                                    PresentationAdapter(listModel.data, requireActivity())
                                binding.presentationRecyclerVIew.adapter = adapter
                                binding.presentationRecyclerVIew.layoutManager =
                                    LinearLayoutManager(
                                        requireActivity(),
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )

                                adapter.setOnRequestAction(object :
                                    PresentationAdapter.OnRequestAction {
                                    override fun editItem(item: PresentationList) {
                                        val bottomSheetFragment =
                                            BottomSheetPresentationEditDialog(item,
                                                object :
                                                    BottomSheetPresentationEditDialog.OnRequestAction {
                                                    override fun refresh() {
                                                        getPresentation()
                                                    }
                                                })
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )
                                    }

                                    override fun deleteItem(item: PresentationList) {
                                        deleteItemAlertBox(9, item.id.toInt())
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

    // This Function is used for get Education
    @SuppressLint("SetTextI18n")
    private fun getPatent() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            profileViewModel.getPatent(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val listModel = Gson().fromJson(
                                jsonObjectData,
                                PatentDataModel::class.java
                            )

                            if (listModel.data != null) {
                                val adapter = PatentAdapter(listModel.data, requireActivity())
                                binding.patentRecyclerView.adapter = adapter
                                binding.patentRecyclerView.layoutManager =
                                    LinearLayoutManager(
                                        requireActivity(),
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )

                                adapter.setOnRequestAction(object :
                                    PatentAdapter.OnRequestAction {
                                    override fun editItem(item: PatentList) {
                                        val bottomSheetFragment =
                                            BottomSheetPatentEditDialog(
                                                item,
                                                object :
                                                    BottomSheetPatentEditDialog.OnRequestAction {
                                                    override fun refresh() {
                                                        getPatent()
                                                    }
                                                })
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )
                                    }

                                    override fun deleteItem(item: PatentList) {
                                        deleteItemAlertBox(10, item.id.toInt())
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
                                                AppConstant.FROM_PROFILE,
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
                                        deleteItemAlertBox(11, item.id.toInt())
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
    private fun deleteItemAlertBox(work: Int, id: Int?) {
        val deleteAccountDialog = Dialog(requireContext())
        deleteAccountDialog.setContentView(R.layout.delete_item_alert_dialog)
        deleteAccountDialog.setCancelable(true)

        val btYes: TextView = deleteAccountDialog.findViewById(R.id.yes)
        val btNo: TextView = deleteAccountDialog.findViewById(R.id.no)
        val title: TextView = deleteAccountDialog.findViewById(R.id.title)

        when (work) {

            1 -> {
                title.text = "Are you want to remove this resume?"
            }

            2 -> {
                title.text = "Are you want to delete this employment?"
            }

            3 -> {
                title.text = "Are you want to delete this education?"
            }

            4 -> {
                title.text = "Are you want to delete this IT Skill?"
            }

            5 -> {
                title.text = "Are you want to delete this Language?"
            }

            6 -> {
                title.text = "Are you want to delete this Online Profile?"
            }

            7 -> {
                title.text = "Are you want to delete this Work Sample?"
            }

            8 -> {
                title.text =
                    "Are you want to delete this White Paper / Research Publication / Journal Entry?"
            }

            9 -> {
                title.text =
                    "Are you want to delete this presentation?"
            }

            10 -> {
                title.text =
                    "Are you want to delete this patent?"
            }

            11 -> {
                title.text =
                    "Are you want to delete this certificate?"
            }


        }


        btNo.setOnClickListener {
            deleteAccountDialog.dismiss()
        }

        btYes.setOnClickListener {
            deleteAccountDialog.dismiss()
            if (!isNetworkAvailable()) {
                alertErrorDialog(getString(R.string.no_internet))
            } else {
                when (work) {

                    1 -> {
                        deleteResume()
                    }

                    2 -> {
                        deleteWorkExperience(id!!)
                    }

                    3 -> {
                        deleteEducation(id!!)
                    }

                    4 -> {
                        deleteSkills(id!!)
                    }

                    5 -> {
                        deleteLanguage(id!!)
                    }

                    6 -> {
                        deleteOnlineProfile(id!!)
                    }

                    7 -> {
                        deleteWorkSample(id!!)
                    }

                    8 -> {
                        deleteWhitePaper(id!!)
                    }

                    9 -> {
                        deletePresentation(id!!)
                    }

                    10 -> {
                        deletePatent(id!!)
                    }

                    11 -> {
                        deleteCertificate(id!!)
                    }

                }

            }

        }

        deleteAccountDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteAccountDialog.show()
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
                                        true
                                    )
                                binding.languageRecycleView.adapter = adapter
                                binding.languageRecycleView.layoutManager =
                                    LinearLayoutManager(requireActivity())
                                adapter.setOnRequestAction(object :
                                    LanguageAdapter.OnRequestAction {
                                    override fun editItem(item: LanguageData) {
                                        val bottomSheetFragment =
                                            BottomSheetLanguageEditDialog(
                                                item,
                                                AppConstant.FROM_PROFILE,
                                                object :
                                                    BottomSheetLanguageEditDialog.OnRequestAction {
                                                    override fun refresh() {
                                                        getLanguage()
                                                    }
                                                }
                                            )
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            bottomSheetFragment.tag
                                        )
                                    }

                                    override fun deleteItem(item: LanguageData) {
                                        deleteItemAlertBox(5, item.id.toInt())
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
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

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
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }

    private fun deleteWorkSample(id: Int) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.deleteWorkSample(
                sessionManager.getBearerToken(),
                id
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE

                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        Toast.makeText(
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }

    private fun deleteWhitePaper(id: Int) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.deleteWhitePaper(
                sessionManager.getBearerToken(),
                id
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE

                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        Toast.makeText(
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }

    private fun deletePresentation(id: Int) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.deletePresentation(
                sessionManager.getBearerToken(),
                id
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE

                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        Toast.makeText(
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }

    private fun deletePatent(id: Int) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.deletePatent(
                sessionManager.getBearerToken(),
                id
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE

                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        Toast.makeText(
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

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
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

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
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }

    private fun deleteEducation(id: Int) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.deleteEducation(
                sessionManager.getBearerToken(),
                id
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE

                val jsonObjectData = checkResponse(jsonObject)
                Log.e("Edit Profile", "True")
                if (jsonObjectData != null) {
                    try {
                        Toast.makeText(
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

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
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }

    // This Function is used for update Resume
    private fun updateResume(pdf: File?) {
        binding.tvProgressBar.visibility = View.VISIBLE

        val pdfRequestBody = pdf?.asRequestBody("application/pdf".toMediaType())

        val pdfPart = pdfRequestBody?.let {
            MultipartBody.Part.createFormData("resume", pdf.name, it)
        }

        lifecycleScope.launch {
            profileViewModel.updateResume(
                sessionManager.getBearerToken(), pdfPart
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE

                val jsonObjectData = checkResponse(jsonObject)
                Log.e("Edit Profile", "True")
                if (jsonObjectData != null) {
                    try {
                        Toast.makeText(
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }

    // This Function is used for Delete Resume
    private fun deleteResume() {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.deleteResume(
                sessionManager.getBearerToken()
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE

                val jsonObjectData = checkResponse(jsonObject)
                Log.e("Edit Profile", "True")
                if (jsonObjectData != null) {
                    try {
                        Toast.makeText(
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }


    }

    private fun deleteProfilePicture() {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            profileViewModel.deleteProfile(
                sessionManager.getBearerToken()
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE

                val jsonObjectData = checkResponse(jsonObject)
                Log.e("Edit Profile", "True")
                if (jsonObjectData != null) {
                    try {
                        Toast.makeText(
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }

    // this function is used for edit attorney profile
    private fun updateProfilePicture(image: File?) {

        val imageRequestBody = image?.asRequestBody("application/octet-stream".toMediaType())
        val imagePart = imageRequestBody?.let {
            MultipartBody.Part.createFormData("photo", image.name, it)
        }

        lifecycleScope.launch {
            profileViewModel.updateProfilePicture(
                sessionManager.getBearerToken(), imagePart
            ).observe(viewLifecycleOwner) { jsonObject ->
                val jsonObjectData = checkResponse(jsonObject)
                Log.e("Edit Profile", "True")
                if (jsonObjectData != null) {
                    try {
                        Glide.with(requireActivity()).load(image).into(binding.tvUserProfile)
                        Toast.makeText(
                            requireContext(),
                            jsonObjectData["message"].asString,
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }
    }


}