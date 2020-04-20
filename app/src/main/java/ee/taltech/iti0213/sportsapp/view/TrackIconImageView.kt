package ee.taltech.iti0213.sportsapp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.TrackLocation
import kotlin.math.max
import kotlin.math.min

class TrackIconImageView(context: Context,attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    companion object {
        const val PADDING = 0.05f
    }

    var color: Int = Color.RED
    var track: List<TrackLocation>? = null

    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)

        if (track == null || track?.size == 0) return

        val minLat = track?.minBy { location -> location.latitude }?.latitude ?: 0.0
        val maxLat = track?.maxBy { location -> location.latitude }?.latitude ?: 0.0
        val minLng = track?.minBy { location -> location.longitude }?.longitude ?: 0.0
        val maxLng = track?.maxBy { location -> location.longitude }?.longitude ?: 0.0

        // Should be keep dimensions?
        val latDelta = maxLat - minLat
        val lngDelta = maxLng - minLng

        val paint = Paint()
        paint.color = color

        val paddedWidth = width * (1 - PADDING * 2)

        var last = track?.first()
        for (location in track!!) {
            canvas?.drawLine(
                width * PADDING + ((location.longitude - minLng) / lngDelta * paddedWidth).toFloat(),
                height * (1 - PADDING) - ((location.latitude - minLat) / latDelta * paddedWidth).toFloat(),
                width * PADDING + ((last!!.longitude - minLng) / lngDelta * paddedWidth).toFloat(),
                height * (1 - PADDING) - ((last.latitude - minLat) / latDelta * paddedWidth).toFloat(),
                paint
            )
            last = location
        }
    }
}