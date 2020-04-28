package ee.taltech.iti0213.sportsapp.db.repository

import android.content.ContentValues
import android.content.Context
import ee.taltech.iti0213.sportsapp.db.DatabaseHelper
import ee.taltech.iti0213.sportsapp.db.domain.User

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

    override fun close() {
        databaseHelper.close()
    }
}