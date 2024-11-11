package tech.merajobs.adapter


import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.merajobs.R
import tech.merajobs.dataModel.ChatList
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.FileDownloader
import tech.merajobs.utility.SessionManager
import java.net.URL

class ChatAdapter(
    private val chatMessages: MutableList<ChatList>,
    private var senderID: Int,
    private var requireContext: Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Track the currently active delete icon position
    var activeDeletePosition: Int? = null

    interface OnRequestAction {
        fun deleteMessage(chatMessage: ChatList)
    }

    private var listener: OnRequestAction? = null

    fun setOnRequestAction(listener: OnRequestAction) {
        this.listener = listener
    }

    companion object {
        const val VIEW_TYPE_TEXT_SENT = 1
        const val VIEW_TYPE_TEXT_RECEIVED = 2
        const val VIEW_TYPE_PDF_SENT = 3
        const val VIEW_TYPE_PDF_RECEIVED = 4
        const val VIEW_TYPE_IMAGE_SENT = 5
        const val VIEW_TYPE_IMAGE_RECEIVED = 6
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int {
        val message = chatMessages[position]
        return if (message.sender_id == senderID) {
            if (message.attachment != null) {
                val ext = getFileExtensionFromUrl(AppConstant.MEDIA_BASE_URL + message.attachment)
                Log.e("Extension", ext.toString())
                when (ext?.lowercase()) {
                    "jpg", "jpeg", "png" -> VIEW_TYPE_IMAGE_SENT
                    else -> VIEW_TYPE_PDF_SENT
                }
            } else {
                VIEW_TYPE_TEXT_SENT
            }
        } else {
            if (message.attachment != null) {
                val ext = getFileExtensionFromUrl(AppConstant.MEDIA_BASE_URL + message.attachment)
                Log.e("Extension", ext.toString())
                when (ext?.lowercase()) {
                    "jpg", "jpeg", "png" -> VIEW_TYPE_IMAGE_RECEIVED
                    else -> VIEW_TYPE_PDF_RECEIVED
                }
            } else {
                VIEW_TYPE_TEXT_RECEIVED
            }
        }
    }

    private fun getFileExtensionFromUrl(url: String): String? {
        return try {
            val fileName = URL(url).path.substringAfterLast('/')
            fileName.substringAfterLast('.', "")
        } catch (e: Exception) {
            null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TEXT_SENT -> TextViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_text_sent, parent, false)
            )
            VIEW_TYPE_TEXT_RECEIVED -> TextViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_text_received, parent, false)
            )
            VIEW_TYPE_IMAGE_SENT -> ImageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_image_sent, parent, false)
            )
            VIEW_TYPE_IMAGE_RECEIVED -> ImageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_image_received, parent, false)
            )
            VIEW_TYPE_PDF_SENT -> PdfViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_sent, parent, false)
            )
            VIEW_TYPE_PDF_RECEIVED -> PdfViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_received, parent, false)
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = chatMessages[position]
        when (holder) {
            is TextViewHolder -> holder.bind(message, position)
            is ImageViewHolder -> holder.bind(message, position)
            is PdfViewHolder -> holder.bind(message, position)
        }
    }

    override fun getItemCount(): Int = chatMessages.size

    override fun getItemId(position: Int): Long {
        // Assuming each ChatList item has a unique ID, replace `id` with the actual unique identifier
        return chatMessages[position].id.hashCode().toLong()
    }

    // ViewHolder for Text Messages
    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.text_message)
        private val deleteIcon: CardView = itemView.findViewById(R.id.deleteIcon)

        @SuppressLint("ClickableViewAccessibility")
        fun bind(message: ChatList, position: Int) {
            // Set message text
            textView.text = message.message

            // Show or hide delete icon based on message state
            deleteIcon.visibility = if (message.deleteShow == 1) View.VISIBLE else View.GONE

            // Delete icon click listener
            deleteIcon.setOnClickListener {
                listener?.deleteMessage(message)
            }

            // Item click listener to hide delete icons
            itemView.setOnClickListener {
                hideActiveDeleteIcon()
            }

            // Long click to show delete icon
            itemView.setOnLongClickListener {
                if (activeDeletePosition != position) {
                    hideActiveDeleteIcon()
                    message.deleteShow = 1
                    activeDeletePosition = position
                    notifyItemChanged(position)
                }
                true
            }
        }
    }

    // ViewHolder for PDF Messages
    inner class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pdfBox: ConstraintLayout = itemView.findViewById(R.id.pdfBox)
        private val pdfView: TextView = itemView.findViewById(R.id.pdf_message)
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val deleteIcon: CardView = itemView.findViewById(R.id.deleteIcon)

        @SuppressLint("SetTextI18n")
        fun bind(message: ChatList, position: Int) {
            // Set message and attachment text
            pdfView.text = message.attachment ?: ""
            tvMessage.text = message.message ?: ""

            // Show or hide delete icon
            deleteIcon.visibility = if (message.deleteShow == 1) View.VISIBLE else View.GONE

            // Delete icon click listener
            deleteIcon.setOnClickListener {
                listener?.deleteMessage(message)
            }

            // PDF box click listener to download file
            pdfBox.setOnClickListener {
                hideActiveDeleteIcon()
                Toast.makeText(requireContext, "File Downloading Started..", Toast.LENGTH_SHORT).show()
                FileDownloader.downloadFile(
                    AppConstant.MEDIA_BASE_URL + (message.attachment ?: ""), itemView.context
                )
            }

            // Item click listener to hide delete icons
            itemView.setOnClickListener {
                hideActiveDeleteIcon()
            }

            // Long click to show delete icon
            itemView.setOnLongClickListener {
                if (activeDeletePosition != position) {
                    hideActiveDeleteIcon()
                    message.deleteShow = 1
                    activeDeletePosition = position
                    notifyItemChanged(position)
                }
                true
            }
        }
    }

    // ViewHolder for Image Messages
    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageBox: ConstraintLayout = itemView.findViewById(R.id.imageBox)
        private val tvImageShow: ImageView = itemView.findViewById(R.id.tvImageShow)
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val deleteIcon: CardView = itemView.findViewById(R.id.deleteIcon)

        @SuppressLint("SetTextI18n")
        fun bind(message: ChatList, position: Int) {
            // Set message text
            tvMessage.text = message.message ?: ""

            // Load image using Glide
            Glide.with(requireContext)
                .load(AppConstant.MEDIA_BASE_URL + (message.attachment ?: ""))
                .into(tvImageShow)

            // Show or hide delete icon
            deleteIcon.visibility = if (message.deleteShow == 1) View.VISIBLE else View.GONE

            // Delete icon click listener
            deleteIcon.setOnClickListener {
                listener?.deleteMessage(message)
            }

            // Image box click listener to download file
            imageBox.setOnClickListener {
                hideActiveDeleteIcon()
                Toast.makeText(requireContext, "File Downloading Started..", Toast.LENGTH_SHORT).show()
                FileDownloader.downloadFile(
                    AppConstant.MEDIA_BASE_URL + (message.attachment ?: ""), itemView.context
                )
            }

            // Item click listener to hide delete icons
            itemView.setOnClickListener {
                hideActiveDeleteIcon()
            }

            // Long click to show delete icon
            itemView.setOnLongClickListener {
                if (activeDeletePosition != position) {
                    hideActiveDeleteIcon()
                    message.deleteShow = 1
                    activeDeletePosition = position
                    notifyItemChanged(position)
                }
                true
            }
        }
    }


    fun hideActiveDeleteIcon() {
        activeDeletePosition?.let { pos ->
            val previousMessage = chatMessages[pos]
            if (previousMessage.deleteShow == 1) {
                previousMessage.deleteShow = 0
                notifyItemChanged(pos)
            }
            activeDeletePosition = null
        }
    }

    /**
     * Optional: Add methods to add/remove messages dynamically
     */
    fun addMessage(message: ChatList) {
        chatMessages.add(message)
        notifyItemInserted(chatMessages.size - 1)
    }

    fun removeMessage(position: Int) {
        if (position in chatMessages.indices) {
            chatMessages.removeAt(position)
            notifyItemRemoved(position)
            if (activeDeletePosition == position) {
                activeDeletePosition = null
            } else if (activeDeletePosition != null && activeDeletePosition!! > position) {
                activeDeletePosition = activeDeletePosition!! - 1
            }
        }
    }
}
