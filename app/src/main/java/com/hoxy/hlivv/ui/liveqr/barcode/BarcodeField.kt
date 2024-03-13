package com.hoxy.hlivv.ui.liveqr.barcode

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author 반정현
 */
@Parcelize
data class BarcodeField(val label: String, val value: String) : Parcelable