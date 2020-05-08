package ee.taltech.iti0213.sportsapp.api.controller

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.fasterxml.jackson.databind.ObjectMapper
import ee.taltech.iti0213.sportsapp.api.WebApiHandler
import ee.taltech.iti0213.sportsapp.api.dto.GpsLocationDto
import ee.taltech.iti0213.sportsapp.api.dto.GpsSessionDto
import org.json.JSONObject
import java.nio.charset.Charset


class TrackSyncController private constructor(val context: Context) {

    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName

        private var instance: TrackSyncController? = null
        private val mapper = ObjectMapper()

        @Synchronized
        fun getInstance(context: Context): TrackSyncController {
            if (instance == null) {
                instance = TrackSyncController(context)
            }
            return instance!!
        }
    }

    fun createNewSession(gpsSessionDto: GpsSessionDto, onSuccess: (r: GpsSessionDto) -> Unit, onError: () -> Unit) {
        val handler = WebApiHandler.getInstance(context)
        Log.d(TAG, mapper.writeValueAsString(gpsSessionDto))

        handler.makeAuthorizedRequest(
            "GpsSessions",
            JSONObject(mapper.writeValueAsString(gpsSessionDto)),
            { response ->
                Log.d(TAG, response.toString())
                val responseDto = mapper.readValue(response.toString(), GpsSessionDto::class.java)
                onSuccess(responseDto)
            },
            { error ->
                Log.e(TAG, error.toString())
                Log.d(TAG, String(error.networkResponse.data, Charset.defaultCharset()))
                onError()
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
            })
    }

    fun addLocationToSession(gpsLocationDto: GpsLocationDto, onError: () -> Unit) {
        val handler = WebApiHandler.getInstance(context)
        handler.makeAuthorizedRequest(
            "GpsLocations",
            JSONObject(mapper.writeValueAsString(gpsLocationDto)),
            { response ->
                Log.d(TAG, response.toString())
            }, { error ->
                Log.e(TAG, error.toString())
                Log.d(TAG, String(error.networkResponse.data, Charset.defaultCharset()))
                onError()
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
            })
    }
}