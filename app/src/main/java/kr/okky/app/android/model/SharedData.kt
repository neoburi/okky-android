package kr.okky.app.android.model

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import kr.okky.app.android.utils.OkkyLog
import java.net.URLEncoder

data class SharedData(
    var subject:String? = null,
    var text:String? = null
) :Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(subject)
        parcel.writeString(text)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SharedData> {
        override fun createFromParcel(parcel: Parcel): SharedData = SharedData(parcel)

        override fun newArray(size: Int): Array<SharedData?> = arrayOfNulls(size)
    }

    fun parseIntent(itt:Intent):SharedData{
        itt.extras?.let {
            subject = it.getString("android.intent.extra.SUBJECT")
            text = it.getString("android.intent.extra.TEXT")
            /*bundle.keySet().forEach { k->
                OkkyLog.log("key=$k, value=${bundle[k]}")
            }*/
        }

        return this
    }

    fun encodedSubject():String {
      return when(subject.isNullOrBlank()){
          true -> ""
          false -> URLEncoder.encode(subject, "utf-8")
      }
    }

    fun encodedText():String =
            when(text.isNullOrBlank()){
                true -> ""
                false -> URLEncoder.encode(text, "utf-8")
            }

    fun hasContent():Boolean = (text != null) && (text!!.isNotBlank())

    fun clear(){
        subject = null
        text = null
    }
}