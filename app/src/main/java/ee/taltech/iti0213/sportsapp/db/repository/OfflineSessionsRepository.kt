package ee.taltech.iti0213.sportsapp.db.repository

import android.content.ContentValues
import android.content.Context
import ee.taltech.iti0213.sportsapp.db.DatabaseHelper
import ee.taltech.iti0213.sportsapp.db.domain.OfflineSession

class OfflineSessionsRepository private constructor(context: Context) : IRepository {

    companion object {
        fun open(context: Context): OfflineSessionsRepository {
            return OfflineSessionsRepository(context)
        }
    }

    private val databaseHelper: DatabaseHelper = DatabaseHelper.getInstance(context)

    fun saveOfflineSession(trackId: Long) {
        databaseHelper.writableDatabase.beginTransaction()
        val offlineSessionsValues = ContentValues()
        offlineSessionsValues.put(DatabaseHelper.KEY_TRACK_ID, trackId)
        databaseHelper.writableDatabase.insert(DatabaseHelper.TABLE_OFFLINE_SESSIONS, null, offlineSessionsValues)
        databaseHelper.writableDatabase.setTransactionSuccessful()
        databaseHelper.writableDatabase.endTransaction()
    }

    fun readOfflineSessions(): List<OfflineSession> {
        val offlineSessionsList = mutableListOf<OfflineSession>()

        val selectQuery = ("SELECT  * FROM " + DatabaseHelper.TABLE_OFFLINE_SESSIONS)

        val cursor = databaseHelper.readableDatabase.rawQuery(selectQuery, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val trackLocation = OfflineSession(
                        cursor.getLong(0),
                        cursor.getLong(1)
                    )
                    offlineSessionsList.add(trackLocation)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return offlineSessionsList
    }

    override fun close() {
        databaseHelper.close()
    }
}