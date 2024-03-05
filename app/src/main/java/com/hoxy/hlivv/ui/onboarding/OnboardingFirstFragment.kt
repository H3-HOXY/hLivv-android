package com.hoxy.hlivv.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hoxy.hlivv.databinding.FragmentOnboardingFirstBinding
import com.hoxy.hlivv.ui.component.BaseFragment


class OnboardingFirstFragment : BaseFragment() {
    private var _binding: FragmentOnboardingFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(): OnboardingFirstFragment {
            return OnboardingFirstFragment()
        }
    }

}