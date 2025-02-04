package com.example.internintelligence_facedetection

import android.os.Parcel
import android.os.Parcelable

data class ParcelableImageData(val base64Image: String?) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(base64Image)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ParcelableImageData> {
        override fun createFromParcel(parcel: Parcel): ParcelableImageData {
            return ParcelableImageData(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableImageData?> {
            return arrayOfNulls(size)
        }
    }
}
