package kr.okky.app.android.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import kr.okky.app.android.global.FcmDataKey
import kr.okky.app.android.global.PushType
import kr.okky.app.android.global.StartingPoint

class PushData() : Parcelable {
    var pushType: String? = PushType.CONTENT.type
    var content: PushContent? = null
    var advertise: PushContent? = null

    var startingPoint: StartingPoint = StartingPoint.NOTIFICATION

    constructor(parcel: Parcel) : this() {
        pushType = parcel.readString()
        content = parcel.readParcelable(PushContent::class.java.classLoader)
        advertise = parcel.readParcelable(PushContent::class.java.classLoader)
    }

    fun parseData(data: Map<String, String>?): PushData {
        data?.let {
            pushType = it[FcmDataKey.PUSH_TYPE.key]
            pushType?.let { self ->
                val gson = Gson()
                if (PushType.CONTENT.type == self) {
                    content = gson.fromJson(it[PushType.CONTENT.key], PushContent::class.java)
                } else {
                    advertise = gson.fromJson(it[PushType.ADVERTISE.key], PushContent::class.java)
                }
            }
        }
        return this
    }

    fun fromBroadcast(): Boolean = StartingPoint.BROADCAST == startingPoint

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pushType)
        parcel.writeParcelable(content, flags)
        parcel.writeParcelable(advertise, flags)
    }

    override fun describeContents(): Int {
        return 0
    }


    fun isAd(): Boolean = PushType.ADVERTISE.type == pushType

    override fun toString(): String {
        return "PushData(pushType=$pushType, content=$content, advertise=$advertise, startingPoint=$startingPoint)"
    }

    companion object CREATOR : Parcelable.Creator<PushData> {
        override fun createFromParcel(parcel: Parcel): PushData {
            return PushData(parcel)
        }

        override fun newArray(size: Int): Array<PushData?> {
            return arrayOfNulls(size)
        }

        val TAG: String = PushData::class.java.simpleName
    }
}