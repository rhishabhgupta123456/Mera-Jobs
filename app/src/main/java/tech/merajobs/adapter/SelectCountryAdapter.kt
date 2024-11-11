package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.merajobs.dataModel.CountryList
import tech.merajobs.databinding.SelectCountryItemBinding


class SelectCountryAdapter(private var selectCountryList: ArrayList<CountryList>, var requireContext: Context):RecyclerView.Adapter<SelectCountryAdapter.Holder>() {
    class Holder(val binding: SelectCountryItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(dataItem: CountryList) {
            binding.checkboxCountry.text = dataItem.country_name
            binding.checkboxCountry.isChecked = dataItem.status

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = SelectCountryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
       return selectCountryList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.binding.checkboxCountry.setOnCheckedChangeListener { _, isChecked ->
            selectCountryList[position].status = isChecked
        }

        holder.bind(selectCountryList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: ArrayList<CountryList>) {
        selectCountryList = newData.toMutableList() as ArrayList<CountryList>
        notifyDataSetChanged()
    }

}