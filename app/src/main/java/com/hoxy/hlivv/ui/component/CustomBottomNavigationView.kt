package com.hoxy.hlivv.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView


class CustomBottomNavigationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {

    init {
        val menuView = getChildAt(0) as ViewGroup
        //index 2 : 비활성화 아이템
        menuView.getChildAt(2).isClickable = false
    }
}