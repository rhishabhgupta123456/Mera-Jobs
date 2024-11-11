package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.merajobs.R
import tech.merajobs.dataModel.EducationData
import tech.merajobs.databinding.ItemEducationBinding
import tech.merajobs.databinding.ItemEmployementBinding
import tech.merajobs.utility.AppConstant
import kotlin.random.Random


class EducationAdapter(
    private var dataList: ArrayList<EducationData>,
    private var requireActivity: Activity,
    private var editable : Boolean
) :
    RecyclerView.Adapter<EducationAdapter.ViewHolder>() {

    interface OnRequestAction {
        fun editItem(item: EducationData)
        fun deleteItem(item: EducationData)
    }

    private var listener: OnRequestAction? = null

    fun setOnRequestAction(listener: OnRequestAction) {
        this.listener = listener
    }

    class ViewHolder(val binding: ItemEducationBinding, private var requireActivity: Activity) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: EducationData, requireActivity: Activity) {

            binding.tvQualification.text =
                dataItem.qualification_name + ", " + dataItem.specialization
            binding.tvCourceName.text = dataItem.course_name
            binding.tvUniversityName.text = dataItem.university_name
            binding.tvCourseDeatils.text =
                dataItem.course_type_label + ", " + dataItem.from_year + " - " + dataItem.to_year



            if (dataItem.company_logo == null || dataItem.company_logo == "") {
                binding.tvLogo.visibility = View.GONE
                binding.tvDefalutImage.visibility = View.VISIBLE

                if (dataItem.course_name != null) {
                    if (dataItem.course_name!!.split(" ").filter { it.isNotEmpty() }
                            .joinToString("") { it[0].uppercase() }.length >= 2) {
                        binding.tvDefalutImage.text =
                            dataItem.course_name!!.split(" ").filter { it.isNotEmpty() }
                                .joinToString("") { it[0].uppercase() }.substring(0, 2)
                    } else {
                        binding.tvDefalutImage.text =
                            dataItem.course_name!!.split(" ").filter { it.isNotEmpty() }
                                .joinToString("") { it[0].uppercase() }
                    }
                } else {
                    binding.tvDefalutImage.text = ""
                }

                when (Random.nextInt(1, 11)) {
                    1 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor1
                            )
                        )
                        binding.tvDefalutImage.setTextColor(
                            this.requireActivity.resources.getColor(
                                R.color.textColor1
                            )
                        )
                    }

                    2 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor2
                            )
                        )
                        binding.tvDefalutImage.setTextColor(
                            this.requireActivity.resources.getColor(
                                R.color.textColor2
                            )
                        )
                    }

                    3 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor3
                            )
                        )
                        binding.tvDefalutImage.setTextColor(
                            this.requireActivity.resources.getColor(
                                R.color.textColor3
                            )
                        )
                    }

                    4 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor4
                            )
                        )
                        binding.tvDefalutImage.setTextColor(
                            this.requireActivity.resources.getColor(
                                R.color.textColor4
                            )
                        )
                    }

                    5 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor5
                            )
                        )
                        binding.tvDefalutImage.setTextColor(
                            this.requireActivity.resources.getColor(
                                R.color.textColor5
                            )
                        )
                    }

                    6 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor6
                            )
                        )
                        binding.tvDefalutImage.setTextColor(
                            this.requireActivity.resources.getColor(
                                R.color.textColor6
                            )
                        )
                    }

                    7 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor7
                            )
                        )
                        binding.tvDefalutImage.setTextColor(
                            this.requireActivity.resources.getColor(
                                R.color.textColor7
                            )
                        )
                    }

                    else -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor8
                            )
                        )
                        binding.tvDefalutImage.setTextColor(
                            this.requireActivity.resources.getColor(
                                R.color.textColor8
                            )
                        )
                    }
                }

            } else {
                binding.tvLogo.visibility = View.VISIBLE
                binding.tvDefalutImage.visibility = View.GONE

                Glide.with(this.requireActivity)
                    .load(AppConstant.MEDIA_BASE_URL + dataItem.company_logo)
                    .into(binding.tvLogo)
            }
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemEducationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, requireActivity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], requireActivity)

        holder.binding.btMenu.setOnClickListener {
            openMenu(holder.binding.btMenu, dataList[position])
        }

        if (editable){
            holder.binding.btMenu.visibility = View.VISIBLE
        }else{
            holder.binding.btMenu.visibility = View.GONE
        }

        holder.binding.tvHead.visibility = View.GONE

        holder.bind(dataList[position], requireActivity)

    }

    // This Function is used for open Popup Menu
    private fun openMenu(item: ImageView, dataItem: EducationData) {
        val popupMenu = PopupMenu(requireActivity, item)
        popupMenu.menuInflater.inflate(R.menu.profile_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked
            when (menuItem.itemId) {
                R.id.btEdit -> {
                    listener?.editItem(dataItem)
                }

                R.id.btDelete -> {
                    listener?.deleteItem(dataItem)
                }
            }
            true
        }


        // Showing the popup menu
        popupMenu.show()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}


