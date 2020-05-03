package ee.taltech.iti0213.sportsapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import ee.taltech.iti0213.sportsapp.R
import ee.taltech.iti0213.sportsapp.api.controller.TrackSyncController
import ee.taltech.iti0213.sportsapp.api.dto.RegisterDto
import ee.taltech.iti0213.sportsapp.detector.FlingDetector
import ee.taltech.iti0213.sportsapp.util.HashUtils

class SettingsActivity : AppCompatActivity() {

    private val trackSyncController = TrackSyncController.getInstance(this)

    private lateinit var flingDetector: FlingDetector

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText

    private lateinit var buttonRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_NO_TITLE)
        actionBar?.hide()
        supportActionBar?.hide()

        setContentView(R.layout.activity_settings)

        flingDetector = FlingDetector(this)

        flingDetector.onFlingLeft = Runnable { onFlingLeft() }

        editTextEmail = findViewById(R.id.txt_email)
        editTextPassword = findViewById(R.id.txt_password)
        editTextFirstName = findViewById(R.id.txt_first_name)
        editTextLastName = findViewById(R.id.txt_last_name)
        buttonRegister = findViewById(R.id.btn_register)

        buttonRegister.setOnClickListener { onRegister() }
    }

    // ======================================= LIFECYCLE CALLBACKS ====================================

    override fun onStart() {
        super.onStart()

        overridePendingTransition(
            R.anim.slide_in_from_left,
            R.anim.slide_out_to_right
        )
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(
            R.anim.slide_in_from_right,
            R.anim.slide_out_to_left
        )
    }

    // ======================================== FLING DETECTION =======================================

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        flingDetector.update(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun onFlingLeft() {
        moveTaskToBack(true)
    }

    // ====================================== REGISTER LOGIC ===========================================

    private fun onRegister() {
        if (editTextEmail.text.length < 3 || editTextPassword.text.length < 6 || editTextFirstName.text.isEmpty() || editTextLastName.text.isEmpty()) {
            Snackbar.make(findViewById(R.id.activity_settings), "Invalid credentials!", Snackbar.LENGTH_LONG).show()
            return
        }
        val registerDto = RegisterDto(
            editTextEmail.text.toString(),
            HashUtils.md5(editTextPassword.text.toString()) + "-",
            editTextFirstName.text.toString(),
            editTextLastName.text.toString()
        )

        trackSyncController.createAccount(registerDto)
    }
}
