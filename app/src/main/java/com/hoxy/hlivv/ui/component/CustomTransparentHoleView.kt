//package com.hoxy.hlivv.ui.component
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Paint
//import android.graphics.PorterDuff
//import android.graphics.PorterDuffXfermode
//import android.graphics.Rect
//import android.util.AttributeSet
//import android.view.View
//
//
//class CustomTransparentHoleView : View {
//    private val paint = Paint()
//
//    constructor(context: Context?) : super(context) {
//        init()
//    }
//
//    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
//        init()
//    }
//
//    private fun init() {
//        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR))
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        canvas.drawRect(Rect(0, 0, width, height), paint)
//    }
//}
//
