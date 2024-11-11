package tech.merajobs.fragment.chatScreen

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.EmployerActivity
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.adapter.MessageInboxAdapter
import tech.merajobs.dataModel.MessageInboxDataModel
import tech.merajobs.dataModel.MessageInboxList
import tech.merajobs.databinding.FragmentMessageInboxBinding
import tech.merajobs.networkModel.homeViewModel.HomeViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager


class MessageInboxFragment : BaseFragment() {

    private lateinit var binding: FragmentMessageInboxBinding
    private lateinit var sessionManager: SessionManager
    lateinit var homeViewModel: HomeViewModel

    lateinit var adapter: MessageInboxAdapter
    var messageInboxList = ArrayList<MessageInboxList>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMessageInboxBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    if (adapter.isMultiSelectActive()) {
                        adapter.clearSelection()
                    } else {
                        findNavController().navigate(R.id.openHomeScreen)
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.notificationRefresh.setOnRefreshListener {
            getChannelList()
        }

        binding.btBack.setOnClickListener {
            findNavController().navigate(R.id.openHomeScreen)
        }

        binding.btDelete.setOnClickListener() {
            deleteChatDialog()
        }

        binding.btCreateAMessage.setOnClickListener() {
            newMessageDialog()
        }

    }

    private fun findSelectChannel(): List<Int> {
        return messageInboxList
            .filter { it.isSelected }
            .map { it.id.toInt() }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getChannelList() {

        binding.tvProgressBar.visibility = View.VISIBLE
        binding.noMessageBox.visibility = View.GONE

        lifecycleScope.launch {
            homeViewModel.getChannelList(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.notificationRefresh.isRefreshing = false
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val channelList =
                                Gson().fromJson(jsonObjectData, MessageInboxDataModel::class.java)
                            binding.btDelete.visibility = View.GONE
                            messageInboxList = channelList.data

                            if (messageInboxList.isEmpty()) {
                                binding.noMessageBox.visibility = View.VISIBLE
                                binding.notificationRecyclerView.visibility = View.GONE
                            } else {
                                binding.noMessageBox.visibility = View.GONE
                                binding.notificationRecyclerView.visibility = View.VISIBLE
                                adapter = MessageInboxAdapter(messageInboxList, requireActivity())
                                binding.notificationRecyclerView.adapter = adapter

                                binding.notificationRecyclerView.layoutManager =
                                    LinearLayoutManager(
                                        requireContext(),
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )

                                adapter.setOnRequestAction(object :
                                    MessageInboxAdapter.OnRequestAction {
                                    override fun selectItem(select: Boolean) {
                                        if (select) {
                                            binding.btDelete.visibility = View.VISIBLE
                                        } else {
                                            binding.btDelete.visibility = View.GONE
                                        }
                                    }

                                })
                            }
                        } catch (e: Exception) {
                            alertErrorDialog(e.message.toString())
                        }
                    }
                }
        }

    }


    override fun onResume() {
        super.onResume()
        getChannelList()
        if (sessionManager.getUserType() == AppConstant.JOB_SEEKER) {
            val activity = requireActivity() as FindJobActivity
            activity.openMessageScreen()
        } else {
            val activity = requireActivity() as EmployerActivity
            activity.openMessageScreen()
        }
    }


    private fun deleteChatDialog() {
        val alertErrorDialog = Dialog(requireActivity())
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
            deleteChannel(findSelectChannel())
            alertErrorDialog.dismiss()
        }

        btnNo.setOnClickListener {
            alertErrorDialog.dismiss()
        }

        alertErrorDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertErrorDialog.show()
    }

    private fun deleteChannel(channelIDs: List<Int>) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {

            homeViewModel.deleteChannel(sessionManager.getBearerToken(), channelIDs)
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            getChannelList()
                            binding.btDelete.visibility = View.GONE
                        } catch (e: Exception) {
                            alertErrorDialog(e.message.toString())
                        }
                    }
                }
        }

    }

    private fun clearAllChat(item: MessageInboxList) {
        binding.tvProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {

            homeViewModel.clearAllChat(
                sessionManager.getBearerToken(),
                item.id.toInt(),
                sessionManager.getUserID()
            )
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                        } catch (e: Exception) {
                            alertErrorDialog(e.message.toString())
                        }
                    }
                }
        }

    }


    private fun newMessageDialog() {
        val alertErrorDialog = Dialog(requireActivity())
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
        tvTitle.text =
            getString(R.string.connect_with_recruiter_by_exploring_the_jobs_and_sending_them_message_resume_directly_on_their_chat_click_yes_to_continue)

        btnYes.setOnClickListener {
            if (sessionManager.getUserType() == AppConstant.JOB_SEEKER){
                val bundle = Bundle()
                bundle.putString(AppConstant.PAGE_TITLE, "Search Job")
                bundle.putString(AppConstant.SOURCE_FRAGMENT, AppConstant.MESSAGE)
                findNavController().navigate(R.id.openSearchJobScreen,bundle)
            }else{

            }
            alertErrorDialog.dismiss()
        }

        btnNo.setOnClickListener {
            alertErrorDialog.dismiss()
        }

        alertErrorDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertErrorDialog.show()
    }


}