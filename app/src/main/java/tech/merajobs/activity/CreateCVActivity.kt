package tech.merajobs.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.databinding.ActivityCreateCvactivityBinding
import tech.merajobs.databinding.ActivityOnBoardingBinding
import tech.merajobs.networkModel.profileNetworkModel.ProfileViewModel
import tech.merajobs.utility.BaseActivity
import tech.merajobs.utility.SessionManager

class CreateCVActivity : BaseActivity() {

    lateinit var binding: ActivityCreateCvactivityBinding
    var screen = 0

    lateinit var sessionManager: SessionManager
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCvactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)


        binding.btPreview.setOnClickListener() {
            findNavController(R.id.fragmentCreateCvContainerView).navigate(
                R.id.openCVPreviewFragmentFragment,
            )
        }

    }

    private fun changeHeader(heading: String) {
        binding.tvPageTitle.text = heading
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgress(progress: Int) {
        if (progress < 25) {
            binding.circle1.isSelected = true
            binding.circle2.isSelected = false
            binding.circle3.isSelected = false
            binding.circle4.isSelected = false
            binding.circle5.isSelected = false
        } else if (progress < 50) {
            binding.circle1.isSelected = true
            binding.circle2.isSelected = true
            binding.circle3.isSelected = false
            binding.circle4.isSelected = false
            binding.circle5.isSelected = false

        } else if (progress < 75) {
            binding.circle1.isSelected = true
            binding.circle2.isSelected = true
            binding.circle3.isSelected = true
            binding.circle4.isSelected = false
            binding.circle5.isSelected = false

        } else if (progress < 100) {
            binding.circle1.isSelected = true
            binding.circle2.isSelected = true
            binding.circle3.isSelected = true
            binding.circle4.isSelected = true
            binding.circle5.isSelected = false

        } else if (progress == 100) {
            binding.circle1.isSelected = true
            binding.circle2.isSelected = true
            binding.circle3.isSelected = true
            binding.circle4.isSelected = true
            binding.circle5.isSelected = true

        }

        binding.CVprogress.max = 100
        binding.CVprogress.progress = progress
        binding.progressText.text = "$progress%"
    }

    fun getProgressDetails(screen: Int) {
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        lifecycleScope.launch {

            profileViewModel.getCVDetails(sessionManager.getBearerToken())
                .observe(this@CreateCVActivity) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val data = checkFieldObject(jsonObjectData["data"].asJsonObject)

                            if (data != null) {
                                updateProgress(checkFieldSting(data.get("profile_complete_percent")).toInt())

                                when (screen) {
                                    0 -> {
                                        if (checkFieldSting(data.get("profile_complete_percent")).toInt() == 100) {
                                            changeHeader("Congratulations")
                                        } else {
                                            changeHeader(getString(R.string.create_cv))
                                        }
                                    }

                                    1 -> {
                                        changeHeader(getString(R.string.create_cv))
                                    }

                                    2 -> {
                                        changeHeader(getString(R.string.create_cv))
                                    }

                                    3 -> {
                                        changeHeader(getString(R.string.create_cv))
                                    }

                                    4 -> {
                                        changeHeader(getString(R.string.create_cv))

                                    }

                                    5 -> {
                                        changeHeader(getString(R.string.create_cv))

                                    }

                                    6 -> {
                                        changeHeader(getString(R.string.create_cv))

                                    }

                                    7 -> {
                                        changeHeader(getString(R.string.create_cv))

                                    }

                                    8 -> {
                                        changeHeader(getString(R.string.create_cv))

                                    }

                                    9 -> {
                                        changeHeader(getString(R.string.create_cv))
                                    }

                                    10 -> {
                                        changeHeader("CV Preview")
                                    }
                                }
                            }


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }

    fun showHideProgressbar(value: Int) {
        if (value == 0) {
            binding.progressBox.visibility = View.GONE
        } else {
            binding.progressBox.visibility = View.VISIBLE
        }
    }


}