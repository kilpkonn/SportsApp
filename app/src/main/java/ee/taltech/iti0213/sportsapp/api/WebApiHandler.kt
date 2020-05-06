package ee.taltech.iti0213.sportsapp.api

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.util.concurrent.atomic.AtomicReference


class WebApiHandler private constructor(var context: Context) {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
        private var instance: AtomicReference<WebApiHandler?> = AtomicReference()

        @Synchronized
        fun getInstance(context: Context): WebApiHandler {
            if (instance.get() == null) {
                instance.set(WebApiHandler(context))
            }
            return instance.get()!!
        }
    }

    var jwt: AtomicReference<String?> = AtomicReference()

    private var requestQueue: RequestQueue? = null
        get() {
            if (field == null) {
                field = Volley.newRequestQueue(context)
            }
            return field
        }

    @Synchronized
    fun <T> addToRequestQueue(request: Request<T>, tag: String = TAG) {
        Log.d(TAG, request.url)
        request.tag = tag
        requestQueue?.add(request)
    }

    @Synchronized
    fun cancelPendingRequest(tag: String) {
        if (requestQueue != null) {
            requestQueue!!.cancelAll(if (TextUtils.isEmpty(tag)) TAG else tag)
        }
    }
}
