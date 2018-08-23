package kr.okky.app.android.ui.frag

import kr.okky.app.android.R

class SettingsEnvFragment : BaseFragment(){

    override fun followingWorksAfterInflateView() {

    }

    override fun getViewResourceId(): Int = R.layout.fragment_setting_env

    companion object {
        fun instance():BaseFragment = SettingsEnvFragment()
    }
}