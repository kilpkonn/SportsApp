package ee.taltech.iti0213.sportsapp.track.pracelable

import android.os.Parcel
import android.os.Parcelable

class DetailedTrackData(
    val duration: Long,
    val distance: Double,
    val elevationGained: Double,
    val averageElevation: Double,
    val drift: Double,
    val checkpointsCount: Int
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt()
    )

    companion object CREATOR : Parcelable.Creator<DetailedTrackData> {
        override fun createFromParcel(parcel: Parcel): DetailedTrackData {
            return DetailedTrackData(parcel)
        }

        override fun newArray(size: Int): Array<DetailedTrackData?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(duration)
        parcel.writeDouble(distance)
        parcel.writeDouble(elevationGained)
        parcel.writeDouble(averageElevation)
        parcel.writeDouble(drift)
        parcel.writeInt(checkpointsCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getSpeed(): Double {
        val speed = (distance / duration) * 1000_000_000 * 3.6
        return if (!speed.isNaN()) speed else 0.0
    }
}