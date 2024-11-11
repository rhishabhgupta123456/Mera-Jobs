package tech.merajobs.fragment.postJobsScreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import tech.merajobs.dataModel.AllFilterData
import tech.merajobs.dataModel.CourseData
import tech.merajobs.dataModel.Model1
import tech.merajobs.dataModel.QualificationData
import tech.merajobs.dataModel.SpecializationData
import tech.merajobs.databinding.FragmentEligibilityBinding
import tech.merajobs.databinding.FragmentJobDetailBinding
import tech.merajobs.networkModel.postJobViewModel.PostJobViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.PostJobSessionManager
import tech.merajobs.utility.SessionManager


class EligibilityFragment : BaseFragment(), View.OnClickListener {


    private lateinit var binding: FragmentEligibilityBinding
    private lateinit var sessionManager: SessionManager
    lateinit var postJobViewModel: PostJobViewModel
    private lateinit var postJobSessionManager: PostJobSessionManager


    private var qualification: String? = null
    private var course: String? = null
    private var specialoization: String? = null
    private var noticePeriod: String? = null
    private var experience: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEligibilityBinding.inflate(
            LayoutInflater.from(requireActivity()), container, false
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
                    findNavController().navigate(R.id.openJobDetailFragment)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.btBack.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)


        updateProgress()
        getQualificationList()
        getAllFilterList()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btBack -> {
                findNavController().navigate(R.id.openJobDetailFragment)
            }

            R.id.btnClose -> {
                findNavController().navigate(R.id.openJobDetailFragment)
            }

            R.id.btnNext -> {
                if (checkValidation()) {
                    postJobSessionManager.setQualification(qualification!!)
                    postJobSessionManager.setCourse(course!!)
                    postJobSessionManager.setSpecialization(specialoization!!)
                    postJobSessionManager.setExperience(experience!!)
                    postJobSessionManager.setNoticePeriod(noticePeriod!!)
                    postJobSessionManager.setQualificationLabel(binding.qualificationSpinner.text.toString())
                    postJobSessionManager.setCourseLabel(binding.courseSpinner.text.toString())
                    postJobSessionManager.setSpecializationLabel(binding.specializationSpinner.text.toString())
                    postJobSessionManager.setExperienceLabel(binding.experienceSpinner.text.toString())
                    postJobSessionManager.setNoticePeriodLabel(binding.noticePeriodSpinner.text.toString())
                    findNavController().navigate(R.id.openPayRollAndWorkLocationFragment)
                }
            }
        }
    }

    private fun checkValidation(): Boolean {
        if (qualification == null) {
            alertErrorDialog("Please select the qualification")
            return false
        } else if (course == null) {
            alertErrorDialog("Please select the course")
            return false
        } else if (specialoization == null) {
            alertErrorDialog("Please select the specialization")
            return false
        } else if (experience == null) {
            alertErrorDialog("Please select the experience")
            return false
        } else if (noticePeriod == null) {
            alertErrorDialog("Please select the notice period")
            return false
        } else {
            return true
        }
    }

    private fun updateProgress() {
        binding.circle1.isSelected = true
        binding.circle2.isSelected = true
        binding.circle3.isSelected = false
        binding.circle4.isSelected = false
        binding.CVprogress.max = 100
        binding.CVprogress.progress = 32
    }


    private fun getQualificationList() {
        var start = 0

        lifecycleScope.launch {
            postJobViewModel.getQualificationList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, QualificationData::class.java)

                            val dataList = mutableListOf("Select")
                            dataList.addAll(allFilterList.data.map { it.qualification })


                            binding.qualificationSpinner.text = "Select"

                            binding.qualificationSpinner.setOnClickListener() {
                                BaseFragment().searchableAlertDialog(
                                    dataList, "Select Qualification", requireContext()
                                ) { selectedItem, _ ->
                                    allFilterList.data.map {
                                        if (selectedItem == it.qualification) {
                                            qualification = it.id
                                            getCourseList(
                                                qualification!!, 0
                                            )
                                        }
                                    }
                                    binding.qualificationSpinner.text = selectedItem
                                }
                            }

                            if (postJobSessionManager.getQualificationLabel() != "") {
                                allFilterList.data.map {
                                    if (it.qualification == postJobSessionManager.getQualificationLabel()) {
                                        qualification = it.id
                                        binding.qualificationSpinner.text = it.qualification
                                        getCourseList(
                                            it.id, 1
                                        )
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

    private fun getCourseList(qualificationId: String, start: Int) {
        var start1 = 0
        lifecycleScope.launch {
            postJobViewModel.getCourseListList(sessionManager.getBearerToken(), qualificationId)
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, CourseData::class.java)

                            val dataList = mutableListOf("Select")
                            dataList.addAll(allFilterList.data.map { it.name })

                            binding.courseSpinner.text = "Select"


                            binding.courseSpinner.setOnClickListener() {
                                BaseFragment().searchableAlertDialog(
                                    dataList, "Select Course", requireContext()
                                ) { selectedItem, position ->
                                    allFilterList.data.map {
                                        if (selectedItem == it.name) {
                                            course = it.id
                                            getSpecializationList(
                                                course!!, 0
                                            )
                                        }
                                    }
                                    binding.courseSpinner.text = selectedItem
                                }
                            }

                            if (start == 1 && postJobSessionManager.getCourseLabel() != "") {
                                allFilterList.data.map {
                                    if (it.name == postJobSessionManager.getCourseLabel()) {
                                        course = it.id
                                        binding.courseSpinner.text = it.name
                                        getSpecializationList(
                                            it.id, 1
                                        )
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

    private fun getSpecializationList(courseId: String, start: Int) {
        lifecycleScope.launch {
            postJobViewModel.getSpecializationList(sessionManager.getBearerToken(), courseId)
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, SpecializationData::class.java)

                            val dataList = mutableListOf("Select")
                            dataList.addAll(allFilterList.data.map { it.specialization })

                            binding.specializationSpinner.text = "Select"

                            binding.specializationSpinner.setOnClickListener() {
                                BaseFragment().searchableAlertDialog(
                                    dataList, "Select Specialization", requireContext()
                                ) { selectedItem, _ ->
                                    allFilterList.data.map {
                                        if (selectedItem == it.specialization) {
                                            specialoization = it.id
                                        }
                                    }
                                    binding.specializationSpinner.text = selectedItem
                                }
                            }


                            if (start == 1 && postJobSessionManager.getSpecializationLabel() != "") {
                                allFilterList.data.map {
                                    if (it.specialization == postJobSessionManager.getSpecializationLabel()) {
                                        specialoization = it.id
                                        binding.specializationSpinner.text = it.specialization
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

    private fun getAllFilterList() {
        lifecycleScope.launch {
            postJobViewModel.getFilterDataList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val allFilterList =
                                Gson().fromJson(jsonObjectData, AllFilterData::class.java)

                            setExperienceSpinnerList(allFilterList.data.work_experience)
                            setNoticePeriodSpinnerList(allFilterList.data.notice_period)


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }
        }

    }

    private fun setNoticePeriodSpinnerList(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label.toString() })


        binding.noticePeriodSpinner.setOnClickListener() {
            searchableAlertDialog(
                dataList, "Select Notice Period", requireContext()
            ) { selectedItem, position ->

                allFilterList.map {
                    if (selectedItem == it.label) {
                        noticePeriod = it.value
                    }
                }

                binding.noticePeriodSpinner.text = selectedItem
            }
        }


        allFilterList.map {
            if (it.value == postJobSessionManager.getNoticePeriod()) {
                noticePeriod = it.value
                binding.noticePeriodSpinner.text = it.label
            }
        }


    }

    private fun setExperienceSpinnerList(allFilterList: ArrayList<Model1>) {
        val dataList = mutableListOf("Select")
        dataList.addAll(allFilterList.map { it.label.toString() })


        binding.experienceSpinner.setOnClickListener() {
            searchableAlertDialog(
                dataList, "Select Experience", requireContext()
            ) { selectedItem, position ->


                allFilterList.map {
                    if (selectedItem == it.label) {
                        experience = it.value
                    }
                }


                binding.experienceSpinner.text = selectedItem
            }
        }


        allFilterList.map {
            if (it.value == postJobSessionManager.getExperience()) {
                experience = it.value
                binding.experienceSpinner.text = it.label
            }
        }


    }

}