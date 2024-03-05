package com.hoxy.hlivv.ui.cart.payment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PaymentViewModel : ViewModel() {
    val numberOfArProduct = MutableLiveData<Int>().apply { value = 0 }
    val subTotalPrice = MutableLiveData<Long>().apply { value = 0L }
    val discountTotalPrice = MutableLiveData<Long>().apply { value = 0L }
    val orderTotalPrice = MutableLiveData<Long>().apply { value = 0L }
    val numberOfProductTypes = MutableLiveData<Int>().apply { value = 0 }

    fun setNumberOfArProduct(numberOfProduct: Int) {
        numberOfArProduct.value = numberOfProduct
    }

    fun setSubTotalPrice(subPrice: Long) {
        subTotalPrice.value = subPrice
    }

    fun setDiscountTotalPrice(discountPrice: Long) {
        discountTotalPrice.value = discountPrice
    }

    fun setOrderTotalPrice(orderPrice: Long) {
        orderTotalPrice.value = orderPrice
    }

    fun setNumberOfProductTypes(count: Int) {
        numberOfProductTypes.value = count
    }


    fun addSubTotalPrice(subPrice: Long) {
        subTotalPrice.value = (subTotalPrice.value ?: 0L) + subPrice
    }

    fun subtractSubTotalPrice(subPrice: Long) {
        subTotalPrice.value = (subTotalPrice.value ?: 0L) - subPrice
    }

    fun addDiscountTotalPrice(discountPrice: Long) {
        discountTotalPrice.value = (discountTotalPrice.value ?: 0L) + discountPrice
    }

    fun subtractDiscountTotalPrice(discountPrice: Long) {
        discountTotalPrice.value = (discountTotalPrice.value ?: 0L) - discountPrice
    }

    fun addOrderTotalPrice(orderPrice: Long) {
        orderTotalPrice.value = (orderTotalPrice.value ?: 0L) + orderPrice
    }

    fun subtractOrderTotalPrice(orderPrice: Long) {
        orderTotalPrice.value = (orderTotalPrice.value ?: 0L) - orderPrice
    }

}