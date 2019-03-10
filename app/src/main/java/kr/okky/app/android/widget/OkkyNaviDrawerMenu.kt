package kr.okky.app.android.widget

import android.support.design.widget.NavigationView
import android.view.MenuItem
import kr.okky.app.android.global.StoreKey
import kr.okky.app.android.global.getUrl
import kr.okky.app.android.global.loadResourceId
import kr.okky.app.android.model.NaviMenu
import kr.okky.app.android.ui.MainActivity
import kr.okky.app.android.utils.OkkyUtils
import kr.okky.app.android.utils.Pref
import java.util.*

class OkkyNaviDrawerMenu
constructor(private val activity: MainActivity,
            private val naviView: NavigationView) : NavigationView.OnNavigationItemSelectedListener {
    private var mDrawerMenuList = ArrayList<NaviMenu>()

    init {
        naviView.isVerticalScrollBarEnabled = false
        naviView.setNavigationItemSelectedListener(this)
    }

    fun loadDrawerMenu() {

        val menus = OkkyUtils.createNavigationDrawerMenu()
        val drawerMn = naviView.menu

        mDrawerMenuList.forEachIndexed { idx, it ->
            drawerMn?.removeItem(idx)
        }
        mDrawerMenuList.clear()

        var itemId = 0
        var order = 0
        menus.forEachIndexed { index, it ->
            if (it.isActive) {
                mDrawerMenuList.add(it)
                val menuItem: MenuItem? = drawerMn?.add(index, itemId++, order++, it.menuName)
                val iconResId = loadResourceId(activity, "drawable", it.icon!!)
                menuItem?.icon = activity.resources.getDrawable(iconResId)

                it.childMenu?.forEach {
                    if (it.isActive) {
                        mDrawerMenuList.add(it)
                        drawerMn?.add(index, itemId++, order++, it.menuName)
                    }
                }
            }
        }
        Pref.saveBooleanValue(StoreKey.DRAWER_MENU_CHANGED.name, false)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        activity.toggleDrawer()
        val menu = mDrawerMenuList[item.itemId]
        val path = menu.menuPath
        when {
            path?.startsWith("/articles")!! -> {
                activity.loadUrl(getUrl().plus(path))
            }
            path.startsWith("/users") -> {
                activity.loadUrl(getUrl().plus(path))
            }
            path.startsWith("http") -> {
                activity.loadUrl(path)
            }
            path.startsWith("settings://") -> {
                activity.openSettings()
            }
            path.startsWith("info@") -> {
                activity.launchEmailApp(path)
            }
        }
        return true
    }
}