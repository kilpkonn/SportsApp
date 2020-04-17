package ee.taltech.iti0213.sportsapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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

        private const val KEY_TRACK_DURATION = "duration"
        private const val KEY_TRACK_DURATION_MOVING = "duration_moving"

        private const val KEY_TRACK_DISTANCE = "distance"
        private const val KEY_TRACK_DRIFT = "drift"
        private const val KEY_ELEVATION_GAINED = "elevation_gained"
        private const val KEY_MAX_SPEED = "max_speed"

        // ------------------------------ Locations table column names ---------------------------
        private const val KEY_LOCATION_NUMBER = "nr"
        private const val KEY_LOCATION_LATITUDE = "latitude"
        private const val KEY_LOCATION_LONGITUDE = "longitude"
        private const val KEY_LOCATION_ACCURACY = "accuracy"
        private const val KEY_LOCATION_ALTITUDE = "altitude"
        private const val KEY_LOCATION_ALTITUDE_ACCURACY = "altitude_accuracy"
        private const val KEY_LOCATION_TIME = "time"
        private const val KEY_LOCATION_TIME_ELAPSED = "time_elapsed"

        // ------------------------------- Checkpoints table column names ---------------------------
        private const val KEY_CHECKPOINT_NUMBER = "nr"
        private const val KEY_CHECKPOINT_LATITUDE = "latitude"
        private const val KEY_CHECKPOINT_LONGITUDE = "longitude"
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
        TODO("Not yet implemented")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}