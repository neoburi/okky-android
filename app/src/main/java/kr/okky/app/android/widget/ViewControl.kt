package kr.okky.app.android.widget

import android.view.View

interface ViewControl {

    fun findViews()

    fun initViews()

    fun attachEvents()

    fun performClick(view: View)

    fun setInitialData()

    fun assignData()

    fun <T : View> getView(viewResourceId: Int): T
}