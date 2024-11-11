package tech.merajobs.fragment.postJobsScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
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
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.adapter.PathAdapter
import tech.merajobs.dataModel.IndustryDataModel
import tech.merajobs.dataModel.PathDataModel
import tech.merajobs.databinding.FragmentJobDetailBinding
import tech.merajobs.databinding.FragmentJobPriviewBinding
import tech.merajobs.networkModel.postJobViewModel.PostJobViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.PostJobSessionManager
import tech.merajobs.utility.SessionManager


class JobPreviewFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentJobPriviewBinding
    private lateinit var sessionManager: SessionManager
    lateinit var postJobViewModel: PostJobViewModel
    lateinit var postJobSessionManager: PostJobSessionManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentJobPriviewBinding.inflate(
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
        postJobViewModel = ViewModelProvider(this)[PostJobViewModel::class.java]
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.openPayRollAndWorkLocationFragment)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.btBack.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)

        setScreen()
        updateProgress()
    }

    private fun setScreen() {

        binding.tvTitle.text = postJobSessionManager.getTitle()
        binding.tvVacancy.text = postJobSessionManager.getNoOFVacancy()
        binding.tvJobLabel.text = postJobSessionManager.getJobLabel()
        binding.tvIndustry.text = postJobSessionManager.getIndustryLabel()
        binding.tvDepartment.text = postJobSessionManager.getDepartmentLabel()
        binding.tvRole.text = postJobSessionManager.getRoleLabel()
        binding.tvSkillRequired.text = postJobSessionManager.getSkill()?.joinToString { "," }
        binding.tvGoodToHave.text = postJobSessionManager.getGoodToHave()
        binding.tvDescription.text = postJobSessionManager.getDescription()
        binding.tvLastDateToApply.text = postJobSessionManager.getLastDateToApply()
        binding.tvQualification.text = postJobSessionManager.getQualificationLabel()
        binding.tvCourse.text = postJobSessionManager.getCourseLabel()
        binding.tvSpecialization.text = postJobSessionManager.getSpecializationLabel()
        binding.tvExperience.text = postJobSessionManager.getExperienceLabel()
        binding.tvNoticePeriod.text = postJobSessionManager.getNoticePeriodLabel()
        binding.tvSalaryRange.text = postJobSessionManager.getSalaryRangeLabel()
        binding.tvSalaryType.text = postJobSessionManager.getSalaryTypeLabel()
        binding.tvNegotiable.text = postJobSessionManager.getNegotiableLabel()
        binding.tvCurrency.text = postJobSessionManager.getCurrencyLabel()
        binding.tvEmploymentType.text = postJobSessionManager.getEmploymentTypeLabel()
        binding.tvWorkLocation.text = postJobSessionManager.getWorkLocationTypeLabel()
        binding.tvLocation.text = postJobSessionManager.getLocationLabel()


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btBack -> {
                findNavController().navigate(R.id.openPayRollAndWorkLocationFragment)
            }

            R.id.btnClose -> {
                findNavController().navigate(R.id.openPayRollAndWorkLocationFragment)
            }

            R.id.btnNext -> {
                if (postJobSessionManager.getJobId() != null){
                    updateJobs()
                }else{
                    postJobs()
                }
            }
        }
    }

    private fun postJobs() {

        lifecycleScope.launch {
            postJobViewModel.postJob(
                sessionManager.getBearerToken(),
                postJobSessionManager.getTitle(),
                postJobSessionManager.getDescription(),
                postJobSessionManager.getExperience(),
                postJobSessionManager.getSkill()?.mapNotNull { it.toIntOrNull() },
                postJobSessionManager.getWorkLocationType(),
                postJobSessionManager.getNoticePeriod(),
                postJobSessionManager.getIndustry(),
                postJobSessionManager.getDepartment(),
                postJobSessionManager.getRole(),
                postJobSessionManager.getEmploymentType(),
                postJobSessionManager.getQualification(),
                postJobSessionManager.getCourse(),
                postJobSessionManager.getSpecialization(),
                postJobSessionManager.getNoOFVacancy(),
                postJobSessionManager.getGoodToHave(),
                postJobSessionManager.getSalaryRange(),
                postJobSessionManager.getCurrency(),
                postJobSessionManager.getLastDateToApply(),
                postJobSessionManager.getNegotiable(),
                postJobSessionManager.getSalaryType(),
                postJobSessionManager.getLocation(),
                postJobSessionManager.getJobLabel(),

                )
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            Toast.makeText(
                                requireContext(),
                                "Job added successfully !",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }


    }

    private fun updateJobs() {

        lifecycleScope.launch {
            postJobViewModel.updateJobs(
                sessionManager.getBearerToken(),
                postJobSessionManager.getJobId()!!.toInt(),
                postJobSessionManager.getTitle(),
                postJobSessionManager.getDescription(),
                postJobSessionManager.getExperience(),
                postJobSessionManager.getSkill()?.mapNotNull { it.toIntOrNull() },
                postJobSessionManager.getWorkLocationType(),
                postJobSessionManager.getNoticePeriod(),
                postJobSessionManager.getIndustry(),
                postJobSessionManager.getDepartment(),
                postJobSessionManager.getRole(),
                postJobSessionManager.getEmploymentType(),
                postJobSessionManager.getQualification(),
                postJobSessionManager.getCourse(),
                postJobSessionManager.getSpecialization(),
                postJobSessionManager.getNoOFVacancy(),
                postJobSessionManager.getGoodToHave(),
                postJobSessionManager.getSalaryRange(),
                postJobSessionManager.getCurrency(),
                postJobSessionManager.getLastDateToApply(),
                postJobSessionManager.getNegotiable(),
                postJobSessionManager.getSalaryType(),
                postJobSessionManager.getLocation(),
                postJobSessionManager.getJobLabel(),

                )
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            Toast.makeText(
                                requireContext(),
                                "Job added successfully !",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }


    }

    private fun updateProgress() {
        binding.circle1.isSelected = true
        binding.circle2.isSelected = true
        binding.circle3.isSelected = true
        binding.circle4.isSelected = true
        binding.CVprogress.max = 100
        binding.CVprogress.progress = 100
    }


}