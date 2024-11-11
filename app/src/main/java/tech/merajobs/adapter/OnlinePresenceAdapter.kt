package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import tech.merajobs.R
import tech.merajobs.dataModel.OnlinePresenceList
import tech.merajobs.databinding.ItemOnlinePesenceBinding
import tech.merajobs.databinding.ItemSkillBinding


class OnlinePresenceAdapter(
    private var dataList: ArrayList<OnlinePresenceList>,
    private var requireActivity: Activity,
    private var ediatble : Boolean
) :
    RecyclerView.Adapter<OnlinePresenceAdapter.ViewHolder>() {

    interface OnRequestAction {
        fun editItem(item: OnlinePresenceList)
        fun deleteItem(item: OnlinePresenceList)
    }

    private var listener: OnRequestAction? = null

    fun setOnRequestAction(listener: OnRequestAction) {
        this.listener = listener
    }

    class ViewHolder(val binding: ItemOnlinePesenceBinding, private var requireActivity: Activity) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: OnlinePresenceList, requireActivity: Activity) {

            binding.tvAccountType.text = dataItem.account_type_label
            binding.tvUrl.text = dataItem.url
            binding.tvDescription.text = dataItem.description

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
            ItemOnlinePesenceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, requireActivity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], requireActivity)

        holder.binding.btMenu.setOnClickListener {
            openMenu(holder.binding.btMenu, dataList[position])
        }

        if (ediatble){
            holder.binding.btMenu.visibility = View.VISIBLE
        }else{
            holder.binding.btMenu.visibility = View.GONE
        }

        holder.binding.tvHead.text = "${position + 1}"

        holder.bind(dataList[position], requireActivity)

    }

    // This Function is used for open Popup Menu
    private fun openMenu(item: ImageView, dataItem: OnlinePresenceList) {
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

