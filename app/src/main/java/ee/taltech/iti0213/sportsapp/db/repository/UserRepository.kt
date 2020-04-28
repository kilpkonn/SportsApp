package ee.taltech.iti0213.sportsapp.db.repository

import android.content.ContentValues
import android.content.Context
import ee.taltech.iti0213.sportsapp.db.DatabaseHelper
import ee.taltech.iti0213.sportsapp.db.domain.User
import ee.taltech.iti0213.sportsapp.track.TrackType

class UserRepository(context: Context): IRepository {

    companion object {
        fun open(context: Context): UserRepository {
            return UserRepository(context)
        }
    }

    private val databaseHelper: DatabaseHelper = DatabaseHelper.getInstance(context)

    fun saveUser(user: User): Long {
        val trackValues = ContentValues()
        trackValues.put(DatabaseHelper.KEY_USER_USERNAME, user.username)
        trackValues.put(DatabaseHelper.KEY_USER_EMAIL, user.email)
        trackValues.put(DatabaseHelper.KEY_USER_PASSWORD, user.password)
        trackValues.put(DatabaseHelper.KEY_USER_FIRST_NAME, user.firstName)
        trackValues.put(DatabaseHelper.KEY_USER_LAST_NAME, user.lastName)
        trackValues.put(DatabaseHelper.KEY_USER_SPEED_MODE, user.speedMode)
        trackValues.put(DatabaseHelper.KEY_USER_DEFAULT_ACTIVITY, user.defaultActivityType.value)
        trackValues.put(DatabaseHelper.KEY_USER_AUTO_SYNC, user.autoSync)

        databaseHelper.writableDatabase.beginTransaction()
        val id = databaseHelper.writableDatabase.insert(DatabaseHelper.TABLE_USERS, null, trackValues)
        databaseHelper.writableDatabase.setTransactionSuccessful()
        databaseHelper.writableDatabase.endTransaction()
        return id
    }

    fun readUser(id: Long): User {

        val selectQuery = ("SELECT  * FROM " + DatabaseHelper.TABLE_USERS
                + " WHERE " + DatabaseHelper.KEY_ID + " = " + id.toString()
                + " LIMIT 1 ")

        val cursor = databaseHelper.readableDatabase.rawQuery(selectQuery, null)
        var user: User? = null

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                user = User(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6) == 1,
                    TrackType.fromInt(cursor.getInt(7))!!,
                    cursor.getInt(8) == 1
                )
            }
            cursor.close()
        }
        return user!!
    }

    override fun close() {
        databaseHelper.close()
    }
}