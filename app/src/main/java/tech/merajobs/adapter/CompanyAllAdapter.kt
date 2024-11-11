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
import tech.merajobs.databinding.ItemCompanyAllBinding
import tech.merajobs.databinding.ItemCompanyBinding
import tech.merajobs.utility.AppConstant


class CompanyAllAdapter(private var dataList: ArrayList<CompanyList> , private var requireActivity : Activity) :
    RecyclerView.Adapter<CompanyAllAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemCompanyAllBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: CompanyList ,requireActivity: Activity) {
            Glide.with(requireActivity)
                .load(AppConstant.MEDIA_BASE_URL + dataItem.company_logo)
                .placeholder(R.drawable.company_icon)
                .into(binding.tvCompanyImage)

            binding.tvCompanyName.text = dataItem.company_name
            binding.tvCompanyJobs.text =  "( ${dataItem.job_count} Jobs )"
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCompanyAllBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position] ,requireActivity)

        holder.itemView.setOnClickListener(){
            val bundle = Bundle()
            bundle.putString(AppConstant.PAGE_TITLE, dataList[position].company_name)
            bundle.putString(AppConstant.EMPLOYER_ID, dataList[position].id)
            findNavController(holder.itemView).navigate(R.id.openSearchJobScreen, bundle)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}


