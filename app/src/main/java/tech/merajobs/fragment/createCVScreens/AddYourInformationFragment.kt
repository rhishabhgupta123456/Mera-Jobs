package tech.merajobs.fragment.createCVScreens

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import tech.merajobs.R
import tech.merajobs.activity.CreateCVActivity
import tech.merajobs.databinding.FragmentAddYourInformationBinding
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.MediaUtility
import tech.merajobs.utility.SessionManager
import java.io.File

class AddYourInformationFragment : BaseFragment(), View.OnClickListener {


    lateinit var binding: FragmentAddYourInformationBinding

    lateinit var sessionManager: SessionManager
    private lateinit var profileViewModel: ProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddYourInformationBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        sessionManager.setSourceFragment("0")
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.btUploadProfilePicture.setOnClickListener(this)
        binding.btOpenPersonalDetails.setOnClickListener(this)
        binding.btOpenEmployementHistory.setOnClickListener(this)
        binding.btOpenEucationHistory.setOnClickListener(this)
        binding.btOpenProfileSummary.setOnClickListener(this)
        binding.btOpenResumeHeading.setOnClickListener(this)
        binding.btOpenSkill.setOnClickListener(this)
        binding.btOpenLanguge.setOnClickListener(this)
        binding.btOpenCertificate.setOnClickListener(this)
        binding.btOpenOnlineProfile.setOnClickListener(this)

        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getUserProfile()
            getCVDetails()
        }

    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as CreateCVActivity
        activity.getProgressDetails(0)
        activity.showHideProgressbar(1)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btUploadProfilePicture -> {
                browseCameraAndGallery()
            }

            R.id.btOpenPersonalDetails -> {
                findNavController().navigate(R.id.openPersonalDetailsScreen)
            }

            R.id.btOpenEmployementHistory -> {
                findNavController().navigate(R.id.openEmploymentHistoryFragment)
            }

            R.id.btOpenEucationHistory -> {
                findNavController().navigate(R.id.openEducationFragment)
            }

            R.id.btOpenProfileSummary -> {
                findNavController().navigate(R.id.openProfileSummaryFragment)
            }

            R.id.btOpenResumeHeading -> {
                findNavController().navigate(R.id.openResumeHeadingFragment)
            }

            R.id.btOpenSkill -> {
                findNavController().navigate(R.id.openSkillFragment)
            }

            R.id.btOpenLanguge -> {
                findNavController().navigate(R.id.openlanguageFragment)
            }

            R.id.btOpenCertificate -> {
                findNavController().navigate(R.id.openCertificateFragment)
            }

            R.id.btOpenOnlineProfile -> {
                findNavController().navigate(R.id.openOnlineProfileFragment)
            }

        }
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
                    if (!isNetworkAvailable()) {
                        alertErrorDialog(getString(R.string.no_internet))
                    } else {
                        deleteProfilePicture()
                    }

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
                            requireContext(), jsonObjectData["message"].asString, Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.openMyProfileScreen)
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


                            val departmentRole =
                                checkFieldObject(jsonObjectData["data"].asJsonObject.get("department_role"))
                            if (departmentRole != null) {
                                binding.tvRole.text = checkFieldSting(
                                    departmentRole.asJsonObject.get(
                                        "role"
                                    )
                                )
                            }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }

    private fun getCVDetails() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            profileViewModel.getCVDetails(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val data = checkFieldObject(jsonObjectData["data"].asJsonObject)

                            if (data != null) {

                                if (checkFieldSting(data.get("profile_summary")) == "1") {
                                    binding.profileSummaryIcon.setImageResource(R.drawable.tick_icon)
                                } else {
                                    binding.profileSummaryIcon.setImageResource(R.drawable.edit_icon1)
                                }

                                if (checkFieldSting(data.get("personal_details")) == "1") {
                                    binding.personalDetailsIcon.setImageResource(R.drawable.tick_icon)
                                } else {
                                    binding.personalDetailsIcon.setImageResource(R.drawable.edit_icon1)
                                }

                                if (checkFieldSting(data.get("education")) == "1") {
                                    binding.educationIcon.setImageResource(R.drawable.tick_icon)
                                } else {
                                    binding.educationIcon.setImageResource(R.drawable.edit_icon1)
                                }

                                if (checkFieldSting(data.get("employment")) == "1") {
                                    binding.employmentIcon.setImageResource(R.drawable.tick_icon)
                                } else {
                                    binding.employmentIcon.setImageResource(R.drawable.edit_icon1)
                                }

                                if (checkFieldSting(data.get("resume_heading")) == "1") {
                                    binding.resumeHeadingIcon.setImageResource(R.drawable.tick_icon)
                                } else {
                                    binding.resumeHeadingIcon.setImageResource(R.drawable.edit_icon1)
                                }

                                if (checkFieldSting(data.get("it_skills")) == "1") {
                                    binding.itSkillsIcon.setImageResource(R.drawable.tick_icon)
                                } else {
                                    binding.itSkillsIcon.setImageResource(R.drawable.edit_icon1)
                                }

                                if (checkFieldSting(data.get("language")) == "1") {
                                    binding.languageIcon.setImageResource(R.drawable.tick_icon)
                                } else {
                                    binding.languageIcon.setImageResource(R.drawable.edit_icon1)
                                }

                                if (checkFieldSting(data.get("certificates")) == "1") {
                                    binding.certificatesIcon.setImageResource(R.drawable.tick_icon)
                                } else {
                                    binding.certificatesIcon.setImageResource(R.drawable.edit_icon1)
                                }

                                if (checkFieldSting(data.get("online_profile")) == "1") {
                                    binding.onlineProfileIcon.setImageResource(R.drawable.tick_icon)
                                } else {
                                    binding.onlineProfileIcon.setImageResource(R.drawable.edit_icon1)
                                }
                            }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }


}