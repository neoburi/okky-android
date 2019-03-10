package kr.okky.app.android.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PushSetData(
        @SerializedName("topic")
        var topic:String?,
        @SerializedName("title")
        var title:String?,
        @SerializedName("description")
        var description:String?,
        @SerializedName("active")
        var active:Boolean?
) :Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(topic)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeValue(active)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "PushSetData(topic=$topic, title=$title, description=$description, active=$active)"
    }

    companion object CREATOR : Parcelable.Creator<PushSetData> {
        override fun createFromParcel(parcel: Parcel): PushSetData {
            return PushSetData(parcel)
        }

        override fun newArray(size: Int): Array<PushSetData?> {
            return arrayOfNulls(size)
        }
    }

}