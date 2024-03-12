package com.hoxy.hlivv.ui.unity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ARViewModel : ViewModel() {
    val productList: MutableLiveData<List<ARCartItem>> = MutableLiveData()

    init {
        productList.value = mutableListOf()
    }

    fun setProductList(list: List<ARCartItem>): Unit {
        productList.value = list
    }

}