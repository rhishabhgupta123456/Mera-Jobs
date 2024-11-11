package tech.merajobs.fragment.findJobScreens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.adapter.CategoryAllAdapter
import tech.merajobs.adapter.CompanyAllAdapter
import tech.merajobs.dataModel.CategoryDataModel
import tech.merajobs.dataModel.CompanyDataModel
import tech.merajobs.databinding.FragmentCompanyBinding
import tech.merajobs.networkModel.homeViewModel.HomeViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager


class CompanyFragment : BaseFragment() {

    lateinit var binding : FragmentCompanyBinding
    lateinit var sessionManager: SessionManager
    lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyBinding.inflate(
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
                    findNavController().navigate(R.id.openHomeScreen)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.btBack.setOnClickListener {
            findNavController().navigate(R.id.openHomeScreen)
        }


        getCompany()

    }

    private fun getCompany() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            homeViewModel.getCompany(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val companyList = Gson().fromJson(jsonObjectData, CompanyDataModel::class.java)


                            binding.companyRecyclerView.adapter = CompanyAllAdapter(companyList.data ,requireActivity())
                            binding.companyRecyclerView.layoutManager = GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL, false)


                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }


    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as FindJobActivity
        activity.showHideBottomMenu(false)
    }


}