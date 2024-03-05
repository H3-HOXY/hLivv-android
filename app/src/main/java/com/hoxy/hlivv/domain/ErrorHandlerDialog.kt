package com.hoxy.hlivv.domain

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.hoxy.hlivv.R

class ErrorHandlerDialog(context: Context, private val msg: String) {

    // lateinit var listener: OnDialogConfirmedListener
    private lateinit var btnYes: Button
    private lateinit var title: TextView

    private val dlg = Dialog(context)

    fun start() {
        /*타이틀바 제거*/
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        /*커스텀 다이얼로그 radius 적용*/
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setContentView(R.layout.error_dialog)


        title = dlg.findViewById(R.id.title_error)
        title.text = msg
        btnYes = dlg.findViewById(R.id.btn_yes)
        btnYes.setOnClickListener {
            //listener.onDialogConfirmed()
            dlg.dismiss()
        }

        dlg.show()
    }
}
