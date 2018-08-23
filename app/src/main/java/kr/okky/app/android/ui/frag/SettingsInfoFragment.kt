package kr.okky.app.android.ui.frag

import kr.okky.app.android.R

class SettingsInfoFragment : BaseFragment(){

    override fun followingWorksAfterInflateView() {

    }

    override fun getViewResourceId(): Int = R.layout.fragment_setting_info

    companion object {
        fun instance():BaseFragment = SettingsInfoFragment()
    }
}