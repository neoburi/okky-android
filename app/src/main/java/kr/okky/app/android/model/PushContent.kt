package kr.okky.app.android.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PushContent(
        @SerializedName("title")
        var title: String? = null,
        @SerializedName("message")
        var message: String? = null,
        @SerializedName("url")
        var url: String? = null,
        @SerializedName("image")
        var image: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeString(url)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "PushContent(title=$title, message=$message)"
    }

    fun hasImage(): Boolean {
        if ((image == null) or (image?.isEmpty()!!)) {
            return false
        }
        return true
    }

    companion object CREATOR : Parcelable.Creator<PushContent> {
        override fun createFromParcel(parcel: Parcel): PushContent {
            return PushContent(parcel)
        }

        override fun newArray(size: Int): Array<PushContent?> {
            return arrayOfNulls(size)
        }
    }

}