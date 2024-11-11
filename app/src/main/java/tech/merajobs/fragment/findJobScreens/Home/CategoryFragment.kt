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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tech.merajobs.R
import tech.merajobs.activity.EmployerActivity
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.adapter.CategoryAllAdapter
import tech.merajobs.adapter.JobAdapter
import tech.merajobs.dataModel.CategoryDataModel
import tech.merajobs.dataModel.JobDataModel
import tech.merajobs.dataModel.JobList
import tech.merajobs.databinding.FragmentCategoryBinding
import tech.merajobs.networkModel.homeViewModel.HomeViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager


class CategoryFragment : BaseFragment() {

    lateinit var binding : FragmentCategoryBinding
    lateinit var sessionManager: SessionManager
    lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(
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


        getCategory()

    }


    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as FindJobActivity
        activity.showHideBottomMenu(false)
    }


    private fun getCategory() {
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE

            homeViewModel.getCategory(sessionManager.getBearerToken())
                .observe(viewLifecycleOwner) { jsonObject ->
                    binding.tvProgressBar.visibility = View.GONE
                    val jsonObjectData = checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            val categoryData = Gson().fromJson(jsonObjectData, CategoryDataModel::class.java)

                            binding.categoryRecyclerView.adapter = CategoryAllAdapter(categoryData.data,requireActivity())
                            binding.categoryRecyclerView.layoutManager = GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL, false)

                        } catch (e: Exception) {
                            alertErrorDialog(e.toString())
                        }
                    }
                }

        }


    }



}