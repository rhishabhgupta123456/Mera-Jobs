package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.merajobs.dataModel.PathDataModel
import tech.merajobs.databinding.ItemPathBinding
import tech.merajobs.databinding.ItemTextBinding


class TextAdapter(private var dataList: ArrayList<PathDataModel>) :
    RecyclerView.Adapter<TextAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemTextBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: PathDataModel) {
            binding.tvName.text = dataItem.pathName
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}


