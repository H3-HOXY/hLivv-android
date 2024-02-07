package com.hoxy.hlivv.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.hoxy.hlivv.R
import com.hoxy.hlivv.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        binding.navView.setupWithNavController(navController)

        binding.homeButton.setOnClickListener {
            navController.navigate(R.id.navigation_home)
        }

        binding.menuButton.setOnClickListener {
            navController.navigate(R.id.navigation_menu)
        }

        binding.searchButton.setOnClickListener {
            navController.navigate(R.id.navigation_search)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.navView.menu.findItem(R.id.navigation_home).isChecked = true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}