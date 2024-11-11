package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.merajobs.dataModel.RadioDataModel
import tech.merajobs.databinding.ItemFilterRadioButtonBinding
import tech.merajobs.databinding.ItemKeyPointBinding


class KeyPointsAdapter(private var dataList: ArrayList<String>) :
    RecyclerView.Adapter<KeyPointsAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemKeyPointBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: String) {
            binding.tvKeyPoints.text = dataItem
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemKeyPointBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}


