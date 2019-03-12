package kr.okky.app.android.ui.frag

import android.content.Context
import android.os.Bundle
import android.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kr.okky.app.android.R
import kr.okky.app.android.global.Acceptor
import kr.okky.app.android.global.loadResourceId
import kr.okky.app.android.model.NaviMenu
import kr.okky.app.android.utils.OkkyUtils


class ShareTargetSelectionFragment:DialogFragment() {
    private var mRecyclerView: RecyclerView? = null
    private var mTargetList:ArrayList<NaviMenu> = ArrayList()
    private var mInflater:LayoutInflater? = null
    private var mAcceptor:Acceptor? = null
    private var mCheckedPosition:Int = -1
    private var mCurrentItem:NaviMenu? = null
    companion object {
        fun newInstance(context:Context, acceptor:Acceptor):DialogFragment{
            val frag = ShareTargetSelectionFragment()
            frag.mInflater = LayoutInflater.from(context)
            frag.mAcceptor = acceptor
            return frag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater.inflate(R.layout.dialog_share_target, container, false)
        mRecyclerView = contentView.findViewById(R.id.recyclerView)
        mRecyclerView?.layoutManager =
                LinearLayoutManager(activity?.baseContext, LinearLayoutManager.VERTICAL, false)
        loadDataAndDisplay()
        attachEvents(contentView)
        return contentView
    }

    private fun attachEvents(v:View){
        v.findViewById<Button>(R.id.confirm).apply {
            setOnClickListener{
                if(mCurrentItem != null){
                    mAcceptor?.let {
                        acp -> acp.accept(mCurrentItem?.menuPath as Any)
                        dismiss()
                    }
                }else{
                    Toast.makeText(activity, R.string.title_share_target_selection, Toast.LENGTH_SHORT).show()
                }
            }
        }
        v.findViewById<Button>(R.id.cancel).apply {
            setOnClickListener {
                mAcceptor?.let {
                    acp -> acp.deny()
                    dismiss()
                }
            }
        }
    }

    private fun loadDataAndDisplay(){
        val listItems = ArrayList<NaviMenu>()
        val list = OkkyUtils.loadShareTargetSections()
        list.forEach{
            listItems.add(it)
            if(it.hasChild()) {
                listItems.addAll(it.childMenu?.filter {
                    child ->
                    !OkkyUtils.shareTargetExceptions.contains(child.menuPath)
                } as ArrayList)
            }
        }

        mRecyclerView?.adapter = ShareTargetAdapter(activity!!, listItems)
    }

    private inner class ShareTargetAdapter(private val mContext:Context, private val mItems:List<NaviMenu>):
            RecyclerView.Adapter<ShareTargetAdapter.RowHolder>(){
        var mInflater:LayoutInflater? = null

        init{
            mInflater = LayoutInflater.from(mContext)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareTargetAdapter.RowHolder {
            val v = mInflater?.inflate(R.layout.dialog_share_target_row, parent, false)
            return RowHolder(v!!)
        }

        override fun getItemCount(): Int = mItems.size

        override fun onBindViewHolder(holder: ShareTargetAdapter.RowHolder, position: Int) {
            val item = mItems[position]
            holder.title?.text = item.menuName

            if(item.isEditable || item.menuPath == "/articles/columns") {
                holder.radio?.visibility = View.VISIBLE
            }else{
                holder.radio?.visibility = View.INVISIBLE
            }
            holder.radio?.isChecked = item.checkedFlag

            when(item.type){
                0 -> {
                    holder.icon?.visibility = View.VISIBLE
                    holder.icon?.setImageResource(loadResourceId(mContext, "drawable", item.icon!!))
                }
                else -> {
                    holder.icon?.visibility = View.INVISIBLE
                    holder.itemView.setBackgroundDrawable(null)
                }
            }
        }

        internal inner class RowHolder(holderView:View) : RecyclerView.ViewHolder(holderView), View.OnClickListener{
            var title:TextView? = null
            var icon:ImageView? = null
            var radio:RadioButton? = null
            init{
                icon = holderView.findViewById(R.id.menu_icon)
                title = holderView.findViewById(R.id.menu_title)
                radio = holderView.findViewById(R.id.radiobutton)
                holderView.setOnClickListener(this)
                radio?.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                mCurrentItem?.checkedFlag = false
                val clickItem = mItems[layoutPosition]
                mCurrentItem = clickItem
                mCheckedPosition = layoutPosition

                if( v is AppCompatButton){
                    with(v as RadioButton){
                        clickItem.checkedFlag = this.isChecked
                    }
                }else {
                    clickItem.checkedFlag = !clickItem.checkedFlag
                }
                mRecyclerView?.adapter?.notifyDataSetChanged()
            }
        }
    }
}