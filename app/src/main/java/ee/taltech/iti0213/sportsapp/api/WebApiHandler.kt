package ee.taltech.iti0213.sportsapp.api

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


class WebApiHandler private constructor(var context: Context) {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
        private var instance: WebApiHandler? = null

        @Synchronized
        fun getInstance(context: Context): WebApiHandler {
            if (instance == null) {
                instance = WebApiHandler(context)
            }
            return instance!!
        }
    }

    var jwt: String? = null

    private var requestQueue: RequestQueue? = null
        get() {
            if (field == null) {
                field = Volley.newRequestQueue(context)
            }
            return field
        }

    fun <T> addToRequestQueue(request: Request<T>, tag: String = TAG) {
        Log.d(TAG, request.url)
        request.tag = tag
        requestQueue?.add(request)
    }

    fun cancelPendingRequest(tag: String) {
        if (requestQueue != null) {
            requestQueue!!.cancelAll(if (TextUtils.isEmpty(tag)) TAG else tag)
        }
    }
}
