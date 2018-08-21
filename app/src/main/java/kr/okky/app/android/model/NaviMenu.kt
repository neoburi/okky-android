package kr.okky.app.android.model

class NaviMenu {
    var menuName: String? = null
    var menuPath: String? = null
    var isActive: Boolean = false
    var isEditable: Boolean = false
    var icon: String? = null
    var childMenu: List<NaviMenu>? = null

    fun hasIcon(): Boolean {
        return !icon!!.isEmpty()
    }

    fun hasChild(): Boolean {
        return childMenu != null && childMenu!!.isNotEmpty()
    }
}
