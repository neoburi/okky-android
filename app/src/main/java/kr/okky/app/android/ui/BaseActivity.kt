package kr.okky.app.android.ui

import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.Toast
import kr.okky.app.android.R
import kr.okky.app.android.widget.ViewControl

abstract class BaseActivity : AppCompatActivity(), ViewControl {
    var mSpinner: ProgressBar? = null

    override fun <T : View> getView(viewResourceId: Int): T = findViewById(viewResourceId)

    fun showSpinner(id: Int) {
        runOnUiThread { getView<View>(id).visibility = View.VISIBLE }
    }

    fun hideBgLogo(){
        val lg = getView<View>(R.id.ic_loading_bg)
        if(lg.visibility == View.VISIBLE) {
            Handler().postDelayed({
                val ani = AnimationUtils.loadAnimation(baseContext, android.R.anim.fade_out)
                ani.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        lg.visibility = View.GONE
                    }
                    override fun onAnimationRepeat(animation: Animation) {}
                })
                lg.startAnimation(ani)
            }, 1000)
        }
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