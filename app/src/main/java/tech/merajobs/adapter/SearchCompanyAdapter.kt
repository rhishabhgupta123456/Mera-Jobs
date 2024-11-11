package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.merajobs.R
import tech.merajobs.dataModel.EmployerList
import tech.merajobs.databinding.ItemBlockedCompanyBinding
import tech.merajobs.utility.AppConstant
import kotlin.random.Random


class SearchCompanyAdapter(private var dataList: ArrayList<EmployerList>, private var  requireActivity : Activity) :
    RecyclerView.Adapter<SearchCompanyAdapter.ViewHolder>() {

    interface OnRequestAction {
        fun blockedCompany(item: EmployerList, position: Int)
        fun unblockedCompany(item: EmployerList, position: Int)
    }

    private var listener: OnRequestAction? = null

    fun setOnRequestAction(listener: OnRequestAction) {
        this.listener = listener
    }

    class ViewHolder(val binding: ItemBlockedCompanyBinding, private var requireActivity: Activity) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: EmployerList, requireActivity: Activity) {
            
            binding.tvCompanyName.text = dataItem.company_name

           if (dataItem.is_blocked == 1){
                binding.btBlock.text = requireActivity.getString(R.string.UnBlock)
                binding.btBlock.setBackgroundResource(R.drawable.blue_button)
            }else{
                binding.btBlock.text = requireActivity.getString(R.string.Block)
                binding.btBlock.setBackgroundResource(R.drawable.red_button)
            }


            if (dataItem.company_logo == null || dataItem.company_logo == "") {
                binding.tvLogo.visibility = View.GONE
                binding.tvDefalutImage.visibility = View.VISIBLE

                if (dataItem.company_name != null) {
                    if (dataItem.company_name!!.split(" ").filter { it.isNotEmpty() }
                            .joinToString("") { it[0].uppercase() }.length >= 2) {
                        binding.tvDefalutImage.text =
                            dataItem.company_name!!.split(" ").filter { it.isNotEmpty() }
                                .joinToString("") { it[0].uppercase() }.substring(0, 2)
                    } else {
                        binding.tvDefalutImage.text =
                            dataItem.company_name!!.split(" ").filter { it.isNotEmpty() }
                                .joinToString("") { it[0].uppercase() }
                    }
                } else {
                    binding.tvDefalutImage.text = ""
                }

                when (Random.nextInt(1, 11)) {
                    1 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor1
                            )
                        )
                        binding.tvDefalutImage.setTextColor(this.requireActivity.resources.getColor(R.color.textColor1))
                    }

                    2 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor2
                            )
                        )
                        binding.tvDefalutImage.setTextColor(this.requireActivity.resources.getColor(R.color.textColor2))
                    }

                    3 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor3
                            )
                        )
                        binding.tvDefalutImage.setTextColor(this.requireActivity.resources.getColor(R.color.textColor3))
                    }

                    4 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor4
                            )
                        )
                        binding.tvDefalutImage.setTextColor(this.requireActivity.resources.getColor(R.color.textColor4))
                    }

                    5 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor5
                            )
                        )
                        binding.tvDefalutImage.setTextColor(this.requireActivity.resources.getColor(R.color.textColor5))
                    }

                    6 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor6
                            )
                        )
                        binding.tvDefalutImage.setTextColor(this.requireActivity.resources.getColor(R.color.textColor6))
                    }

                    7 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor7
                            )
                        )
                        binding.tvDefalutImage.setTextColor(this.requireActivity.resources.getColor(R.color.textColor7))
                    }

                    else -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            this.requireActivity.resources.getColor(
                                R.color.bgColor8
                            )
                        )
                        binding.tvDefalutImage.setTextColor(this.requireActivity.resources.getColor(R.color.textColor8))
                    }
                }

            } else {
                binding.tvLogo.visibility = View.VISIBLE
                binding.tvDefalutImage.visibility = View.GONE

                Glide.with(this.requireActivity).load(AppConstant.MEDIA_BASE_URL + dataItem.company_logo)
                    .into(binding.tvLogo)
            }
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBlockedCompanyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding ,requireActivity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.btBlock.setOnClickListener {
            if (holder.binding.btBlock.text == requireActivity.getString(R.string.Block)){
                dataList[position].is_blocked = 1
                listener?.blockedCompany(dataList[position],position)

            }else{
                dataList[position].is_blocked = 0
                listener?.unblockedCompany(dataList[position],position)
            }
        }


        holder.bind(dataList[position], requireActivity)

    }
 
    override fun getItemCount(): Int {
        return dataList.size
    }

    fun removeItem(position: Int) {
           notifyItemRangeChanged(position, dataList.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(searchList: ArrayList<EmployerList>) {
        dataList = searchList
        notifyDataSetChanged()
    }
}


