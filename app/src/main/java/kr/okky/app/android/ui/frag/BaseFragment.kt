package kr.okky.app.android.ui.frag

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import kr.okky.app.android.ui.BaseActivity
import kr.okky.app.android.widget.ViewControl

abstract class BaseFragment : Fragment(), ViewControl {
    protected var mActivity: BaseActivity? = null
    protected var mRootView: View? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(getViewResourceId(), container, false)
        followingWorksAfterInflateView()
        return mRootView
    }

    open fun followingWorksAfterInflateView() {
        throw IllegalStateException("This method must be implemented by subclass.")
    }

    override fun findViews() =
        throw IllegalStateException("This method must be implemented by subclass.")

    override fun attachEvents() =
        throw IllegalStateException("This method must be implemented by subclass.")


    open fun getViewResourceId():Int{
        throw IllegalStateException("This method must be implemented by subclass.")
    }

    fun toast(id: Int) {
        mActivity!!.toast(id)
    }

    override fun performClick(view: View) =
        throw IllegalStateException("This method must be implemented by subclass.")


    override fun setInitialData() =
        throw IllegalStateException("This method must be implemented by subclass.")

    fun <T : View> getView(viewResourceId: Int): T {
        return mRootView!!.findViewById(viewResourceId)
    }

    fun getRootView():View = mRootView!!

    override fun initViews() =
        throw IllegalStateException("This method must be implemented by subclass.")

    override fun assignData() =
        throw IllegalStateException("This method must be implemented by subclass.")
}
