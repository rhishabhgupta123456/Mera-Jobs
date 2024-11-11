package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import tech.merajobs.R
import tech.merajobs.dataModel.OnlinePresenceList
import tech.merajobs.dataModel.PatentList
import tech.merajobs.dataModel.WorkSampleDataModel
import tech.merajobs.dataModel.WorkSampleList
import tech.merajobs.databinding.ItemOnlinePesenceBinding
import tech.merajobs.databinding.ItemPatentBinding
import tech.merajobs.databinding.ItemSkillBinding
import tech.merajobs.databinding.ItemWorkSampleBinding


class PatentAdapter(private var dataList: ArrayList<PatentList>, private var  requireActivity : Activity) :
    RecyclerView.Adapter<PatentAdapter.ViewHolder>() {

    interface OnRequestAction {
        fun editItem(item: PatentList)
        fun deleteItem(item: PatentList)
    }

    private var listener: OnRequestAction? = null

    fun setOnRequestAction(listener: OnRequestAction) {
        this.listener = listener
    }

    class ViewHolder(val binding: ItemPatentBinding, private var requireActivity: Activity) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: PatentList, requireActivity: Activity) {
            binding.tvTitle.text = dataItem.title
            binding.tvUrl.text = dataItem.url
            binding.tvDescription.text =  dataItem.description
            binding.tvOffice.text =   "Office :" + dataItem.office
            binding.tvApplicationNo.text =   "Application No. :" + dataItem.application_no
            binding.tvIssueDate.text =   "Issue Date. :" + dataItem.issued_date


            binding.tvUrl.setOnClickListener(){
                if (dataItem.url.isNotEmpty()){
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(dataItem.url)
                    requireActivity.startActivity(intent)
                }
            }
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPatentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding ,requireActivity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], requireActivity)

        holder.binding.btMenu.setOnClickListener {
            openMenu(holder.binding.btMenu, dataList[position])
        }


        holder.bind(dataList[position], requireActivity)

    }
    // This Function is used for open Popup Menu
    private fun openMenu(item: ImageView, dataItem: PatentList) {
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


