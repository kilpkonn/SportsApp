package ee.taltech.iti0213.sportsapp.track.pracelable.loaction

import android.location.Location
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import java.sql.Timestamp

class TrackLocation(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val elapsedTimestamp: Long,
    val accuracy: Float,
    val altitude: Double,
    val altitudeAccuracy: Float
) : Parcelable {

    companion object CREATOR : Parcelable.Creator<TrackLocation> {
        override fun createFromParcel(parcel: Parcel): TrackLocation {
            return TrackLocation(parcel)
        }

        override fun newArray(size: Int): Array<TrackLocation?> {
            return arrayOfNulls(size)
        }

        fun fromLocation(location: Location): TrackLocation {
            val verticalAccuracy =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) location.verticalAccuracyMeters else 0f

            return TrackLocation(
                location.latitude,
                location.longitude,
                location.time,
                location.elapsedRealtimeNanos,
                location.accuracy,
                location.altitude,
                verticalAccuracy
            )
        }

        fun calcDistanceBetween(from: TrackLocation, to: TrackLocation): Float {
            return calcDistanceBetween(from.latitude, from.longitude, to.latitude, to.longitude)
        }

        fun calcDistanceBetween(
            lat: Double,
            lng: Double,
            endLat: Double,
            endLng: Double
        ): Float {
            // distance, initial bearing, end bearing
            val distance = floatArrayOf(0f, 0f, 0f)
            Location.distanceBetween(lat, lng, endLat, endLng, distance)
            return distance[0]
        }

        fun calcBearingBetween(
                lat: Double,
                lng: Double,
                endLat: Double,
                endLng: Double
        ): Float {
            // distance, initial bearing, end bearing
            val distance = floatArrayOf(0f, 0f, 0f)
            Location.distanceBetween(lat, lng, endLat, endLng, distance)
            return (distance[1] + distance[2]) / 2f // Average
        }
    }

    var currentCP: WayPoint? = null

    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readFloat(),
        parcel.readDouble(),
        parcel.readFloat()
    ) {
        currentCP = parcel.readParcelable(WayPoint::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeLong(timestamp)
        parcel.writeLong(elapsedTimestamp)
        parcel.writeFloat(accuracy)
        parcel.writeDouble(altitude)
        parcel.writeFloat(altitudeAccuracy)
        parcel.writeParcelable(currentCP, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

}