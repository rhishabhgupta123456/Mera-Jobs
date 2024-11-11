package tech.merajobs.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.merajobs.R
import tech.merajobs.activity.ChatActivity
import tech.merajobs.dataModel.MessageInboxList
import tech.merajobs.databinding.ItemMessageInboxBinding
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.SessionManager
import kotlin.random.Random


class MessageInboxAdapter(
    private var notificationList: ArrayList<MessageInboxList>,
    var requireContext: Activity,
) : RecyclerView.Adapter<MessageInboxAdapter.Holder>() {


    interface OnRequestAction {
        fun selectItem(select: Boolean)
    }

    private var isMultiSelectMode = false

    private var listener: OnRequestAction? = null

    fun setOnRequestAction(listener: OnRequestAction) {
        this.listener = listener
    }

    class Holder(val binding: ItemMessageInboxBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dataItem: MessageInboxList, requireContext: Activity) {



            binding.tvName.text = dataItem.to_name

            binding.tvLastMessage.text = dataItem.last_message
            binding.tvLastSeen.text = dataItem.last_time_online

            if (dataItem.last_message_seen_status == 1) {
                binding.tvLastMessageView.visibility = View.VISIBLE
            } else {
                binding.tvLastMessageView.visibility = View.GONE
            }

            if (dataItem.to_online_status == 1) {
                binding.tvOnline.visibility = View.VISIBLE
            } else {
                binding.tvOnline.visibility = View.GONE
            }

            if (dataItem.isSelected) {
                binding.tvSelect.visibility = View.VISIBLE
            } else {
                binding.tvSelect.visibility = View.GONE
            }

            if (dataItem.to_photo == null || dataItem.to_photo == "") {
                binding.tvLogo.visibility = View.GONE
                binding.tvDefalutImage.visibility = View.VISIBLE

                if (dataItem.to_name != null) {
                    if (dataItem.to_name.split(" ").filter { it.isNotEmpty() }
                            .joinToString("") { it[0].uppercase() }.length >= 2) {
                        binding.tvDefalutImage.text =
                            dataItem.to_name.split(" ").filter { it.isNotEmpty() }
                                .joinToString("") { it[0].uppercase() }.substring(0, 2)
                    } else {
                        binding.tvDefalutImage.text =
                            dataItem.to_name.split(" ").filter { it.isNotEmpty() }
                                .joinToString("") { it[0].uppercase() }
                    }
                } else {
                    binding.tvDefalutImage.text = null
                }

                when (Random.nextInt(1, 11)) {
                    1 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireContext.resources.getColor(
                                R.color.bgColor1
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireContext.resources.getColor(R.color.textColor1))
                    }

                    2 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireContext.resources.getColor(
                                R.color.bgColor2
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireContext.resources.getColor(R.color.textColor2))
                    }

                    3 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireContext.resources.getColor(
                                R.color.bgColor3
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireContext.resources.getColor(R.color.textColor3))
                    }

                    4 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireContext.resources.getColor(
                                R.color.bgColor4
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireContext.resources.getColor(R.color.textColor4))
                    }

                    5 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireContext.resources.getColor(
                                R.color.bgColor5
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireContext.resources.getColor(R.color.textColor5))
                    }

                    6 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireContext.resources.getColor(
                                R.color.bgColor6
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireContext.resources.getColor(R.color.textColor6))
                    }

                    7 -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireContext.resources.getColor(
                                R.color.bgColor7
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireContext.resources.getColor(R.color.textColor7))
                    }

                    else -> {
                        binding.tvDefalutImage.setBackgroundColor(
                            requireContext.resources.getColor(
                                R.color.bgColor8
                            )
                        )
                        binding.tvDefalutImage.setTextColor(requireContext.resources.getColor(R.color.textColor8))
                    }
                }

            } else {
                binding.tvLogo.visibility = View.VISIBLE
                binding.tvDefalutImage.visibility = View.GONE

                Glide.with(requireContext)
                    .load(AppConstant.MEDIA_BASE_URL + dataItem.to_photo)
                    .into(binding.tvLogo)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemMessageInboxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val dataItem = notificationList[position]
        holder.bind(dataItem, requireContext)


        holder.itemView.setOnClickListener() {
            if (isMultiSelectMode) {
                toggleSelection(position)
            } else {
                // Open chat as before
                val intent = Intent(requireContext, ChatActivity::class.java)
                intent.putExtra(AppConstant.CHANNEL_ID, dataItem.id)
                intent.putExtra(AppConstant.RECEIVER_NAME, dataItem.to_name)
                intent.putExtra(AppConstant.RECEIVER_PROFILE_PICTURE, dataItem.to_photo)
                requireContext.startActivity(intent)
            }
        }

        holder.itemView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // When the button is pressed, change the background color
                    holder.itemView.setBackgroundColor(requireContext.resources.getColor(R.color.pressColor))
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // When the press is released, reset the background color
                    holder.itemView.setBackgroundColor(requireContext.resources.getColor(R.color.white))
                }
            }
            false
        }

        holder.itemView.setOnLongClickListener {
            if (!isMultiSelectMode) {
                isMultiSelectMode = true
            }
            toggleSelection(position)
            true
        }


    }

    private fun toggleSelection(position: Int) {
        val item = notificationList[position]
        item.isSelected = !item.isSelected
        val isSelected = notificationList.any { it.isSelected }
        listener?.selectItem(isSelected)
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearSelection() {
        notificationList.forEach {
            it.isSelected = false
        }
        isMultiSelectMode = false
        listener?.selectItem(false)
        notifyDataSetChanged()
    }


    fun isMultiSelectActive(): Boolean {
        return isMultiSelectMode
    }



}