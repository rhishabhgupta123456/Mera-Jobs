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
import tech.merajobs.dataModel.LanguageData
import tech.merajobs.databinding.ItemEducationBinding
import tech.merajobs.databinding.ItemEmployementBinding
import tech.merajobs.databinding.ItemLanguageBinding
import tech.merajobs.utility.AppConstant
import kotlin.random.Random


class LanguageAdapter(
    private var dataList: ArrayList<LanguageData>,
    private var requireActivity: Activity,
    private var editable : Boolean
) :
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    interface OnRequestAction {
        fun editItem(item: LanguageData)
        fun deleteItem(item: LanguageData)
    }

    private var listener: OnRequestAction? = null

    fun setOnRequestAction(listener: OnRequestAction) {
        this.listener = listener
    }

    class ViewHolder(val binding: ItemLanguageBinding, private var requireActivity: Activity) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: LanguageData, requireActivity: Activity) {

            binding.tvLanguage.text = dataItem.language

            if (dataItem.read == "0" && dataItem.write == "0" && dataItem.speak == "0") {
                binding.tvProperty.text = dataItem.proficiency_level
            } else {
                binding.tvProperty.text = dataItem.proficiency_level + " ( "

                if (dataItem.read == "1") {
                    binding.tvProperty.text = binding.tvProperty.text.toString() + "Read"
                }

                if (dataItem.write == "1") {
                    binding.tvProperty.text = binding.tvProperty.text.toString() + ",Write"
                }

                if (dataItem.speak == "1") {
                    binding.tvProperty.text = binding.tvProperty.text.toString() + ",Speak"
                }


                binding.tvProperty.text = binding.tvProperty.text.toString() + " )"
            }

        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, requireActivity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], requireActivity)

        holder.binding.btMenu.setOnClickListener {
            openMenu(holder.binding.btMenu, dataList[position])
        }

        holder.binding.tvHead.visibility = View.GONE

        if (editable){
            holder.binding.btMenu.visibility = View.VISIBLE
        }else{
            holder.binding.btMenu.visibility = View.GONE
        }

        holder.bind(dataList[position], requireActivity)

    }

    // This Function is used for open Popup Menu
    private fun openMenu(item: ImageView, dataItem: LanguageData) {
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


