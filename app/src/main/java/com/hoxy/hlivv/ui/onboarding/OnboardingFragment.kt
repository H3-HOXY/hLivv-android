package com.hoxy.hlivv.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hoxy.hlivv.databinding.FragmentOnboardingBinding
import com.hoxy.hlivv.ui.component.BaseFragment


class OnboardingFragment : BaseFragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        val fragmentList = arrayListOf(
            OnboardingFirstFragment.newInstance(),
            OnboardingSecondFragment.newInstance(),
            OnboardingStartFragment.newInstance()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter
        binding.dotsIndicator.attachTo(binding.viewPager)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}