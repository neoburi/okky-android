package kr.okky.app.android.ui.frag

import kr.okky.app.android.R

class SettingsMenuFragment : BaseFragment(){

    override fun followingWorksAfterInflateView() {

    }

    override fun getViewResourceId(): Int = R.layout.fragment_setting_menu

    companion object {
        fun instance():BaseFragment = SettingsMenuFragment()
    }
}