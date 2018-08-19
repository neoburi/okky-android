package kr.okky.app.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import kr.okky.app.android.R
import kr.okky.app.android.global.BusProvider
import kr.okky.app.android.global.Menu

class BottomMenu : LinearLayout {
    //private val mBottomBtns = arrayOfNulls<ImageButton>(8)
    private val mBottomBtns = arrayOfNulls<ImageButton>(7)
    private val mWeight = 0.14286f
    constructor(context: Context) : super(context) {
        initMenu(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initMenu(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initMenu(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initMenu(context)
    }

    private fun initMenu(context: Context) {
        val inflater = LayoutInflater.from(context)
        Menu.values().forEach {
            if (it.isBottomMenu()) {
                val menuBox = inflater.inflate(R.layout.bottom_menu, null) as RelativeLayout
                val menuBoxParam = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, mWeight)
                menuBox.layoutParams = menuBoxParam

                val ib = menuBox.findViewById<ImageButton>(R.id.ic_menu)
                ib.setImageResource(it.resId())
                ib.setOnClickListener(BottomMenuClickListener(it))
                mBottomBtns[it.ordinal] = ib
                addView(menuBox)
            }
        }
    }

    private inner class BottomMenuClickListener internal constructor(private val mMenu: Menu) : View.OnClickListener {
        override fun onClick(view: View) {
            BusProvider.instance().post(mMenu)
        }
    }

    fun setAvailable(available: Boolean, position: Int) {
        val btn = mBottomBtns[position]
        btn?.isEnabled = available
        btn?.setImageResource(
                if (available)
                    Menu.values()[position].resId()
                else
                    Menu.values()[position].unableResId()
        )
        invalidate()
    }

    fun disableAll(){
        Menu.values().forEach {
            val ordinal = it.ordinal
            mBottomBtns[ordinal]?.isEnabled = false
            mBottomBtns[ordinal]?.setImageResource(it.unableResId())
        }
    }

    fun enableAll(){
        Menu.values().forEach {
            val ordinal = it.ordinal
            mBottomBtns[ordinal]?.isEnabled = true
            mBottomBtns[ordinal]?.setImageResource(it.resId())
        }
    }
}
