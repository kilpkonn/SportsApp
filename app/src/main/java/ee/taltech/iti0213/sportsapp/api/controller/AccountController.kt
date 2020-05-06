package ee.taltech.iti0213.sportsapp.api.controller

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.fasterxml.jackson.databind.ObjectMapper
import ee.taltech.iti0213.sportsapp.api.WebApiHandler
import ee.taltech.iti0213.sportsapp.api.dto.GpsSessionDto
import ee.taltech.iti0213.sportsapp.api.dto.LoginDto
import ee.taltech.iti0213.sportsapp.api.dto.LoginResponseDto
import ee.taltech.iti0213.sportsapp.api.dto.RegisterDto
import org.json.JSONObject
import java.nio.charset.Charset


class AccountController private constructor(val context: Context) {

    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName

        private const val BASE_URL = "https://sportmap.akaver.com/api/"
        private const val API_VERSION = 1.0

        private var instance: AccountController? = null
        private val mapper = ObjectMapper()

        @Synchronized
        fun getInstance(context: Context): AccountController {
            if (instance == null) {
                instance = AccountController(context)
            }
            return instance!!
        }
    }

    fun createAccount(registerDto: RegisterDto, onSuccess: (r: LoginResponseDto) -> Unit) {
        val handler = WebApiHandler.getInstance(context)
        val httpRequest = JsonObjectRequest(
            Request.Method.POST,
            "${BASE_URL}v${API_VERSION}/Account/Register",
            JSONObject(mapper.writeValueAsString(registerDto)),
            Response.Listener { response ->
                Log.d(TAG, response.toString())
                val responseDto = mapper.readValue(response.toString(), LoginResponseDto::class.java)
                handler.jwt.set(responseDto.token)
                onSuccess(responseDto)
            },
            Response.ErrorListener {error ->
                Log.e(TAG, error.toString())
                Log.d(TAG, String(error.networkResponse.data, Charset.defaultCharset()))
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
            }
        )
        handler.addToRequestQueue(httpRequest)
    }

    fun login(loginDto: LoginDto, onSuccess: (r: LoginResponseDto) -> Unit = { }) {
        val handler = WebApiHandler.getInstance(context)
        val httpRequest = JsonObjectRequest(
            Request.Method.POST,
            "${BASE_URL}v${API_VERSION}/Account/Login",
            JSONObject(mapper.writeValueAsString(loginDto)),
            Response.Listener { response ->
                val responseDto = mapper.readValue(response.toString(), LoginResponseDto::class.java)
                handler.jwt.set(responseDto.token)
                onSuccess(responseDto)
            },
            Response.ErrorListener {error ->
                Log.e(TAG, error.toString())
                Log.d(TAG, String(error.networkResponse.data, Charset.defaultCharset()))
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
            }
        )
        handler.addToRequestQueue(httpRequest)
    }
}