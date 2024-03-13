package com.hoxy.hlivv.ui.liveqr.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import com.hoxy.hlivv.R
import com.hoxy.hlivv.ui.liveqr.barcode.product.OnDialogConfirmedListener

/**
 * @author 반정현
 */
class CartCheckDialog(context: Context) {

    lateinit var listener: OnDialogConfirmedListener
    lateinit var btnMove: Button
    lateinit var btnCancel: Button

    private val dlg = Dialog(context)

    fun start() {
        /*타이틀바 제거*/
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        /*커스텀 다이얼로그 radius 적용*/
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setContentView(R.layout.cart_goto_dialog)

        btnMove = dlg.findViewById(R.id.btn_goto_cart)
        btnMove.setOnClickListener {
            listener.onDialogConfirmed()
            dlg.dismiss()
        }

        btnCancel = dlg.findViewById(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            listener.onDialogCancel()
            dlg.dismiss()
        }
        dlg.show()
    }
}