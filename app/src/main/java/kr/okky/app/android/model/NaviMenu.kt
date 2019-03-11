package kr.okky.app.android.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NaviMenu {
    @SerializedName("menuName")
    var menuName: String? = null
    @SerializedName("menuPath")
    var menuPath: String? = null
    @SerializedName("active")
    var isActive: Boolean = false
    @SerializedName("editable")
    var isEditable: Boolean = false
    @SerializedName("icon")
    var icon: String? = null
    @SerializedName("type")
    var type: Int = -1
    @SerializedName("childMenu")
    var childMenu: List<NaviMenu>? = null
    @Expose(serialize = false, deserialize = false)
    var checkedFlag:Boolean = false

    fun isParentRow():Boolean = type == 0

    fun hasIcon(): Boolean {
        return !icon!!.isEmpty()
    }

    fun hasChild(): Boolean {
        return childMenu != null && childMenu!!.isNotEmpty()
    }
}
