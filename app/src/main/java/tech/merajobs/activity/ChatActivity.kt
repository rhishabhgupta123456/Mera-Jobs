package tech.merajobs.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import tech.merajobs.R
import tech.merajobs.adapter.ChatAdapter
import tech.merajobs.dataModel.ChatList
import tech.merajobs.dataModel.ChatMessage
import tech.merajobs.databinding.ActivityChatBinding
import tech.merajobs.networkModel.homeViewModel.HomeViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseActivity
import tech.merajobs.utility.MediaUtility
import tech.merajobs.utility.SessionManager
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.random.Random

class ChatActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityChatBinding
    lateinit var sessionManager: SessionManager
    var userName: String? = null
    var userProfilePicture: String? = null
    var ChannelId: String? = null

    lateinit var adapter: ChatAdapter
    var selectfile: File? = null
    var selectFileType: Int? = null

    lateinit var homeViewModel: HomeViewModel


    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        try {
            val data = result.data
            if (data != null) {
                val pdfUri: Uri? = data.data!!
                Log.e("PDF", pdfUri.toString())
                if (pdfUri != null) {
                    val pdfFile = getFileFromUri(this, pdfUri)
                    Log.e("Pdf File ", pdfFile!!.name.toString())
                    Log.e("Pdf File Path ", pdfFile.path.toString())
                    if (!isNetworkAvailable()) {
                        alertErrorDialog(getString(R.string.no_internet))
                    } else {
                        binding.fileSelectBox.visibility = View.VISIBLE
                        binding.tvFileName.text = pdfFile.name
                        selectfile = pdfFile
                    }

                } else {
                    Log.e("PDF", "No PDF selected")
                }

            }
        } catch (e: Exception) {
            alertErrorDialog(e.toString())
        }
    }

    // This Function is used for convert URI in FILE
    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        inputStream?.let {
            val tempFile = File(context.cacheDir, "tempFile.pdf")
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            return tempFile
        }
        return null
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    finish()
                }
            }

        onBackPressedDispatcher.addCallback(this, callback)


        binding.bottomSelect.visibility = View.GONE

        ChannelId = intent.getStringExtra(AppConstant.CHANNEL_ID)
        userProfilePicture = intent.getStringExtra(AppConstant.RECEIVER_PROFILE_PICTURE)
        userName = intent.getStringExtra(AppConstant.RECEIVER_NAME)

        binding.btBack.setOnClickListener(this)
        binding.btSendMessage.setOnClickListener(this)
        binding.btAttachFile.setOnClickListener(this)
        binding.btAttachImage.setOnClickListener(this)
        binding.btMenu.setOnClickListener(this)
        binding.btRemoveFile.setOnClickListener(this)
        binding.btPlus.setOnClickListener(this)


        binding.main.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && binding.bottomSelect.visibility == View.VISIBLE) {
                binding.bottomSelect.visibility = View.GONE
            }
            true
        }

        binding.chatRecyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (binding.bottomSelect.visibility == View.VISIBLE) {
                    binding.bottomSelect.visibility = View.GONE
                }

                if (adapter.activeDeletePosition != null) {
                    adapter.hideActiveDeleteIcon()
                }
            }
            // Return false to allow RecyclerView to handle scrolling and other gestures
            false
        }

        setUserDetail()

    }

    private fun getChatHistory() {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {

            homeViewModel.getChatHistory(sessionManager.getBearerToken(), ChannelId!!.toInt())
                .observe(this@ChatActivity) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val channelList =
                                Gson().fromJson(jsonObjectData, ChatMessage::class.java)

                            adapter = ChatAdapter(
                                channelList.data,
                                sessionManager.getUserID(),
                                this@ChatActivity
                            )
                            binding.chatRecyclerView.adapter = adapter

                            binding.chatRecyclerView.layoutManager =
                                LinearLayoutManager(this@ChatActivity).apply {
                                    stackFromEnd = true
                                }


                            adapter.setOnRequestAction(object : ChatAdapter.OnRequestAction {
                                override fun deleteMessage(chatMessages: ChatList) {
                                    deleteParticularMessage(chatMessages)
                                }

                            })


                        } catch (e: Exception) {
                            alertErrorDialog(e.message.toString())
                        }
                    }
                }
        }

    }


    private fun setUserDetail() {
        binding.tvName.text = userName
        binding.tvRole.text = ""

        if (userProfilePicture == null || userProfilePicture == "") {
            binding.tvLogo.visibility = View.GONE
            binding.tvDefalutImage.visibility = View.VISIBLE

            if (userName != null) {
                if (userName!!.split(" ").filter { it.isNotEmpty() }
                        .joinToString("") { it[0].uppercase() }.length >= 2) {
                    binding.tvDefalutImage.text =
                        userName!!.split(" ").filter { it.isNotEmpty() }
                            .joinToString("") { it[0].uppercase() }.substring(0, 2)
                } else {
                    binding.tvDefalutImage.text =
                        userName!!.split(" ").filter { it.isNotEmpty() }
                            .joinToString("") { it[0].uppercase() }
                }
            } else {
                binding.tvDefalutImage.text = null
            }

            when (Random.nextInt(1, 11)) {
                1 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor1
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor1))
                }

                2 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor2
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor2))
                }

                3 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor3
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor3))
                }

                4 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor4
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor4))
                }

                5 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor5
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor5))
                }

                6 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor6
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor6))
                }

                7 -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor7
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor7))
                }

                else -> {
                    binding.tvDefalutImage.setBackgroundColor(
                        resources.getColor(
                            R.color.bgColor8
                        )
                    )
                    binding.tvDefalutImage.setTextColor(resources.getColor(R.color.textColor8))
                }
            }

        } else {
            binding.tvLogo.visibility = View.VISIBLE
            binding.tvDefalutImage.visibility = View.GONE

            Glide.with(this)
                .load(AppConstant.MEDIA_BASE_URL + userProfilePicture)
                .into(binding.tvLogo)
        }

        getChatHistory()


    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btBack -> {
                finish()
            }

            R.id.btMenu -> {
                openMenu()
            }

            R.id.btPlus -> {
                binding.bottomSelect.visibility = View.VISIBLE
            }

            R.id.btRemoveFile -> {
                binding.fileSelectBox.visibility = View.GONE
                selectfile = null
                selectFileType = null
            }

            R.id.btSendMessage -> {
                if (binding.etMessage.text.toString().isNotEmpty()) {
                    sendMessage(binding.etMessage.text.toString(), selectfile)
                }
            }

            R.id.btAttachFile -> {
                selectFileType = 1
                selectPDF()
            }

            R.id.btAttachImage -> {
                selectFileType = 0
                browseCameraAndGallery()
            }


        }
    }


    private fun openMenu() {
        val popupMenu = PopupMenu(this, binding.btMenu)
        popupMenu.menuInflater.inflate(R.menu.chat_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked
            when (menuItem.itemId) {

                R.id.btClear -> {
                    clearChatDialog()
                }


                R.id.btDelete -> {
                    deleteChatDialog()
                }

            }
            true
        }


        // Showing the popup menu
        popupMenu.show()
    }

    private fun clearChatDialog() {
        val alertErrorDialog = Dialog(this)
        alertErrorDialog.setCancelable(false)
        alertErrorDialog.setContentView(R.layout.alertbox_closeapp)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertErrorDialog.window!!.attributes)

        alertErrorDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertErrorDialog.window!!.attributes = layoutParams

        val tvTitle: TextView = alertErrorDialog.findViewById(R.id.tv_title)
        val btnYes: TextView = alertErrorDialog.findViewById(R.id.btnYes)
        val btnNo: TextView = alertErrorDialog.findViewById(R.id.btnNo)
        tvTitle.text = "Are you want to clear the whole chat history"

        btnYes.setOnClickListener {
            clearAllChat()
            alertErrorDialog.dismiss()
        }

        btnNo.setOnClickListener {
            alertErrorDialog.dismiss()
        }

        alertErrorDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertErrorDialog.show()
    }

    private fun deleteChatDialog() {
        val alertErrorDialog = Dialog(this)
        alertErrorDialog.setCancelable(false)
        alertErrorDialog.setContentView(R.layout.alertbox_closeapp)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertErrorDialog.window!!.attributes)

        alertErrorDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertErrorDialog.window!!.attributes = layoutParams

        val tvTitle: TextView = alertErrorDialog.findViewById(R.id.tv_title)
        val btnYes: TextView = alertErrorDialog.findViewById(R.id.btnYes)
        val btnNo: TextView = alertErrorDialog.findViewById(R.id.btnNo)
        tvTitle.text = "Are you want to delete the channel"

        btnYes.setOnClickListener {
            deleteChannel()
            alertErrorDialog.dismiss()
        }

        btnNo.setOnClickListener {
            alertErrorDialog.dismiss()
        }

        alertErrorDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertErrorDialog.show()
    }

    private fun browseCameraAndGallery() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose Image", "Cancel")
        val builder = AlertDialog.Builder(
            this
        )
        builder.setTitle("Choose File")
        builder.setItems(
            items
        ) { dialog: DialogInterface, item: Int ->
            if (items[item] == "Take Photo") {
                try {
                    cameraIntent()
                } catch (e: java.lang.Exception) {
                    Log.v("Exception", e.message!!)
                }
            } else if (items[item] == "Choose Image") {
                try {
                    galleryIntent()
                } catch (e: java.lang.Exception) {
                    Log.v("Exception", e.message!!)
                }
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectPDF()
        } else {
            alertErrorDialog("Permission Denied")
        }
    }

    private fun selectPDF() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        Intent.createChooser(intent, "Select PDF")
        resultLauncher.launch(intent)
    }


    // this function is used for open the camera
    private fun cameraIntent() {
        ImagePicker.with(this).crop(150f, 150f).cameraOnly().compress(1024).maxResultSize(
            1080, 1080
        ).start()
    }

    // this function is used for open the gallery
    private fun galleryIntent() {
        ImagePicker.with(this).crop(150f, 150f).galleryOnly().compress(1024).maxResultSize(
            1080, 1080
        ).start()
    }

    @Deprecated("Deprecated in Java")
    // this function is used for to get image from camera or gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ImagePicker.REQUEST_CODE) {
            data?.let {
                onSelectFromGalleryResultant(it)
            }
        }


    }

    // this function is used for to get image from gallery
    private fun onSelectFromGalleryResultant(data: Intent) {
        try {
            if (!isNetworkAvailable()) {
                alertErrorDialog(getString(R.string.no_internet))
            } else {
                selectfile = File(MediaUtility.getPath(this, data.data!!))
                binding.fileSelectBox.visibility = View.VISIBLE
                binding.tvFileName.text = selectfile!!.name
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun sendMessage(message: String?, file: File?) {

        val messagePart = message?.toRequestBody("text/plain".toMediaTypeOrNull())

        val body = if (selectFileType == 0) {
            file?.asRequestBody("application/octet-stream".toMediaType())
        } else {
            file?.asRequestBody("application/pdf".toMediaType())
        }

        val part = if (file != null) {
            if (selectFileType == 0) {
                body?.let {
                    MultipartBody.Part.createFormData("attachment", file.name, it)
                }
            } else {
                body?.let {
                    MultipartBody.Part.createFormData("attachment", file.name, it)
                }
            }
        } else {
            null
        }


        lifecycleScope.launch {
            homeViewModel.sendMessage(
                sessionManager.getBearerToken(), ChannelId!!.toInt(), messagePart, part
            ).observe(this@ChatActivity) { jsonObject ->
                val jsonObjectData = checkResponse(jsonObject)
                Log.e("Edit Profile", "True")
                if (jsonObjectData != null) {
                    try {

                        getChatHistory()
                        binding.etMessage.text = null
                        binding.fileSelectBox.visibility = View.GONE
                        selectfile = null
                        selectFileType = null

                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }
        }

    }

    private fun deleteChannel() {
        binding.tvProgressBar.visibility = View.VISIBLE

        val channelList = ArrayList<Int>()
        channelList.add(ChannelId!!.toInt())

        lifecycleScope.launch {

            homeViewModel.deleteChannel(sessionManager.getBearerToken(), channelList)
                .observe(this@ChatActivity) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            finish()
                        } catch (e: Exception) {
                            alertErrorDialog(e.message.toString())
                        }
                    }
                }
        }

    }

    private fun clearAllChat() {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {

            homeViewModel.clearAllChat(
                sessionManager.getBearerToken(),
                ChannelId!!.toInt(),
                sessionManager.getUserID()
            )
                .observe(this@ChatActivity) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            getChatHistory()
                        } catch (e: Exception) {
                            alertErrorDialog(e.message.toString())
                        }
                    }
                }
        }

    }

    private fun deleteParticularMessage(item: ChatList) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            homeViewModel.deleteParticularMessage(sessionManager.getBearerToken(), item.id)
                .observe(this@ChatActivity) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            adapter.hideActiveDeleteIcon()
                            getChatHistory()
                        } catch (e: Exception) {
                            alertErrorDialog(e.message.toString())
                        }
                    }
                }
        }
    }


}