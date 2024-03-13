package com.hoxy.hlivv.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hoxy.hlivv.R
import com.hoxy.hlivv.data.models.LoginDto
import com.hoxy.hlivv.data.repository.PreferencesRepository
import com.hoxy.hlivv.databinding.FragmentHomeBinding

/**
 * @author 반정현
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val jwt: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.loginStatus.observe(viewLifecycleOwner, Observer { status ->
            binding.loginStatus.text = status
        })

        val preferencesRepository: PreferencesRepository = PreferencesRepository(requireContext())
        // 로그인 상태 체크
        homeViewModel.checkLogin(preferencesRepository.getStringPref(R.string.pref_key_token, ""))

        binding.loginStatus.setOnClickListener {
            if (homeViewModel.loginStatus.value == "로그인") {
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            } else if (homeViewModel.loginStatus.value == "로그아웃") {
                preferencesRepository.saveStringPref(R.string.pref_key_token, "")
                val token = preferencesRepository.getStringPref(R.string.pref_key_token, "")
                preferencesRepository.saveLoginInfo(LoginDto("", ""))
                homeViewModel.checkLogin(
                    preferencesRepository.getStringPref(
                        R.string.pref_key_token,
                        ""
                    )
                )
                //TokenManager.removeToken(requireContext())
                //homeViewModel.checkLogin(TokenManager.getTokenFromSharedPreferences(requireContext()))
            }
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        if (navView.visibility == View.GONE) {
            navView.visibility = View.VISIBLE
        }
        binding.qrButton.setOnClickListener {
            navController.navigate(R.id.navigation_qr)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}