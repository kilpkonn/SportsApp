package ee.taltech.iti0213.sportsapp.component.view

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.TrackLocation
import kotlin.math.*

class TrackIconImageView(context: Context,attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    companion object {
        const val PADDING = 0.05f

        val argbEvaluator = ArgbEvaluator()
    }

    var color: Int = Color.RED
    var colorMax: Int = 0xFFff9e9e.toInt()
    var track: List<TrackLocation>? = null
    var maxSpeed = 99.99

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
        paint.strokeWidth = 3f

        val paddedWidth = width * (1 - PADDING * 2)

        var last = track?.first()
        for (location in track!!) {
            val relSpeed = min(1.0, TrackLocation.calcDistanceBetween(location, last!!) /
                    ((location.elapsedTimestamp - (last.elapsedTimestamp)  + 1) / 1_000_000_000 / 3.6) / maxSpeed)

            paint.color = argbEvaluator.evaluate(relSpeed.pow(0.5).toFloat(), color, colorMax) as Int

            canvas?.drawLine(
                width * PADDING + ((location.longitude - minLng) / lngDelta * paddedWidth).toFloat(),
                height * (1 - PADDING) - ((location.latitude - minLat) / latDelta * paddedWidth).toFloat(),
                width * PADDING + ((last.longitude - minLng) / lngDelta * paddedWidth).toFloat(),
                height * (1 - PADDING) - ((last.latitude - minLat) / latDelta * paddedWidth).toFloat(),
                paint
            )
            last = location
        }
    }
}