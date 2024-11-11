package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.merajobs.R
import tech.merajobs.dataModel.CompanyDataModel
import tech.merajobs.dataModel.CompanyList
import tech.merajobs.dataModel.IndustryList
import tech.merajobs.databinding.ItemCompanyAllBinding
import tech.merajobs.databinding.ItemCompanyBinding
import tech.merajobs.databinding.ItemIndustryAllBinding
import tech.merajobs.utility.AppConstant


class IndustryAllAdapter(private var dataList: ArrayList<IndustryList>, private var requireActivity : Activity) :
    RecyclerView.Adapter<IndustryAllAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemIndustryAllBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: IndustryList ,requireActivity: Activity) {
            Glide.with(requireActivity)
                .load(AppConstant.MEDIA_BASE_URL + dataItem.industryImage)
                .placeholder(R.drawable.company_icon)
                .into(binding.tvCompanyImage)

            binding.tvCompanyName.text = dataItem.industry_name
            binding.tvCompanyJobs.text =  "( ${dataItem.job_count} Jobs )"
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemIndustryAllBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position] ,requireActivity)

        holder.itemView.setOnClickListener(){
            val bundle = Bundle()
            bundle.putString(AppConstant.PAGE_TITLE, dataList[position].industry_name)
            bundle.putString(AppConstant.EMPLOYER_ID, dataList[position].id)
            findNavController(holder.itemView).navigate(R.id.openSearchJobScreen, bundle)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}


