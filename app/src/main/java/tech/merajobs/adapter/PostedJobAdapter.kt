package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import tech.merajobs.R
import tech.merajobs.activity.ChatActivity
import tech.merajobs.activity.PostJobActivity
import tech.merajobs.dataModel.JobList
import tech.merajobs.databinding.ItemCompanyBinding
import tech.merajobs.databinding.ItemJobBinding
import tech.merajobs.databinding.ItemPostedJobBinding
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.PostJobSessionManager
import kotlin.random.Random


class PostedJobAdapter(
    private var dataList: ArrayList<JobList>,
    private var requireActivity: Activity,
    private var sourceFragment: String,
) :
    RecyclerView.Adapter<PostedJobAdapter.ViewHolder>() {


    class ViewHolder(val binding: ItemPostedJobBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: JobList, requireActivity: Activity) {


            binding.tvCompanyName.text = dataItem.company_name
            binding.tvCompanyAddress.text = dataItem.address
            binding.tvTitle.text = dataItem.title
            binding.tvJobType.text = dataItem.employment_type_label
            binding.tvSalary.text = dataItem.salary_label
            binding.tvWorkType.text = dataItem.work_location_type_label

            if (dataItem.salary_label != "Not Disclosed") {
                binding.tvSalaryType.text = " / ${dataItem.salary_type_label}"
            }


            if (dataItem.company_logo == null || dataItem.company_logo == "") {
                binding.tvLogo.visibility = View.GONE
                binding.tvDefalutImage.visibility = View.VISIBLE

                if (dataItem.company_name != null) {
                    if (dataItem.company_name.split(" ").filter { it.isNotEmpty() }
                            .joinToString("") { it[0].uppercase() }.length >= 2) {
                        binding.tvDefalutImage.text =
                            dataItem.company_name.split(" ").filter { it.isNotEmpty() }
                                .joinToString("") { it[0].uppercase() }.substring(0, 2)
                    } else {
                        binding.tvDefalutImage.text =
                            dataItem.company_name.split(" ").filter { it.isNotEmpty() }
                                .joinToString("") { it[0].uppercase() }
                    }
                } else {
                    binding.tvDefalutImage.text = ""
                }

                when (Random.nextInt(1, 11)) {
                    1 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireActivity.resources.getColor(
                                R.color.bgColor1
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireActivity.resources.getColor(R.color.textColor1))
                    }

                    2 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireActivity.resources.getColor(
                                R.color.bgColor2
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireActivity.resources.getColor(R.color.textColor2))
                    }

                    3 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireActivity.resources.getColor(
                                R.color.bgColor3
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireActivity.resources.getColor(R.color.textColor3))
                    }

                    4 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireActivity.resources.getColor(
                                R.color.bgColor4
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireActivity.resources.getColor(R.color.textColor4))
                    }

                    5 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireActivity.resources.getColor(
                                R.color.bgColor5
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireActivity.resources.getColor(R.color.textColor5))
                    }

                    6 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireActivity.resources.getColor(
                                R.color.bgColor6
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireActivity.resources.getColor(R.color.textColor6))
                    }

                    7 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireActivity.resources.getColor(
                                R.color.bgColor7
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireActivity.resources.getColor(R.color.textColor7))
                    }

                    else -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireActivity.resources.getColor(
                                R.color.bgColor8
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireActivity.resources.getColor(R.color.textColor8))
                    }
                }

            } else {
                binding.tvLogo.visibility = View.VISIBLE
                binding.tvDefalutImage.visibility = View.GONE

                Glide.with(requireActivity).load(dataItem.company_logo)
                    .into(binding.tvLogo)
            }


        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPostedJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], requireActivity)


        holder.binding.btEdit.setOnClickListener() {
            val postJobSessionManager = PostJobSessionManager(requireActivity)
            postJobSessionManager.setJobId(dataList[position].id.toString())
            postJobSessionManager.setTitle(dataList[position].title)
            postJobSessionManager.setNoOFVacancy(dataList[position].position.toString())
            postJobSessionManager.setJobLabel(dataList[position].job_label)
            postJobSessionManager.setIndustry(dataList[position].industry_id.toString())
            postJobSessionManager.setIndustryLabel(dataList[position].industry_name)
            postJobSessionManager.setDepartment(dataList[position].department_id.toString())
            postJobSessionManager.setDepartmentLabel(dataList[position].department_name)
            postJobSessionManager.setSkill(null)
            postJobSessionManager.setRole(dataList[position].role_id.toString())
            postJobSessionManager.setRoleLabel(dataList[position].role)
            postJobSessionManager.setGoodToHave(dataList[position].good_to_have)
            postJobSessionManager.setDescription(dataList[position].description)
            postJobSessionManager.setLastDateToApply(dataList[position].last_apply_date)
            postJobSessionManager.setQualification(dataList[position].qualification_id.toString())
            postJobSessionManager.setCourse(dataList[position].course_id.toString())
            postJobSessionManager.setSpecialization(dataList[position].specialization_id.toString())
            postJobSessionManager.setExperience(dataList[position].experience)
            postJobSessionManager.setNoticePeriod(dataList[position].notice_period)
            postJobSessionManager.setQualificationLabel(dataList[position].qualification)
            postJobSessionManager.setCourseLabel(dataList[position].course.toString())
            postJobSessionManager.setSpecializationLabel(dataList[position].specialization.toString())
            postJobSessionManager.setExperienceLabel(dataList[position].experience_label.toString())
            postJobSessionManager.setNoticePeriodLabel(dataList[position].notice_period.toString())
            postJobSessionManager.setSalaryRange(dataList[position].salary_range.toString())
            postJobSessionManager.setSalaryType(dataList[position].salary_type)
            postJobSessionManager.setNegotiable(dataList[position].is_negotiable)
            postJobSessionManager.setCurrency(dataList[position].currency)
            postJobSessionManager.setEmploymentType(dataList[position].employment_type.toString())
            postJobSessionManager.setWorkLocationType(dataList[position].work_location_type)
            postJobSessionManager.setLocation(dataList[position].location.toString())
            postJobSessionManager.setSalaryRangeLabel(dataList[position].salary_label.toString())
            postJobSessionManager.setSalaryTypeLabel(dataList[position].salary_type_label.toString())
            postJobSessionManager.setNegotiableLabel(dataList[position].is_negotiable.toString())
            postJobSessionManager.setCurrencyLabel(dataList[position].salary_label.toString())
            postJobSessionManager.setEmploymentTypeLabel(dataList[position].employment_type_label.toString())
            postJobSessionManager.setWorkLocationTypeLabel(dataList[position].work_location_type_label.toString())
            postJobSessionManager.setLocationLabel(dataList[position].location.toString())
            requireActivity.startActivity(Intent(requireActivity, PostJobActivity::class.java))
        }

        holder.binding.btApply.setOnClickListener() {
            val bundle = Bundle()
            bundle.putString(AppConstant.SOURCE_FRAGMENT, sourceFragment)
            bundle.putString(AppConstant.JOB_DESCRIPTION, Gson().toJson(dataList[position]))
            findNavController(holder.itemView).navigate(R.id.openJobDescriptionPreview, bundle)
        }


    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}


