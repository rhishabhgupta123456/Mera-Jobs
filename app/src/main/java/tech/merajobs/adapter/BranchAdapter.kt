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
import tech.merajobs.dataModel.BranchList
import tech.merajobs.dataModel.EducationData
import tech.merajobs.databinding.ItemBranchBinding
import tech.merajobs.databinding.ItemEducationBinding
import tech.merajobs.utility.AppConstant
import kotlin.random.Random


class BranchAdapter(
    private var dataList: ArrayList<BranchList>,
    private var requireActivity: Activity,
    private var editable : Boolean
) :
    RecyclerView.Adapter<BranchAdapter.ViewHolder>() {

    interface OnRequestAction {
        fun editItem(item: BranchList)
   }

    private var listener: OnRequestAction? = null

    fun setOnRequestAction(listener: OnRequestAction) {
        this.listener = listener
    }

    class ViewHolder(val binding: ItemBranchBinding, private var requireActivity: Activity) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: BranchList) {

            binding.etBranchName.text = dataItem.branch_name
            binding.etAddress.text = dataItem.address
            binding.etCity.text = dataItem.city_name
            binding.etState.text = dataItem.state_name
            binding.etCountry.text = dataItem.country_name
            binding.etZip.text = dataItem.zip


        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBranchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, requireActivity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])

        holder.binding.btMenu.setOnClickListener {
            listener?.editItem(dataList[position])
        }

        if (editable){
            holder.binding.btMenu.visibility = View.VISIBLE
        }else{
            holder.binding.btMenu.visibility = View.GONE
        }

        holder.binding.tvHead.visibility = View.GONE

        holder.bind(dataList[position])

    }


    override fun getItemCount(): Int {
        return dataList.size
    }
}


