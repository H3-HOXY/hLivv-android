package com.hoxy.hlivv.ui.unity.unity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.hoxy.hlivv.databinding.FragmentUnityBinding
import com.unity3d.player.UnityPlayer

class UnityFragment : Fragment() {

    private lateinit var _binding: FragmentUnityBinding
    private val binding get() = _binding

    private lateinit var unityPlayer: UnityPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.addView(unityPlayer)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnUnityFragmentReady) {
            unityPlayer = context.onUnityFragmentReady()
            unityPlayer.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    companion object {
        fun interface OnUnityFragmentReady {
            fun onUnityFragmentReady(): UnityPlayer
        }
    }
}