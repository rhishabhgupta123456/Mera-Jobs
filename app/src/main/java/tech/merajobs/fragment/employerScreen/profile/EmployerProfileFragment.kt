package tech.merajobs.fragment.employerScreen.profile

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import tech.merajobs.R
import tech.merajobs.activity.EmployerActivity
import tech.merajobs.dataModel.CountryDataModel
import tech.merajobs.dataModel.CountryList
import tech.merajobs.databinding.FragmentEmployerProfileBinding
import tech.merajobs.networkModel.businessViewModel.BusinessViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.MediaUtility
import tech.merajobs.utility.SessionManager
import java.io.File


class EmployerProfileFragment : BaseFragment(), View.OnClickListener {

    lateinit var binding: FragmentEmployerProfileBinding
    private lateinit var sessionManager: SessionManager
    lateinit var businessViewModel: BusinessViewModel

    var selectedCountryId: Int? = null

    var photoType: Int? = null
    var userProfile: File? = null
    var companyLogo: File? = null
    var timelineProfile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEmployerProfileBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as EmployerActivity
        activity.openProfileScreen()
        activity.showHideBottomMenu(true)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        businessViewModel = ViewModelProvider(this)[BusinessViewModel::class.java]

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.openHomeScreen)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)



        binding.btBack.setOnClickListener(this)
        binding.btUploadProfilePicture.setOnClickListener(this)
        binding.btUploadCompanyPicture.setOnClickListener(this)
        binding.timelinePhoto.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        binding.btOpenMenu.setOnClickListener(this)


        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getUserProfile()
        }


    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btOpenMenu -> {
                val activity = requireActivity() as EmployerActivity
                activity.openDrawer()
            }

            R.id.btBack -> {
                findNavController().navigate(R.id.openHomeScreen)
            }

            R.id.btnSaveChange -> {
                uploadProfilePicture()
            }

            R.id.btUploadProfilePicture -> {
                photoType = 1
                browseCameraAndGallery()
            }

            R.id.btUploadCompanyPicture -> {
                photoType = 2
                browseCameraAndGallery()
            }

            R.id.timelinePhoto -> {
                photoType = 3
                browseCameraAndGallery()
            }
        }
    }


    // this function is used for open the menu for opening gallery of camera
    private fun browseCameraAndGallery() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose Image", "Cancel")
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
                when (photoType) {
                    1 -> {

                        userProfile = File(MediaUtility.getPath(requireContext(), data.data!!))
                        Glide.with(requireActivity())
                            .load(userProfile)
                            .placeholder(R.drawable.demo_user)
                            .into(binding.tvUserProfile)

                    }

                    2 -> {

                        companyLogo = File(MediaUtility.getPath(requireContext(), data.data!!))
                        Glide.with(requireActivity())
                            .load(companyLogo)
                            .placeholder(R.drawable.demo_user)
                            .into(binding.tvCompanyPhoto)
                    }

                    3 -> {

                        timelineProfile = File(MediaUtility.getPath(requireContext(), data.data!!))

                        Glide.with(requireActivity())
                            .load(timelineProfile)
                            .placeholder(R.drawable.onborad_pic1)
                            .into(binding.timelinePhoto)

                    }
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun getCountryListFromDataBase(jsonElement: String) {

        lifecycleScope.launch {
            businessViewModel.getAllCountry(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->

                    val jsonObjectData = checkResponse(jsonObject)

                    if (jsonObjectData != null) {

                        try {
                            val country =
                                Gson().fromJson(jsonObjectData, CountryDataModel::class.java)
                            val countryList = country.data
                            setCountrySpinner(countryList, jsonElement)

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }

                    }
                }
        }

    }


    private fun setCountrySpinner(countryList: ArrayList<CountryList>, jsonElement: String) {
        var start = 0
        val dataList = mutableListOf("Select")
        dataList.addAll(countryList.map { it.country_name.toString() })



        binding.countrySpinner.text = "Select"

        binding.countrySpinner.setOnClickListener() {
            BaseFragment().searchableAlertDialog(
                dataList, "Select Country", requireContext()
            ) { selectedItem, _ ->

                countryList.map {
                    if (selectedItem == it.country_name) {
                        selectedCountryId = it.id.toInt()
                    }
                }

                binding.countrySpinner.text = selectedItem
            }
        }

        countryList.map {
            if (it.id == jsonElement) {
                selectedCountryId = it.id.toInt()
                binding.countrySpinner.text = it.country_name
            }
        }

    }


    private fun getUserProfile() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            businessViewModel.getEmployerProfile(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {


                            binding.etFullName.setText(checkFieldSting(jsonObjectData["data"].asJsonObject["name"]))
                            binding.etCompanyName.setText(checkFieldSting(jsonObjectData["data"].asJsonObject["company_name"]))
                            binding.etLinkedInProfile.setText(checkFieldSting(jsonObjectData["data"].asJsonObject["company_linkedin"]))
                            binding.etFacebookProfile.setText(checkFieldSting(jsonObjectData["data"].asJsonObject["company_facebook"]))
                            binding.etYoutubeChannel.setText(checkFieldSting(jsonObjectData["data"].asJsonObject["company_youtube"]))
                            binding.etTwitterProfile.setText(checkFieldSting(jsonObjectData["data"].asJsonObject["company_twitter"]))
                            binding.etCompanyIntroductionVideo.setText(
                                checkFieldSting(
                                    jsonObjectData["data"].asJsonObject["company_intro_video"]
                                )
                            )
                            binding.etAbout.setText(checkFieldSting(jsonObjectData["data"].asJsonObject["company_about"]))
                            binding.etCompanyWebsite.setText(checkFieldSting(jsonObjectData["data"].asJsonObject["company_website"]))



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

                            Glide.with(requireActivity())
                                .load(
                                    AppConstant.MEDIA_BASE_URL + checkFieldSting(
                                        jsonObjectData["data"].asJsonObject.get(
                                            "company_logo"
                                        )
                                    )
                                )
                                .placeholder(R.drawable.demo_user)
                                .into(binding.tvCompanyPhoto)

                            Glide.with(requireActivity())
                                .load(
                                    AppConstant.MEDIA_BASE_URL + checkFieldSting(
                                        jsonObjectData["data"].asJsonObject.get(
                                            "company_timeline_photo"
                                        )
                                    )
                                )
                                .placeholder(R.drawable.demo_user)
                                .into(binding.timelinePhoto)

                            sessionManager.setUserName(checkFieldSting(jsonObjectData["data"].asJsonObject["name"]))
                            sessionManager.setUserEmail(checkFieldSting(jsonObjectData["data"].asJsonObject["email"]))
                            sessionManager.setUserPhone(checkFieldSting(jsonObjectData["data"].asJsonObject["mobile"]))
                            sessionManager.setUserWhatsAppNotification(
                                checkFieldSting(
                                    jsonObjectData["data"].asJsonObject["whatsapp_notification_enabled"]
                                )
                            )
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


                            val activity = requireActivity() as EmployerActivity
                            activity.setNavigationItem(
                                checkFieldSting(jsonObjectData["data"].asJsonObject["name"]),
                                checkFieldSting(jsonObjectData["data"].asJsonObject["mobile"]),
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("photo")),
                                checkFieldSting(jsonObjectData["data"].asJsonObject.get("photo_approved"))
                            )

                            getCountryListFromDataBase(
                                checkFieldSting(
                                    jsonObjectData["data"].asJsonObject.get(
                                        "country_id"
                                    )
                                )
                            )

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }


    private fun uploadProfilePicture() {

        val name =
            binding.etFullName.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val companyName =
            binding.etCompanyName.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val linkedinProfile = binding.etLinkedInProfile.text.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val companyWebsite =
            binding.etCompanyWebsite.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val countryId = selectedCountryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val companyAbout =
            binding.etAbout.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val companyFacebook = binding.etFacebookProfile.text.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val companyYoutube =
            binding.etYoutubeChannel.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val companyTwitter =
            binding.etTwitterProfile.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val companyIntroVideo = binding.etCompanyIntroductionVideo.text.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val photo = if (userProfile == null) {
            null
        } else {
            userProfile!!.asRequestBody("application/octet-stream".toMediaType()).let {
                MultipartBody.Part.createFormData("photo", userProfile!!.name, it)
            }
        }

        val companyLogo1 = if (companyLogo == null) {
            null
        } else {
            companyLogo!!.asRequestBody("application/octet-stream".toMediaType()).let {
                MultipartBody.Part.createFormData("company_logo", companyLogo!!.name, it)
            }
        }


        val companyTimelinePhoto = if (timelineProfile == null) {
            null
        } else {
            timelineProfile!!.asRequestBody("application/octet-stream".toMediaType()).let {
                MultipartBody.Part.createFormData(
                    "company_timeline_photo",
                    timelineProfile!!.name,
                    it
                )
            }
        }



        lifecycleScope.launch {
            businessViewModel.updateEmployerProfile(
                sessionManager.getBearerToken(),
                name,
                companyName,
                linkedinProfile,
                companyWebsite,
                countryId,
                companyAbout,
                companyFacebook,
                companyYoutube,
                companyTwitter,
                companyIntroVideo,
                photo,
                companyLogo1,
                companyTimelinePhoto
            ).observe(viewLifecycleOwner) { jsonObject ->
                val jsonObjectData = checkResponse(jsonObject)
                if (jsonObjectData != null) {
                    try {
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


}