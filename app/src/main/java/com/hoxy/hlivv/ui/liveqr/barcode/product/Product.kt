package com.hoxy.hlivv.ui.liveqr.barcode.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


//data class Product internal constructor(val imageUrl: String, val title: String, val price: String)

@Parcelize
data class Product(
    val productId: Long,
    val imageUrl: String,
    val title: String,
    val unitPrice: String,
    val arSupported: Boolean,
    val qrSupported: Boolean,
    val eco: Boolean
) : Parcelable