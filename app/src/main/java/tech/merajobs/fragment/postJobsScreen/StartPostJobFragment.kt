package tech.merajobs.fragment.postJobsScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import tech.merajobs.R
import tech.merajobs.databinding.FragmentMessageInboxBinding
import tech.merajobs.databinding.FragmentStartPostJobBinding
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.SessionManager


class StartPostJobFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentStartPostJobBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStartPostJobBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.btBack.setOnClickListener(this)
        binding.btDirectJobPosting.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btBack -> {
                requireActivity().finish()
            }

            R.id.btDirectJobPosting -> {
                findNavController().navigate(R.id.openDirectPostJobFragment)
            }
        }
    }

} 