package com.hoxy.hlivv.ui.liveqr.barcode.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * @author 반정현
 */
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