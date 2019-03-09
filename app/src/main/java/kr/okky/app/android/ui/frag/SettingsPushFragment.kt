package kr.okky.app.android.ui.frag

import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.widget.Switch
import android.widget.TextView
import kr.okky.app.android.R
import kr.okky.app.android.global.StoreKey
import kr.okky.app.android.utils.Pref
import kr.okky.app.android.utils.SpanTextBuilder

class SettingsPushFragment : BaseFragment(){


    override fun followingWorksAfterInflateView() {
        setHeaderTextStyles()
        setInitialData()
        attachEvents()
    }

    override fun attachEvents() {
        val sw:Switch = getView(R.id.menu_switch)
        sw.setOnCheckedChangeListener { button, checked ->
            Pref.saveBooleanValue(StoreKey.PUSH_ACCEPT.name, checked)
        }
    }

    override fun setInitialData() {
        val sw:Switch = getView(R.id.menu_switch)
        sw.isChecked = Pref.getBooleanValue(StoreKey.PUSH_ACCEPT.name, false)
    }

    private fun setHeaderTextStyles(){
        val textBuilder = SpanTextBuilder()
        textBuilder.append(getString(R.string.settings_push_header_text_01), RelativeSizeSpan(1.0f))
        textBuilder.append(getString(R.string.settings_push_header_text_02),
                ForegroundColorSpan(resources.getColor(R.color.color_dddddd)),
                RelativeSizeSpan(0.8f))
        val tv:TextView = getView(R.id.textView2)
        tv.text = textBuilder.build()
    }

    override fun getViewResourceId(): Int {
        return R.layout.fragment_setting_push
    }

    companion object {
        fun instance():BaseFragment = SettingsPushFragment()
    }
}