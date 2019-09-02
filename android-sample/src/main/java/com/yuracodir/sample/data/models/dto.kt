package com.yuracodir.sample.data.models

import android.os.Parcel
import android.os.Parcelable

class AlbumDto(
  val id: Int,
  val userId: Int,
  val title: String)

class PhotoDto(
  val albumId: Int,
  val id: Int,
  val title: String,
  val url: String,
  val thumbnailUrl: String) : Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readInt(),
    parcel.readInt(),
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "")

  override fun equals(other: Any?): Boolean {
    return other is PhotoDto && other.id == id
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(albumId)
    parcel.writeInt(id)
    parcel.writeString(title)
    parcel.writeString(url)
    parcel.writeString(thumbnailUrl)
  }

  override fun describeContents(): Int {
    return 0
  }

  override fun hashCode(): Int {
    var result = albumId
    result = 31 * result + id
    result = 31 * result + title.hashCode()
    result = 31 * result + url.hashCode()
    result = 31 * result + thumbnailUrl.hashCode()
    return result
  }

  companion object CREATOR : Parcelable.Creator<PhotoDto> {
    override fun createFromParcel(parcel: Parcel): PhotoDto {
      return PhotoDto(parcel)
    }

    override fun newArray(size: Int): Array<PhotoDto?> {
      return arrayOfNulls(size)
    }
  }
}
