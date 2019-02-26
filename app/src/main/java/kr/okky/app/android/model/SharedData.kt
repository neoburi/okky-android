package kr.okky.app.android.model

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
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

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SharedData> {
        override fun createFromParcel(parcel: Parcel): SharedData {
            return SharedData(parcel)
        }

        override fun newArray(size: Int): Array<SharedData?> {
            return arrayOfNulls(size)
        }
    }

    fun parseIntent(itt:Intent):SharedData{
        val bundle = itt.extras
        when(bundle != null) {
            true -> {
                subject = bundle.getString("android.intent.extra.SUBJECT")
                text = bundle.getString("android.intent.extra.TEXT")
            }
        }
        return this
    }

    fun encodedSubject():String {
      return when(subject == null){
          true -> ""
          false -> URLEncoder.encode(subject, "utf-8")
      }
    }

    fun encodedText():String =
            when(text == null){
                true -> ""
                false -> URLEncoder.encode(text, "utf-8")
            }

    fun hasContent():Boolean{
        return (text != null) && (text!!.isNotBlank())
    }

    fun clear(){
        subject = null
        text = null
    }
}