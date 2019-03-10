package kr.okky.app.android.ui.frag

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.google.gson.Gson
import kr.okky.app.android.R
import kr.okky.app.android.global.StoreKey
import kr.okky.app.android.global.loadResourceId
import kr.okky.app.android.model.NaviMenu
import kr.okky.app.android.utils.OkkyUtils
import kr.okky.app.android.utils.Pref


class SettingsMenuFragment : BaseFragment(){
    private var mRecyclerView: RecyclerView? = null
    private var mList:MutableList<NaviMenu> = ArrayList()
    private var mDrawerMenuList:List<NaviMenu>? = null

    override fun followingWorksAfterInflateView() {
        findViews()
        setInitialData()
    }

    override fun findViews(){
        mRecyclerView = getView(R.id.recyclerView)
        mRecyclerView?.layoutManager =
                LinearLayoutManager(activity?.baseContext, LinearLayoutManager.VERTICAL, false)
    }

    override fun setInitialData() {
        mDrawerMenuList = OkkyUtils.createNavigationDrawerMenu()
        mDrawerMenuList?.forEach{
            mList.add(it)
            if(it.hasChild()) {
                mList.addAll(it.childMenu as ArrayList<NaviMenu>)
            }
        }
        mRecyclerView?.adapter = NavigationMenuAdapter(activity!!, mList)
    }

    override fun getViewResourceId(): Int = R.layout.fragment_setting_menu

    private fun setDrawerMenuShowFlag(position:Int, checked:Boolean){
        val item = mList[position]
        item.isActive = checked
        val jsonTxt= Gson().toJson(mDrawerMenuList)
        Pref.saveStringValue(StoreKey.DRAWER_MENU_JSON.name, jsonTxt)
        Pref.saveBooleanValue(StoreKey.DRAWER_MENU_CHANGED.name, true)
    }

    companion object {
        fun instance():BaseFragment = SettingsMenuFragment()
    }

    private inner class NavigationMenuAdapter
        constructor(private val context:Context, private val items:List<NaviMenu>):
            RecyclerView.Adapter<NavigationMenuAdapter.ListHeaderViewHolder>()
    {
        var inflater:LayoutInflater? = null
        init {
            inflater = LayoutInflater.from(context)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                NavigationMenuAdapter.ListHeaderViewHolder {
            val view = inflater?.inflate(R.layout.settings_menu_row, parent, false)
            return ListHeaderViewHolder(view!!)
        }

        override fun onBindViewHolder(holder: NavigationMenuAdapter.ListHeaderViewHolder, position: Int) {
            val item = items[position]
            holder.title?.text = item.menuName
            when(item.isEditable){
                true -> {
                    holder.switch?.visibility = View.VISIBLE
                    holder.switch?.isChecked = item.isActive
                    holder.switch?.isEnabled = true
                }
                else -> {
                    holder.switch?.visibility = View.INVISIBLE
                    holder.switch?.isChecked = false
                    holder.switch?.isEnabled = false
                }
            }

            when(item.type){
                0 -> {
                    holder.icon?.visibility = View.VISIBLE
                    holder.icon?.setImageResource(loadResourceId(context, "drawable", item.icon!!))
                    holder.itemView.setBackgroundResource(R.drawable.border_menu)
                }
                else -> {
                    holder.icon?.visibility = View.INVISIBLE
                    holder.itemView.setBackgroundDrawable(null)
                }
            }
        }

        override fun getItemViewType(position: Int): Int = items[position].type

        override fun getItemCount(): Int = items.size

        internal inner class ListHeaderViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView), View.OnClickListener {
            var icon: ImageView? = null
            var title: TextView? = null
            var switch: Switch? = null

            init {
                pickupView()
            }

            fun pickupView(){
                icon = itemView.findViewById(R.id.menu_icon)
                title = itemView.findViewById(R.id.menu_title)
                switch = itemView.findViewById(R.id.menu_switch)
                switch?.setOnClickListener(this)
            }

            override fun onClick(p0: View?) {
                val sw = p0 as Switch
                setDrawerMenuShowFlag(layoutPosition, sw.isChecked)
            }
        }
    }
}