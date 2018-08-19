package kr.okky.app.android.global

import kr.okky.app.android.R

enum class Menu
    constructor(private val iconResId:Int, private val unableIconResId:Int, private val type:String){
    HOME(R.drawable.click_menu_home, R.drawable.ic_action_home_disable, "B"),
    BACKWARD(R.drawable.click_menu_backward, R.drawable.ic_action_backward_disable, "B"),
    FORWARD(R.drawable.click_menu_forward, R.drawable.ic_action_forward_disable, "B"),
    RELOAD(R.drawable.click_menu_reload, R.drawable.ic_action_reload_disable, "B"),
    //FAVORITE(R.drawable.click_menu_favorite, R.drawable.ic_action_favorite_disable, "B"),
    SHARE(R.drawable.click_menu_share, R.drawable.ic_action_share_disable, "B"),
    GO_TOP(R.drawable.click_menu_gotop, R.drawable.ic_action_gotop_disable, "B"),
    MORE(R.drawable.click_menu_more, R.drawable.ic_action_more_disable, "B");

    fun resId(): Int = iconResId
    fun unableResId(): Int = unableIconResId
    fun type():String = type
    fun isBottomMenu():Boolean = "B" == type()
}