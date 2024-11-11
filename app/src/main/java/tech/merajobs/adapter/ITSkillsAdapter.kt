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
import tech.merajobs.dataModel.ITSkillsData
import tech.merajobs.dataModel.WorkExperienceData
import tech.merajobs.databinding.ItemEmployementBinding
import tech.merajobs.databinding.ItemSkillBinding
import tech.merajobs.utility.AppConstant
import kotlin.random.Random


class ITSkillsAdapter(
    private var dataList: ArrayList<ITSkillsData>,
    private var requireActivity: Activity,
    private var editable : Boolean
) :
    RecyclerView.Adapter<ITSkillsAdapter.ViewHolder>() {

    interface OnRequestAction {
        fun editItem(item: ITSkillsData)
        fun deleteItem(item: ITSkillsData)
    }

    private var listener: OnRequestAction? = null

    fun setOnRequestAction(listener: OnRequestAction) {
        this.listener = listener
    }

    class ViewHolder(val binding: ItemSkillBinding, private var requireActivity: Activity) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: ITSkillsData, requireActivity: Activity) {


            binding.tvSkill.text = dataItem.skill_name + " ( " + dataItem.version + " )"
            binding.tvSkillExperience.text =
                dataItem.experience_years + " Years, " + dataItem.experience_months + " months"
            binding.tvLastUsed.text = "Last Used : " + dataItem.last_used_year

        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    private fun openMenu(item: ImageView, dataItem: ITSkillsData) {
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


