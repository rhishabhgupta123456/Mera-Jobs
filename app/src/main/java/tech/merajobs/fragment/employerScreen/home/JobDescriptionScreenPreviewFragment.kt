package tech.merajobs.fragment.employerScreen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tech.merajobs.R
import tech.merajobs.activity.EmployerActivity
import tech.merajobs.dataModel.JobList
import tech.merajobs.databinding.FragmentJobDescriptionScreenPreviewBinding
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager
import kotlin.random.Random


class JobDescriptionScreenPreviewFragment : BaseFragment(), View.OnClickListener {


    lateinit var binding: FragmentJobDescriptionScreenPreviewBinding

    lateinit var sessionManager: SessionManager

    var JobId: Int? = null
    var userId: String = ""
    var userName: String = ""
    var userProfilePicture: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentJobDescriptionScreenPreviewBinding.inflate(
            LayoutInflater.from(requireActivity()), container, false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.openPostedJobFragment)
                }
            }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)



        binding.btOpenDescription.setOnClickListener(this)
        binding.btOpenCompany.setOnClickListener(this)

        openDescription()

        val arrayListJson = requireArguments().getString(AppConstant.JOB_DESCRIPTION)
        val type = object : TypeToken<JobList>() {}.type
        val jobDetails: JobList = Gson().fromJson(arrayListJson, type)

        JobId = jobDetails.id
        userId = jobDetails.employer_id
        userName = jobDetails.industry_name
        userProfilePicture = jobDetails.company_logo



        binding.tvJobTitle.text = jobDetails.title
        binding.tvPostedDate.text = jobDetails.posted_date
        binding.tvCompanyName.text = jobDetails.company_name
        binding.tvEducation.text = jobDetails.qualification
        binding.tvSalary.text = jobDetails.salary_label
        binding.tvJobType.text = jobDetails.employment_type_label
        binding.tvJobLevel.text = jobDetails.job_label_display
        binding.tvExperience.text = jobDetails.experience_label
        binding.tvLastDate.text = jobDetails.last_apply_date
        binding.tvLocation.text = jobDetails.work_location_type_label
        binding.tvVacancy.text = jobDetails.position.toString()
        binding.tvKeyResponsbility.text = htmlToPlainText(jobDetails.description)
        binding.tvSkill.text = jobDetails.skills
        binding.HeadOffice.text = jobDetails.address
        binding.tvAboutCompany.text = "Demo"
        binding.tvCompanyWebsite.text = jobDetails.employer.company_website


        Glide.with(requireActivity())
            .load(jobDetails.employer.company_timeline_photo)
            .into(binding.backgorundBg)


        binding.btBack.setOnClickListener(this)

        if (jobDetails.company_logo == null || jobDetails.company_logo == "") {
            binding.tvLogo.visibility = View.GONE
            binding.tvDefalutImage.visibility = View.VISIBLE

            if (jobDetails.company_name != null) {
                if (jobDetails.company_name.split(" ").filter { it.isNotEmpty() }
                        .joinToString("") { it[0].uppercase() }.length >= 2) {
                    binding.tvDefalutImage.text =
                        jobDetails.company_name.split(" ").filter { it.isNotEmpty() }
                            .joinToString("") { it[0].uppercase() }.substring(0, 2)
                } else {
                    binding.tvDefalutImage.text =
                        jobDetails.company_name.split(" ").filter { it.isNotEmpty() }
                            .joinToString("") { it[0].uppercase() }
                }
            } else {
                binding.tvDefalutImage.text = ""
            }

            when (Random.nextInt(1, 11)) {
                1 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor1
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor1))
                }

                2 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor2
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor2))
                }

                3 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor3
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor3))
                }

                4 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor4
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor4))
                }

                5 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor5
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor5))
                }

                6 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor6
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor6))
                }

                7 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor7
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor7))
                }

                else -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor8
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor8))
                }
            }

        } else {
            binding.tvLogo.visibility = View.VISIBLE
            binding.tvDefalutImage.visibility = View.GONE

            Glide.with(requireActivity()).load(jobDetails.company_logo).into(binding.tvLogo)
        }

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.btBack -> {
                findNavController().navigate(R.id.openPostedJobFragment)

            }


            R.id.btOpenDescription -> {
                openDescription()
            }

            R.id.btOpenCompany -> {
                openCompany()
            }

        }
    }

    private fun openCompany() {
        binding.btOpenCompany.setBackgroundResource(R.drawable.blue_button)
        binding.btOpenCompany.setTextColor(resources.getColor(R.color.white))
        binding.btOpenDescription.setBackgroundResource(R.drawable.skyblue_button)
        binding.btOpenDescription.setTextColor(resources.getColor(R.color.mainTextColor))

        binding.tvDescriptionBox.visibility = View.GONE
        binding.tvCompanyBox.visibility = View.VISIBLE

    }

    private fun openDescription() {
        binding.btOpenDescription.setBackgroundResource(R.drawable.blue_button)
        binding.btOpenDescription.setTextColor(resources.getColor(R.color.white))
        binding.btOpenCompany.setBackgroundResource(R.drawable.skyblue_button)
        binding.btOpenCompany.setTextColor(resources.getColor(R.color.mainColor))


        binding.tvDescriptionBox.visibility = View.VISIBLE
        binding.tvCompanyBox.visibility = View.GONE
    }


    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as EmployerActivity
        activity.showHideBottomMenu(false)
    }

}