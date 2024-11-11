package tech.merajobs.fragment.findJobScreens.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.adapter.IndustryAdapter
import tech.merajobs.adapter.IndustryAllAdapter
import tech.merajobs.dataModel.IndustryDataModel
import tech.merajobs.databinding.FragmentCompanyBinding
import tech.merajobs.databinding.FragmentIndustryBinding
import tech.merajobs.networkModel.homeViewModel.HomeViewModel
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager


class IndustryFragment : BaseFragment() {


    lateinit var binding: FragmentIndustryBinding
    lateinit var sessionManager: SessionManager
    lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentIndustryBinding.inflate(
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


        if (!isNetworkAvailable()) {
            alertErrorDialog(getString(R.string.no_internet))
        } else {
            getIndustry()
        }
    }


    private fun getIndustry() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            homeViewModel.getIndustry(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val companyList =
                                Gson().fromJson(jsonObjectData, IndustryDataModel::class.java)


                            binding.industriesRecyclerView.adapter =
                                IndustryAllAdapter(companyList.data, requireActivity())
                            binding.industriesRecyclerView.layoutManager = GridLayoutManager(
                                requireContext(),
                                2,
                                GridLayoutManager.VERTICAL,
                                false
                            )

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }


}