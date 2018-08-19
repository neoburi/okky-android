package kr.okky.app.android.widget


import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class OkkyWebView : WebView {
    var onScrollChangeListener: OnScrollChangeListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (onScrollChangeListener != null) {
            onScrollChangeListener!!.onScrollChange(this, l, t, oldl, oldt)
        }
    }

    interface OnScrollChangeListener {
        fun onScrollChange(v: WebView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int)
    }
}