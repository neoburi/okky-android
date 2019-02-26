package kr.okky.app.android.model

import android.os.Parcel
import android.os.Parcelable
import kr.okky.app.android.global.TAG
import kr.okky.app.android.utils.OkkyLog

class PushData: Parcelable{
    var title:String? = null
    var message:String? = null
    var url:String? = null
    constructor()
    constructor(parcel: Parcel){
        title = parcel.readString()
        message = parcel.readString()
        url = parcel.readString()
    }

    fun parseData(data:Map<String, String>):PushData{
        data?.let {
            for((key, value) in data){
                OkkyLog.err(TAG, "key=$key, value=$value")
            }
            title = data["title"]
            message = data["msg"]
            url = data["url"]
        }
        return this
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun string(): String {
        return "PushData(title=$title, message=$message, url=$url)"
    }

    companion object CREATOR : Parcelable.Creator<PushData> {

        override fun createFromParcel(parcel: Parcel): PushData {
            return PushData(parcel)
        }

        override fun newArray(size: Int): Array<PushData?> {
            return arrayOfNulls(size)
        }
    }


}