package kr.okky.app.android.ui.frag

import android.widget.TextView
import kr.okky.app.android.R
import kr.okky.app.android.utils.OkkyUtils

class SettingsInfoFragment : BaseFragment(){

    override fun followingWorksAfterInflateView() {
        val version = getView<TextView>(R.id.txt_version)
        version.text =
                String.format(getString(R.string.txt_app_version), OkkyUtils.getVersionName(activity!!))
    }

    override fun getViewResourceId(): Int = R.layout.fragment_setting_info

    companion object {
        fun instance():BaseFragment = SettingsInfoFragment()
    }
}