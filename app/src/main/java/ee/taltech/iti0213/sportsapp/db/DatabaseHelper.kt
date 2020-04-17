package ee.taltech.iti0213.sportsapp.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ee.taltech.iti0213.sportsapp.track.Track
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.Checkpoint
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.TrackLocation
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.WayPoint


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SportsApp"
        private const val DATABASE_VERSION = 1

        // ------------------------------------ Table names -----------------------------------
        private const val TABLE_TRACKS = "tracks"
        private const val TABLE_LOCATIONS = "locations"
        private const val TABLE_CHECKPOINTS = "checkpoints"
        private const val TABLE_WAY_POINTS = "way_points"

        // ---------------------------------- Common Columns names ---------------------------
        private const val KEY_ID = "id"
        private const val KEY_TRACK_ID = "track_id"

        // ------------------------------- Tracks table column names ----------------------------
        private const val KEY_TRACK_NAME = "name"
        private const val KEY_TRACK_TYPE = "type"
        private const val KEY_TRACK_START_STAMP = "start_stamp"
        private const val KEY_TRACK_END_STAMP = "end_stamp"

        private const val KEY_TRACK_DURATION_MOVING = "duration_moving"
        private const val KEY_TRACK_DISTANCE = "distance"
        private const val KEY_TRACK_DRIFT = "drift"
        private const val KEY_TRACK_ELEVATION_GAINED = "elevation_gained"
        private const val KEY_TRACK_MAX_SPEED = "max_speed"

        // ------------------------------ Locations table column names ---------------------------
        private const val KEY_LOCATION_NUMBER = "nr"
        private const val KEY_LOCATION_LATITUDE = "latitude"
        private const val KEY_LOCATION_LONGITUDE = "longitude"
        private const val KEY_LOCATION_ALTITUDE = "altitude"
        private const val KEY_LOCATION_ACCURACY = "accuracy"
        private const val KEY_LOCATION_ALTITUDE_ACCURACY = "altitude_accuracy"
        private const val KEY_LOCATION_TIME = "time"
        private const val KEY_LOCATION_TIME_ELAPSED = "time_elapsed"

        // ------------------------------- Checkpoints table column names ---------------------------
        private const val KEY_CHECKPOINT_NUMBER = "nr"
        private const val KEY_CHECKPOINT_LATITUDE = "latitude"
        private const val KEY_CHECKPOINT_LONGITUDE = "longitude"
        private const val KEY_CHECKPOINT_ALTITUDE = "altitude"
        private const val KEY_CHECKPOINT_ACCURACY = "accuracy"
        private const val KEY_CHECKPOINT_ALTITUDE_ACCURACY = "altitude_accuracy"
        private const val KEY_CHECKPOINT_TIMESTAMP = "timestamp"
        private const val KEY_CHECKPOINT_ELAPSED_TIMESTAMP = "elapsed_timestamp"
        private const val KEY_CHECKPOINT_DRIFT_FROM_LAST_CP = "drift_from_last_cp"
        private const val KEY_CHECKPOINT_TIME_SINCE_LAST_CP = "time_since_last_cp"
        private const val KEY_CHECKPOINT_DIST_FROM_LAST_CP = "distance_from_last_cp"

        // -------------------------------- Way points table column names ----------------------------
        private const val KEY_WAY_POINT_NUMBER = "nr"
        private const val KEY_WAY_POINT_LATITUDE = "latitude"
        private const val KEY_WAY_POINT_LONGITUDE = "longitude"
        private const val KEY_WAY_POINT_ADDED_TIMESTAMP = "added_timestamp"
        private const val KEY_WAY_POINT_REMOVED_TIMESTAMP = "removed_timestamp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTracksTable = ("CREATE TABLE " + TABLE_TRACKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TRACK_NAME + " TEXT NOT NULL,"
                + KEY_TRACK_TYPE + " INTEGER NOT NULL,"
                + KEY_TRACK_START_STAMP + " UNSIGNED BIGINT NOT NULL,"
                + KEY_TRACK_END_STAMP + " UNSIGNED BIGINT NOT NULL,"
                + KEY_TRACK_DURATION_MOVING + " UNSIGNED BIGINT NOT NULL,"
                + KEY_TRACK_DISTANCE + " REAL NOT NULL,"
                + KEY_TRACK_DRIFT + " REAL NOT NULL,"
                + KEY_TRACK_ELEVATION_GAINED + " REAL NOT NULL,"
                + KEY_TRACK_MAX_SPEED + " REAL NOT NULL" + ")")

        db?.execSQL(createTracksTable)

        val createLocationsTable = ("CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TRACK_ID + " INTEGER NOT NULL,"
                + KEY_LOCATION_NUMBER + " INTEGER NOT NULL,"
                + KEY_LOCATION_LATITUDE + " REAL NOT NULL,"
                + KEY_LOCATION_LONGITUDE + " REAL NOT NULL,"
                + KEY_LOCATION_ALTITUDE + " REAL NOT NULL,"
                + KEY_LOCATION_ACCURACY + " REAL NOT NULL,"
                + KEY_LOCATION_ALTITUDE_ACCURACY + " REAL NOT NULL,"
                + KEY_LOCATION_TIME + " UNSIGNED BIGINT NOT NULL,"
                + KEY_LOCATION_TIME_ELAPSED + " UNSIGNED BIGINT NOT NULL,"
                + " FOREIGN KEY (" + KEY_TRACK_ID
                + ") REFERENCES " + TABLE_TRACKS + "(" + KEY_ID + ") ON UPDATE CASCADE ON DELETE CASCADE" + ")")

        db?.execSQL(createLocationsTable)

        val createCheckpointsTable = ("CREATE TABLE " + TABLE_CHECKPOINTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TRACK_ID + " INTEGER NOT NULL,"
                + KEY_CHECKPOINT_NUMBER + " INTEGER NOT NULL,"
                + KEY_CHECKPOINT_LATITUDE + " REAL NOT NULL,"
                + KEY_CHECKPOINT_LONGITUDE + " REAL NOT NULL,"
                + KEY_CHECKPOINT_ALTITUDE + " REAL NOT NULL,"
                + KEY_CHECKPOINT_ACCURACY + " REAL NOT NULL,"
                + KEY_CHECKPOINT_ALTITUDE_ACCURACY + " REAL NOT NULL,"
                + KEY_CHECKPOINT_TIMESTAMP + " UNSIGNED BIGINT NOT NULL,"
                + KEY_CHECKPOINT_ELAPSED_TIMESTAMP + " UNSIGNED BIGINT NOT NULL,"
                + KEY_CHECKPOINT_DRIFT_FROM_LAST_CP + " REAL NOT NULL,"
                + KEY_CHECKPOINT_TIME_SINCE_LAST_CP + " UNSIGNED BIGINT NOT NULL,"
                + KEY_CHECKPOINT_DIST_FROM_LAST_CP + " REAL NOT NULL,"
                + " FOREIGN KEY (" + KEY_TRACK_ID
                + ") REFERENCES " + TABLE_TRACKS + "(" + KEY_ID + ") ON UPDATE CASCADE ON DELETE CASCADE" + ")")

        db?.execSQL(createCheckpointsTable)

        val createWayPointsTable = ("CREATE TABLE " + TABLE_WAY_POINTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TRACK_ID + " INTEGER NOT NULL,"
                + KEY_WAY_POINT_NUMBER + " INTEGER NOT NULL,"
                + KEY_WAY_POINT_LATITUDE + " REAL NOT NULL,"
                + KEY_WAY_POINT_LONGITUDE + " REAL NOT NULL,"
                + KEY_WAY_POINT_ADDED_TIMESTAMP + " UNSIGNED BIGINT NOT NULL,"
                + KEY_WAY_POINT_REMOVED_TIMESTAMP + " UNSIGNED BIGINT NULL,"
                + " FOREIGN KEY (" + KEY_TRACK_ID
                + ") REFERENCES " + TABLE_TRACKS + "(" + KEY_ID + ") ON UPDATE CASCADE ON DELETE CASCADE" + ")")

        db?.execSQL(createWayPointsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun saveTrack(track: Track): Long {
        val trackValues = ContentValues()
        trackValues.put(KEY_TRACK_NAME, track.name)
        trackValues.put(KEY_TRACK_TYPE, track.type.value)
        trackValues.put(KEY_TRACK_START_STAMP, track.startTime)
        trackValues.put(KEY_TRACK_END_STAMP, track.currentTime)
        trackValues.put(KEY_TRACK_DURATION_MOVING, track.movingTime)
        trackValues.put(KEY_TRACK_DISTANCE, track.runningDistance)
        trackValues.put(KEY_TRACK_DRIFT, track.getDrift())
        trackValues.put(KEY_TRACK_ELEVATION_GAINED, track.elevationGained)
        trackValues.put(KEY_TRACK_MAX_SPEED, track.maxSpeed)

        return this.writableDatabase.insert(TABLE_TRACKS, null, trackValues)
    }

    fun saveLocationToTrack(location: TrackLocation, locationNumber: Int, trackId: Long): Long {
        val locationValues = ContentValues()
        locationValues.put(KEY_TRACK_ID, trackId)
        locationValues.put(KEY_LOCATION_NUMBER, locationNumber)
        locationValues.put(KEY_LOCATION_LATITUDE, location.latitude)
        locationValues.put(KEY_LOCATION_LONGITUDE, location.longitude)
        locationValues.put(KEY_LOCATION_ALTITUDE, location.altitude)
        locationValues.put(KEY_LOCATION_ACCURACY, location.accuracy)
        locationValues.put(KEY_LOCATION_ALTITUDE_ACCURACY, location.altitudeAccuracy)
        locationValues.put(KEY_LOCATION_TIME, location.timestamp)
        locationValues.put(KEY_LOCATION_TIME_ELAPSED, location.elapsedTimestamp)

        return this.writableDatabase.insert(TABLE_LOCATIONS, null, locationValues)
    }

    fun saveCheckpointToTrack(cp: Checkpoint, cpNumber: Int, trackId: Long): Long {
        val cpValues = ContentValues()
        cpValues.put(KEY_TRACK_ID, trackId)
        cpValues.put(KEY_CHECKPOINT_NUMBER, cpNumber)
        cpValues.put(KEY_CHECKPOINT_LATITUDE, cp.latitude)
        cpValues.put(KEY_CHECKPOINT_LONGITUDE, cp.longitude)
        cpValues.put(KEY_CHECKPOINT_ALTITUDE, cp.altitude)
        cpValues.put(KEY_CHECKPOINT_ACCURACY, cp.accuracy)
        cpValues.put(KEY_CHECKPOINT_ALTITUDE_ACCURACY, cp.altitudeAccuracy)
        cpValues.put(KEY_CHECKPOINT_TIMESTAMP, cp.timestamp)
        cpValues.put(KEY_CHECKPOINT_ELAPSED_TIMESTAMP, cp.elapsedTimestamp)
        cpValues.put(KEY_CHECKPOINT_DRIFT_FROM_LAST_CP, cp.driftFromLastCP)
        cpValues.put(KEY_CHECKPOINT_TIME_SINCE_LAST_CP, cp.timeSinceLastCP)
        cpValues.put(KEY_CHECKPOINT_DIST_FROM_LAST_CP, cp.distanceFromLastCP)

        return this.writableDatabase.insert(TABLE_CHECKPOINTS, null, cpValues)
    }

    fun saveWayPointToTrack(wp: WayPoint, wpNumber: Int, trackId: Long): Long {
        val wpValues = ContentValues()
        wpValues.put(KEY_TRACK_ID, trackId)
        wpValues.put(KEY_WAY_POINT_NUMBER, wpNumber)
        wpValues.put(KEY_WAY_POINT_LATITUDE, wp.latitude)
        wpValues.put(KEY_WAY_POINT_LONGITUDE, wp.longitude)
        wpValues.put(KEY_WAY_POINT_ADDED_TIMESTAMP, wp.timeAdded)
        wpValues.put(KEY_WAY_POINT_REMOVED_TIMESTAMP, wp.timeRemoved)

        return this.writableDatabase.insert(TABLE_WAY_POINTS, null, wpValues)
    }

    fun readTracksSummary(startId: Long, endId: Long): List<TrackSummary> {
        val trackList = mutableListOf<TrackSummary>()

        val selectQuery = ("SELECT  * FROM " + TABLE_TRACKS
                + " WHERE " + KEY_ID + " BETWEEN " + startId.toString() + " AND " + endId.toString()
                + " ORDER BY " + KEY_ID + " DESC")

        val cursor = this.readableDatabase.rawQuery(selectQuery, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val trackSummary = TrackSummary(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getLong(3),
                        cursor.getLong(4),
                        cursor.getLong(5),
                        cursor.getDouble(6),
                        cursor.getDouble(7),
                        cursor.getDouble(8),
                        cursor.getDouble(9)
                    )
                    trackList.add(trackSummary)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return trackList
    }

    fun readTrackLocations(trackId: Long): List<TrackLocation> {
        val trackList = mutableListOf<TrackLocation>()

        val selectQuery = ("SELECT  * FROM " + TABLE_LOCATIONS
                + " WHERE " + KEY_TRACK_ID + " = " + trackId.toString()
                + " ORDER BY " + KEY_LOCATION_NUMBER + " ASC")

        val cursor = this.readableDatabase.rawQuery(selectQuery, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val trackLocation = TrackLocation(
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getDouble(5),
                        cursor.getFloat(6),
                        cursor.getFloat(7),
                        cursor.getLong(8),
                        cursor.getLong(9)
                    )
                    trackList.add(trackLocation)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return trackList
    }

    fun readTrackCheckpoints(trackId: Long): List<Checkpoint> {
        val trackList = mutableListOf<Checkpoint>()

        val selectQuery = ("SELECT  * FROM " + TABLE_CHECKPOINTS
                + " WHERE " + KEY_TRACK_ID + " = " + trackId.toString()
                + " ORDER BY " + KEY_CHECKPOINT_NUMBER + " ASC")

        val cursor = this.readableDatabase.rawQuery(selectQuery, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val trackLocation = Checkpoint(
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getDouble(5),
                        cursor.getDouble(6),
                        cursor.getDouble(7),
                        cursor.getLong(8),
                        cursor.getLong(9),
                        cursor.getFloat(10),
                        cursor.getLong(11),
                        cursor.getDouble(12)
                    )
                    trackList.add(trackLocation)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return trackList
    }

    fun readTrackWayPoints(trackId: Long): List<WayPoint> {
        val trackList = mutableListOf<WayPoint>()

        val selectQuery = ("SELECT  * FROM " + TABLE_WAY_POINTS
                + " WHERE " + KEY_TRACK_ID + " = " + trackId.toString()
                + " ORDER BY " + KEY_WAY_POINT_NUMBER + " ASC")

        val cursor = this.readableDatabase.rawQuery(selectQuery, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val trackLocation = WayPoint(
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getLong(5),
                        cursor.getLong(6)
                    )
                    trackList.add(trackLocation)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return trackList
    }
}