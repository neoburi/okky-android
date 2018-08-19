package kr.okky.app.android

import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import kr.okky.app.android.widget.ViewControl

abstract class BaseActivity : AppCompatActivity(), ViewControl {
    var mSpinner: ProgressBar? = null

    fun <T : View> getView(viewResourceId: Int): T {
        return findViewById(viewResourceId)
    }

    fun showSpinner(id: Int) {
        runOnUiThread { getView<View>(id).visibility = View.VISIBLE }
    }

    fun hideBgLogo(){
        getView<View>(R.id.ic_loading_bg).visibility = View.GONE
    }

    fun closeSpinner(id: Int) {
        runOnUiThread { getView<View>(id).visibility = View.GONE }
    }

    fun toast(id: Int){
        Toast.makeText(baseContext, id, Toast.LENGTH_LONG).show()
    }

    open fun requestAppPermission(){
        throw IllegalStateException("This method body should be implemented by sub class.[checkAppPermission()]")
    }

    open fun hasAppPermission():Boolean{
        throw IllegalStateException("This method body should be implemented by sub class.[hasAppPermission()]")
    }

    override fun findViews() {
        throw IllegalStateException("This method body should be implemented by sub class.[findViews()]")
    }


    override fun initViews(){
        throw IllegalStateException("This method body should be implemented by sub class.[initViews()]")
    }


    override fun attachEvents() {
        throw IllegalStateException("This method body should be implemented by sub class.[attachEvents()]")
    }

    override fun performClick(view: View) {
        throw IllegalStateException("This method body should be implemented by sub class.[performClick()]")
    }

    override fun setInitialData() {
        throw IllegalStateException("This method body should be implemented by sub class.[setInitialData()]")
    }

    override fun assignData() {
        throw IllegalStateException("This method body should be implemented by sub class.[assignData()]")
    }
}