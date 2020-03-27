package ee.taltech.iti0213.sportsapp.track.pracelable.loaction

import android.os.Parcel
import android.os.Parcelable

class Checkpoint : Parcelable {
    val latitude: Double
    val longitude: Double
    val timestamp: Long
    val driftFromLastCP: Float
    val timeSinceLastCP: Long
    val distanceFromLastCP: Double

    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readLong(),
        parcel.readFloat(),
        parcel.readLong(),
        parcel.readDouble()
    ) {
    }

    constructor(location: TrackLocation, distanceFromLastCP: Double, lastCP: Checkpoint?) {
        this.distanceFromLastCP = distanceFromLastCP
        this.latitude = location.latitude
        this.longitude = location.longitude
        this.timestamp = location.timestamp
        if (lastCP != null) {
            driftFromLastCP = TrackLocation.calcDistanceBetween(
                latitude,
                longitude,
                lastCP.latitude,
                lastCP.longitude
            )
            timeSinceLastCP = lastCP.timestamp - location.timestamp
        } else {
            driftFromLastCP = 0f
            timeSinceLastCP = 0
        }
    }

    constructor(latitude: Double, longitude: Double, timestamp: Long, driftFromLastCP: Float, timeSinceLastCP: Long, distanceFromLastCP: Double) {
        this.latitude = latitude
        this.longitude = longitude
        this.timestamp = timestamp
        this.driftFromLastCP = driftFromLastCP
        this.timeSinceLastCP = timeSinceLastCP
        this.distanceFromLastCP = distanceFromLastCP
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeLong(timestamp)
        parcel.writeFloat(driftFromLastCP)
        parcel.writeLong(timeSinceLastCP)
        parcel.writeDouble(distanceFromLastCP)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Checkpoint> {
        override fun createFromParcel(parcel: Parcel): Checkpoint {
            return Checkpoint(parcel)
        }

        override fun newArray(size: Int): Array<Checkpoint?> {
            return arrayOfNulls(size)
        }

        fun fromLocation(location: TrackLocation, distance: Double, lastCP: Checkpoint?): Checkpoint {
            return Checkpoint(location, distance, lastCP)
        }
    }

}