package ee.taltech.iti0213.sportsapp.api.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.fasterxml.jackson.databind.ObjectMapper
import ee.taltech.iti0213.sportsapp.api.WebApiHandler
import ee.taltech.iti0213.sportsapp.api.domain.LoginResponseDto
import ee.taltech.iti0213.sportsapp.api.domain.RegisterDto
import org.json.JSONObject


class TrackSyncController private constructor(val context: Context) {

    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName

        private val BASE_URL = "https://sportmap.akaver.com/api/"
        private val API_VERSION = 1.0

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

    fun createAccount(registerDto: RegisterDto) {
        val handler = WebApiHandler.getInstance(context)
        val httpRequest = JsonObjectRequest(
            Request.Method.POST,
            "${BASE_URL}v${API_VERSION}/register",
            JSONObject(mapper.writeValueAsString(registerDto)),
            Response.Listener { response ->
                Log.d(TAG, response.toString())

                val responseDto = mapper.readValue(response.toString(), LoginResponseDto::class.java)
                handler.jwt = responseDto.token
            },
            Response.ErrorListener {error ->
                Log.e(TAG, error.toString())
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
            }
        )
        handler.addToRequestQueue(httpRequest)
    }
}