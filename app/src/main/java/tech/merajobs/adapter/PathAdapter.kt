package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.merajobs.dataModel.PathDataModel
import tech.merajobs.databinding.ItemPathBinding


class PathAdapter(private var dataList: ArrayList<PathDataModel> , private var requireContext : Context) :
    RecyclerView.Adapter<PathAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemPathBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: PathDataModel) {
            binding.tvPathName.text = dataItem.pathName

        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPathBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])

        holder.itemView.setOnClickListener(){
            dataList.removeAt(position)
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateList(newItem: PathDataModel) {
        dataList.add(newItem)
        notifyDataSetChanged()
    }
}


