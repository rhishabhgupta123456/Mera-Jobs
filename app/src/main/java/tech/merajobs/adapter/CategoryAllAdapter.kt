package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.merajobs.R
import tech.merajobs.dataModel.CategoryDataModel
import tech.merajobs.dataModel.CategoryList
import tech.merajobs.databinding.ItemCategoryAllBinding
import tech.merajobs.utility.AppConstant


class CategoryAllAdapter(private var dataList: ArrayList<CategoryList> , private var requireActivity : Activity) :
    RecyclerView.Adapter<CategoryAllAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemCategoryAllBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: CategoryList ,requireActivity : Activity) {
            Glide.with(requireActivity)
                .load(AppConstant.MEDIA_BASE_URL + dataItem.categoryImage)
                .placeholder(R.drawable.category1)
                .into(binding.tvCategoryImage)

            binding.tvCategoryName.text = dataItem.role
            binding.tvCategoryJobs.text =  "( ${dataItem.job_count} Jobs )"
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCategoryAllBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position] , requireActivity)

        holder.itemView.setOnClickListener(){
            val bundle = Bundle()
            bundle.putString(AppConstant.PAGE_TITLE, dataList[position].role)
            bundle.putString(AppConstant.ROLE, dataList[position].role_id)
            findNavController(holder.itemView).navigate(R.id.openSearchJobScreen, bundle)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}


