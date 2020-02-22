package kr.okky.app.android.ui.frag

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import com.google.firebase.messaging.FirebaseMessaging
import kr.okky.app.android.R
import kr.okky.app.android.global.StoreKey
import kr.okky.app.android.model.PushSetData
import kr.okky.app.android.utils.OkkyUtils
import kr.okky.app.android.utils.Pref
import kr.okky.app.android.utils.SpanTextBuilder

class SettingsPushFragment : BaseFragment(){
    private var mRecyclerView: RecyclerView? = null
    private var mList:MutableList<PushSetData> =  ArrayList()

    override fun followingWorksAfterInflateView() {
        setHeaderTextStyles()
        findViews()
        setInitialData()
        attachEvents()
    }

    override fun findViews() {
        mRecyclerView = getView(R.id.recyclerView)
        mRecyclerView?.layoutManager =
                LinearLayoutManager(activity?.baseContext, LinearLayoutManager.VERTICAL, false)
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
        OkkyUtils.loadPushSettingData().let {
            mList.addAll(it)
        }
        mRecyclerView?.adapter = PushSettingAdapter(activity!!, mList)
    }

    private inner class PushSettingAdapter(
            private val context:Context,
            private val list:List<PushSetData>
    ) : RecyclerView.Adapter<PushSettingAdapter.ViewHolder>() {
        private var inflater:LayoutInflater? = null
        init{
            inflater = LayoutInflater.from(context)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater?.inflate(R.layout.settings_pushset_row, parent, false)
            return ViewHolder(view!!)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            val spanTextBuilder = createSpanText(item)
            holder.title?.text = spanTextBuilder.build()
            holder.switch?.isChecked = item.active!!
        }

        override fun getItemCount(): Int = list.size

        private fun createSpanText(item:PushSetData):SpanTextBuilder{
            return SpanTextBuilder().apply {
                append(item.title!!, RelativeSizeSpan(1.0f))
                append("\n".plus(item.description!!),
                        ForegroundColorSpan(resources.getColor(R.color.color_dddddd)),
                        RelativeSizeSpan(0.8f))
            }
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            var title:TextView? = null
            var switch:Switch? = null
            init {
                title = itemView.findViewById(R.id.menu_title)
                switch = itemView.findViewById(R.id.menu_switch)
                switch?.setOnClickListener(this)
            }
            override fun onClick(v: View?) {
                val sw = v as Switch
                registreTopic(sw, layoutPosition)
            }
        }

    }


    private fun setHeaderTextStyles(){
        val tv:TextView = getView(R.id.textView2)
        tv.text = SpanTextBuilder().apply {
            append(getString(R.string.settings_push_header_text_01), RelativeSizeSpan(1.0f))
            append(getString(R.string.settings_push_header_text_02),
                    ForegroundColorSpan(resources.getColor(R.color.color_dddddd)),
                    RelativeSizeSpan(0.8f))
        }.build()
    }

    override fun getViewResourceId(): Int {
        return R.layout.fragment_setting_push
    }

    private fun registreTopic(switch:Switch, position:Int){
        val settingData = mList[position]

        when(switch.isChecked){
            true -> {
                FirebaseMessaging.getInstance().subscribeToTopic(settingData.topic)
                        .addOnCompleteListener { task ->
                            when(task.isSuccessful){
                                true->{
                                    settingData.active = true
                                }
                                false ->{
                                    settingData.active = false
                                    switch.isChecked = false
                                    mRecyclerView?.adapter?.notifyDataSetChanged()
                                }
                            }
                            OkkyUtils.storePushSetDataJsonToPref(mList)
                        }
            }
            else ->{
                FirebaseMessaging.getInstance().unsubscribeFromTopic(settingData.topic)
                        .addOnCompleteListener { task ->
                            when(task.isSuccessful){
                                true->{
                                    settingData.active = false
                                }
                                false ->{
                                    settingData.active = true
                                    switch.isChecked = true
                                    mRecyclerView?.adapter?.notifyDataSetChanged()
                                }
                            }
                            OkkyUtils.storePushSetDataJsonToPref(mList)
                        }
            }
        }

        settingData.active = switch.isChecked

        when {
            switch.isChecked -> {

            }

        }
    }

    companion object {
        fun instance():BaseFragment = SettingsPushFragment()
    }
}