package ee.taltech.iti0213.sportsapp.util.serializer

import android.content.Context
import android.os.Environment
import android.util.Log
import io.jenetics.jpx.GPX
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class TrackSerializer {
    companion object {
        private const val FILE_PATH = "sportsApp/gpx"
    }
    private var mExternalFile: File?=null

    val isExternalStorageReadOnly: Boolean get() {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState
    }
    val isExternalStorageAvailable: Boolean get() {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == extStorageState
    }

    fun saveGpx(gpx: GPX, name: String, context: Context, onSuccess: () -> Unit, onError: () -> Unit) {
        mExternalFile = File(context.getExternalFilesDir(FILE_PATH), name.replace(" ", "_"))
        Log.d("", File(context.getExternalFilesDir(FILE_PATH), name).absolutePath.toString())
        try {
            FileOutputStream(mExternalFile!!).use { out ->
                GPX.write(gpx, out)
            }
            onSuccess()
        } catch (e: IOException) {
            e.printStackTrace()
            onError()
        }
    }
}