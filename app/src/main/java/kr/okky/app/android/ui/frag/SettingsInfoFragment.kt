package kr.okky.app.android.ui.frag

import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import kr.okky.app.android.R
import kr.okky.app.android.utils.OkkyUtils

class SettingsInfoFragment : BaseFragment(){

    override fun followingWorksAfterInflateView() {
        getView<TextView>(R.id.txt_version).text =
                String.format(getString(R.string.txt_app_version), OkkyUtils.getVersionName(activity!!))
        getView<TextView>(R.id.txt_thanks_desc).movementMethod = ScrollingMovementMethod()
    }

    override fun getViewResourceId(): Int = R.layout.fragment_setting_info

    companion object {
        fun instance():BaseFragment = SettingsInfoFragment()
    }
}