package ee.taltech.iti0213.sportsapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.media.MediaFormat


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

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
                + KEY_TRACK_NAME + " TEXT,"
                + KEY_TRACK_TYPE + " INTEGER,"
                + KEY_TRACK_START_STAMP + " UNSIGNED BIGINT,"
                + KEY_TRACK_END_STAMP + " UNSIGNED BIGINT"
                + KEY_TRACK_DURATION_MOVING + " KEY_TRACK_DURATION_MOVING,"
                + KEY_TRACK_DISTANCE + " REAL,"
                + KEY_TRACK_DRIFT + " REAL,"
                + KEY_TRACK_ELEVATION_GAINED + " REAL,"
                + KEY_TRACK_MAX_SPEED + " REAL" + ")")

        db?.execSQL(createTracksTable)

        val createLocationsTable = ("CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TRACK_ID + " INTEGER,"
                + KEY_LOCATION_NUMBER + " INTEGER,"
                + KEY_LOCATION_LATITUDE + " REAL,"
                + KEY_LOCATION_LONGITUDE + " REAL,"
                + KEY_LOCATION_ALTITUDE + " REAL,"
                + KEY_LOCATION_ACCURACY + " REAL,"
                + KEY_LOCATION_ALTITUDE_ACCURACY + " REAL,"
                + KEY_LOCATION_TIME + " UNSIGNED BIGINT,"
                + KEY_LOCATION_TIME_ELAPSED + " UNSIGNED BIGINT" + ")")

        db?.execSQL(createLocationsTable)

        val createCheckpointsTable = ("CREATE TABLE " + TABLE_CHECKPOINTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TRACK_ID + " INTEGER,"
                + KEY_CHECKPOINT_NUMBER + " INTEGER,"
                + KEY_CHECKPOINT_LATITUDE + " REAL,"
                + KEY_CHECKPOINT_LONGITUDE + " REAL,"
                + KEY_CHECKPOINT_ALTITUDE + " REAL,"
                + KEY_CHECKPOINT_ACCURACY + " REAL,"
                + KEY_CHECKPOINT_ALTITUDE_ACCURACY + " REAL"
                + KEY_CHECKPOINT_TIMESTAMP + " UNSIGNED BIGINT,"
                + KEY_CHECKPOINT_ELAPSED_TIMESTAMP + " UNSIGNED BIGINT,"
                + KEY_CHECKPOINT_DRIFT_FROM_LAST_CP + " REAL,"
                + KEY_CHECKPOINT_TIME_SINCE_LAST_CP + " UNSIGNED BIGINT,"
                + KEY_CHECKPOINT_DIST_FROM_LAST_CP + " REAL"+ ")")

        db?.execSQL(createCheckpointsTable)

        val createWayPointsTable = ("CREATE TABLE " + TABLE_WAY_POINTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TRACK_ID + " INTEGER,"
                + KEY_WAY_POINT_NUMBER + " INTEGER,"
                + KEY_WAY_POINT_LATITUDE + " REAL,"
                + KEY_WAY_POINT_LONGITUDE + " REAL,"
                + KEY_WAY_POINT_ADDED_TIMESTAMP + " UNSIGNED BIGINT,"
                + KEY_WAY_POINT_REMOVED_TIMESTAMP + " UNSIGNED BIGINT" + ")")

        db?.execSQL(createWayPointsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}