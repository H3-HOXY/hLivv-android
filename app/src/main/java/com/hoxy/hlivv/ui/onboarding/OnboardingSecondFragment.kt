package com.hoxy.hlivv.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hoxy.hlivv.databinding.FragmentOnboardingSecondBinding
import com.hoxy.hlivv.ui.component.BaseFragment

/**
 * @author 반정현
 */
class OnboardingSecondFragment : BaseFragment() {
    private var _binding: FragmentOnboardingSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(): OnboardingFirstFragment {
            return OnboardingFirstFragment()
        }
    }
}
