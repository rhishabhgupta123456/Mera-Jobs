package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import tech.merajobs.R
import tech.merajobs.dataModel.JobList
import tech.merajobs.databinding.ItemCompanyBinding
import tech.merajobs.databinding.ItemJobBinding
import tech.merajobs.utility.AppConstant
import kotlin.random.Random


class JobAdapter(
    private var dataList: ArrayList<JobList>,
    private var requireActivity: Activity,
    private var sourceFragment: String,
) :
    RecyclerView.Adapter<JobAdapter.ViewHolder>() {

    interface OnRequestAction {
        fun savedJob(item: JobList)
        fun unSavedJob(item: JobList)
    }

    var listener: OnRequestAction? = null

    fun setOnRequestAction(listener: OnRequestAction) {
        this.listener = listener
    }


    class ViewHolder(val binding: ItemJobBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: JobList, requireActivity: Activity) {


            binding.tvCompanyName.text = dataItem.company_name
            binding.tvCompanyAddress.text = dataItem.address
            binding.tvTitle.text = dataItem.title
            binding.tvJobType.text = dataItem.employment_type_label
             binding.tvSalary.text = dataItem.salary_label
            binding.tvWorkType.text = dataItem.work_location_type_label

          if (dataItem.salary_label != "Not Disclosed"){
              binding.tvSalaryType.text = " / ${dataItem.salary_type_label}"
          }

            if (dataItem.is_saved == 1) {
                binding.btBookmarkJob.setImageResource(R.drawable.bookmark_select_icon)
            } else {
                binding.btBookmarkJob.setImageResource(R.drawable.save_job_icon)
            }


            when (dataItem.job_label) {
                "0" -> binding.cardView.setCardBackgroundColor(requireActivity.resources.getColor(R.color.normalCardColor))
                "1" -> binding.cardView.setCardBackgroundColor(requireActivity.resources.getColor(R.color.spotlightedCardColor))
                "2" -> binding.cardView.setCardBackgroundColor(requireActivity.resources.getColor(R.color.featuredCardColor))
                else -> binding.cardView.setCardBackgroundColor(requireActivity.resources.getColor(R.color.normalCardColor))
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
            ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], requireActivity)

        holder.binding.btApply.setOnClickListener() {
            val bundle = Bundle()
            bundle.putString(AppConstant.SOURCE_FRAGMENT, sourceFragment)
            bundle.putString(AppConstant.JOB_DESCRIPTION, Gson().toJson(dataList[position]))
            findNavController(holder.itemView).navigate(R.id.openJobDescription, bundle)
        }

        holder.binding.btBookmarkJob.setOnClickListener() {
            if (dataList[position].is_saved == 1) {
                dataList[position].is_saved = 0
                listener?.unSavedJob(dataList[position])
                holder.binding.btBookmarkJob.setImageResource(R.drawable.save_job_icon)
            } else {
                dataList[position].is_saved = 1
                listener?.savedJob(dataList[position])
                holder.binding.btBookmarkJob.setImageResource(R.drawable.bookmark_select_icon)
            }
        }


    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}


