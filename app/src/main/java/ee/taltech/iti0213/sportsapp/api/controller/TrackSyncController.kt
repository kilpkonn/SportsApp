package ee.taltech.iti0213.sportsapp.api.controller

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

import com.fasterxml.jackson.databind.ObjectMapper
import ee.taltech.iti0213.sportsapp.api.WebApiHandler
import ee.taltech.iti0213.sportsapp.api.dto.GpsLocationDto
import ee.taltech.iti0213.sportsapp.api.dto.GpsSessionDto
import ee.taltech.iti0213.sportsapp.api.dto.LoginResponseDto
import org.json.JSONObject
import java.lang.reflect.Method
import java.nio.charset.Charset


class TrackSyncController private constructor(val context: Context) {

    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName

        private const val BASE_URL = "https://sportmap.akaver.com/api/"
        private const val API_VERSION = 1.0

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

    fun createNewSession(gpsSessionDto: GpsSessionDto, onSuccess: (r: GpsSessionDto) -> Unit) {
        val handler = WebApiHandler.getInstance(context)

       val httpRequest = object : JsonObjectRequest(
            Request.Method.POST,
            "${BASE_URL}v${API_VERSION}/GpsSessions",
            JSONObject(mapper.writeValueAsString(gpsSessionDto)),
            Response.Listener { response ->
                Log.d(TAG, response.toString())
                val responseDto = mapper.readValue(response.toString(), GpsSessionDto::class.java)
                onSuccess(responseDto)
            },
            Response.ErrorListener { error ->
                Log.e(TAG, error.toString())
                Log.d("", String(error.networkResponse.data, Charset.defaultCharset()))
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                for ((key, value) in super.getHeaders()) {
                    headers[key] = value
                }
                headers["Authorization"] = "Bearer " + handler.jwt.get()!!
                return headers
            }
        }

        handler.addToRequestQueue(httpRequest)
    }

    fun addLocationToSession(gpsLocationDto: GpsLocationDto) {
        val handler = WebApiHandler.getInstance(context)
        val httpRequest = object : JsonObjectRequest(
            Method.POST,
            "${BASE_URL}v${API_VERSION}/GpsLocations",
            JSONObject(mapper.writeValueAsString(gpsLocationDto)),
            Response.Listener { response ->
                Log.d(TAG, response.toString())
            },
            Response.ErrorListener { error ->
                Log.e(TAG, error.toString())
                Log.d("", String(error.networkResponse.data, Charset.defaultCharset()))
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                for ((key, value) in super.getHeaders()) {
                    headers[key] = value
                }
                headers["Authorization"] = "Bearer " + handler.jwt.get()!!
                return headers
            }
        }
        handler.addToRequestQueue(httpRequest)
    }
}