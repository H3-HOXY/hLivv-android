package com.hoxy.hlivv.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hoxy.hlivv.R


class OnboardingSecondFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_second, container, false)
    }

    companion object {
        fun newInstance(): OnboardingSecondFragment {
            return OnboardingSecondFragment()
        }
    }

}