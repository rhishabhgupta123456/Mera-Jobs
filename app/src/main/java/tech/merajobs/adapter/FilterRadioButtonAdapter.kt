package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.merajobs.dataModel.RadioDataModel
import tech.merajobs.databinding.ItemFilterRadioButtonBinding


class FilterRadioButtonAdapter(private var dataList: ArrayList<RadioDataModel>) :
    RecyclerView.Adapter<FilterRadioButtonAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemFilterRadioButtonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: RadioDataModel) {
            binding.radioButton.text = dataItem.optionName
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFilterRadioButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}


