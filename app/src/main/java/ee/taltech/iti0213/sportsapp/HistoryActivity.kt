package ee.taltech.iti0213.sportsapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ee.taltech.iti0213.sportsapp.detector.FlingDetector

class HistoryActivity : AppCompatActivity() {

    private lateinit var flingDetector: FlingDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        flingDetector = FlingDetector(this)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_from_right,R.anim.slide_into_right);
    }

    // ============================= FLING DETECTOR CALLBACKS ==========================

    fun onFlingRight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else finish()
    }
}
