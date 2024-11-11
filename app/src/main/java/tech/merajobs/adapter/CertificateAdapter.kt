package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import tech.merajobs.R
import tech.merajobs.dataModel.CertificateList
import tech.merajobs.databinding.ItemCertificateBinding


class CertificateAdapter(
    private var dataList: ArrayList<CertificateList>,
    private var requireActivity: Activity,
    private var editable : Boolean
) :
    RecyclerView.Adapter<CertificateAdapter.ViewHolder>() {

    interface OnRequestAction {
        fun editItem(item: CertificateList)
        fun deleteItem(item: CertificateList)
    }

    private var listener: OnRequestAction? = null

    fun setOnRequestAction(listener: OnRequestAction) {
        this.listener = listener
    }

    class ViewHolder(val binding: ItemCertificateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: CertificateList) {
            binding.tvCertificateName.text = dataItem.certificate
            binding.tvCertificateNumber.text = "Certificate Number : " + dataItem.certificate_no
            binding.tvInstitute.text = dataItem.institute
            binding.tvValid.text = "Valid Till : " + dataItem.valid_till
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCertificateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])

        holder.binding.btMenu.setOnClickListener {
            openMenu(holder.binding.btMenu, dataList[position])
        }

        holder.binding.tvHead.text = "${position + 1}"


        if (editable){
            holder.binding.btMenu.visibility = View.VISIBLE
        }else{
            holder.binding.btMenu.visibility = View.GONE
        }

        holder.bind(dataList[position])

    }

    // This Function is used for open Popup Menu
    private fun openMenu(item: ImageView, dataItem: CertificateList) {
        val popupMenu = PopupMenu(requireActivity, item)
        popupMenu.menuInflater.inflate(R.menu.profile_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked
            when (menuItem.itemId) {
                R.id.btEdit -> {
                    listener?.editItem(dataItem)
                }

                R.id.btDelete -> {
                    listener?.deleteItem(dataItem)
                }
            }
            true
        }


        // Showing the popup menu
        popupMenu.show()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}


