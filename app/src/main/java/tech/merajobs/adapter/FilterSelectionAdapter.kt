package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.merajobs.R
import tech.merajobs.dataModel.SelectionDataModel
import tech.merajobs.databinding.ItemFilterSelectorBinding


class FilterSelectionAdapter(private var dataList: ArrayList<SelectionDataModel>, var requireActivity : Activity) :
    RecyclerView.Adapter<FilterSelectionAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemFilterSelectorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: SelectionDataModel ,requireActivity : Activity) {
            var select = false
            binding.radioButton.text = dataItem.optionName

            binding.cardBox.setOnClickListener() {
                if (select) {
                    binding.radioButton.setBackgroundColor(requireActivity.resources.getColor(R.color.unselectButton))
                    binding.radioButton.setTextColor(requireActivity.resources.getColor(R.color.shadowTextColor))
                    select = false
                } else {
                    binding.radioButton.setBackgroundColor(requireActivity.resources.getColor(R.color.selectButton))
                    binding.radioButton.setTextColor(requireActivity.resources.getColor(R.color.white))
                    select = true

                }
            }
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFilterSelectorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position],requireActivity)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}


