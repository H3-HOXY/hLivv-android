package com.hoxy.hlivv.ui.unity

import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.hoxy.hlivv.databinding.ActivityUnityBinding
import com.hoxy.hlivv.ui.unity.unity.NativeUnityBridge
import com.hoxy.hlivv.ui.unity.unity.UnityFragment
import com.unity3d.player.IUnityPlayerLifecycleEvents
import com.unity3d.player.UnityPlayer


class ARActivity : AppCompatActivity(), UnityFragment.Companion.OnUnityFragmentReady,
    IUnityPlayerLifecycleEvents {
    private lateinit var _binding: ActivityUnityBinding
    private val binding get() = _binding

    private lateinit var viewModel: ARViewModel
    private lateinit var unityPlayer: UnityPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUnityBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFormat(PixelFormat.RGBX_8888)
        setContentView(binding.root)

        unityPlayer = UnityPlayer(this, this)

        supportFragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            add(binding.unity3DFragment.id, UnityFragment())
        }.commit()

        BottomSheetBehavior.from(binding.bottomSheet).apply {
            addBottomSheetCallback(bottomSheetCallback)
            state = STATE_EXPANDED
            peekHeight = 0
        }

        NativeUnityBridge.getInstance().onMenuExpend = {
            showBottomSheet()
        }


        viewModel = ViewModelProvider(this).get(ARViewModel::class.java)

        // 나중에 번들링으로 갖고오기
        viewModel.setProductList(
            listOf(
                ARCartItem(
                    "마젠드 - 서재용(블랙)",
                    "p1091100",
                    "https://static.hyundailivart.co.kr/UserFiles/data/image/detail/PRO145/PRO14509/PRO14509_00000000_detail1_ORIGIN.jpg/dims/resize/x610/optimize"
                ),
                ARCartItem(
                    "조인 1200 책상 (3색)",
                    "p1091102",
                    "https://static.hyundailivart.co.kr/upload_mall/goods/P200022982/GM40230182_img.jpg/dims/resize/x610/optimize"
                )
            )
        )

        viewModel.productList.observe(this) { data ->
        }

        val context = this.baseContext
        binding.recyclerArCartItem.apply {
            layoutManager = GridLayoutManager(this@ARActivity, 2)
            adapter = ARRecyclerAdapter(context, viewModel.productList.value!!) {
                UnityPlayer.UnitySendMessage(
                    "StorageSystem",
                    "LoadFromLocalStorage",
                    it.productCode.lowercase()
                )
                hideBottomSheet()
            }
            adapter!!.notifyDataSetChanged()
        }
//        binding.loadChair.setOnClickListener {
//            UnityPlayer.UnitySendMessage("StorageSystem", "LoadFromLocalStorage", "p1091100")
//            hideBottomSheet()
//        }
//
//        binding.loadDesk.setOnClickListener {
//            UnityPlayer.UnitySendMessage("StorageSystem", "LoadFromLocalStorage", "p1091102")
//            hideBottomSheet()
//        }

    }


    override fun onDestroy() {
        unityPlayer.quit()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        unityPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        unityPlayer.resume()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        unityPlayer.configurationChanged(newConfig)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        unityPlayer.windowFocusChanged(hasFocus)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.action == KeyEvent.ACTION_MULTIPLE) unityPlayer.injectEvent(
            event
        ) else super.dispatchKeyEvent(event)
    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return unityPlayer.injectEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return unityPlayer.injectEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return unityPlayer.injectEvent(event)
    }


    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        return unityPlayer.injectEvent(event)
    }

    /**
     * 유니티 관련
     */
    override fun onUnityPlayerUnloaded() {
    }

    override fun onUnityPlayerQuitted() {
    }

    override fun onUnityFragmentReady(): UnityPlayer = unityPlayer

    /**
     * BottomSheet 관련
     */
    private fun showBottomSheet() {
        BottomSheetBehavior.from(binding.bottomSheet).state = STATE_EXPANDED
    }

    private fun hideBottomSheet() {
        BottomSheetBehavior.from(binding.bottomSheet).state = STATE_COLLAPSED
    }


    companion object {

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            private var isInitial: Boolean = true
            private var oldOffset: Float = 0.0f

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (isInitial) {
                    oldOffset = slideOffset
                    isInitial = false
                }
                if (oldOffset - slideOffset > 0) BottomSheetBehavior.from(bottomSheet).state =
                    STATE_HIDDEN
                oldOffset = slideOffset
            }
        }

    }

}