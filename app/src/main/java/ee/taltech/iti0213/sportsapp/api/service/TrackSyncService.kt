package ee.taltech.iti0213.sportsapp.api.service

import android.content.Context
import ee.taltech.iti0213.sportsapp.api.domain.RegisterDto

class TrackSyncService private constructor(context: Context) {

    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
        private var instance: TrackSyncService? = null

        @Synchronized
        fun getInstance(context: Context): TrackSyncService {
            if (instance == null) {
                instance = TrackSyncService(context)
            }
            return instance!!
        }
    }

    fun createAccount(registerDto: RegisterDto) {

    }
}