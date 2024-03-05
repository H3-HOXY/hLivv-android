package com.hoxy.hlivv.ui.liveqr.barcode

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class BarcodeField(val label: String, val value: String) : Parcelable