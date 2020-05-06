package ee.taltech.iti0213.sportsapp.activity

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import ee.taltech.iti0213.sportsapp.R
import ee.taltech.iti0213.sportsapp.api.WebApiHandler
import ee.taltech.iti0213.sportsapp.api.controller.AccountController
import ee.taltech.iti0213.sportsapp.api.controller.TrackSyncController
import ee.taltech.iti0213.sportsapp.api.dto.GpsLocationDto
import ee.taltech.iti0213.sportsapp.api.dto.GpsSessionDto
import ee.taltech.iti0213.sportsapp.api.dto.LoginDto
import ee.taltech.iti0213.sportsapp.api.dto.RegisterDto
import ee.taltech.iti0213.sportsapp.db.domain.User
import ee.taltech.iti0213.sportsapp.db.repository.*
import ee.taltech.iti0213.sportsapp.detector.FlingDetector
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.TrackLocation
import ee.taltech.iti0213.sportsapp.util.HashUtils
import ee.taltech.iti0213.sportsapp.util.TrackUtils
import java.time.LocalDateTime
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private val accountController = AccountController.getInstance(this)
    private val trackSyncController = TrackSyncController.getInstance(this)

    private val userRepository = UserRepository.open(this)
    private val offlineSessionsRepository = OfflineSessionsRepository.open(this)
    private val trackSummaryRepository = TrackSummaryRepository.open(this)
    private val trackLocationsRepository = TrackLocationsRepository.open(this)
    private val checkpointsRepository = CheckpointsRepository.open(this)
    private val wayPointsRepository = WayPointsRepository.open(this)

    private var user: User? = null

    private lateinit var flingDetector: FlingDetector

    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText

    private lateinit var layoutRegister: ConstraintLayout
    private lateinit var layoutSettings: ConstraintLayout

    private lateinit var buttonRegister: Button
    private lateinit var buttonSyncTrack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_NO_TITLE)
        actionBar?.hide()
        supportActionBar?.hide()

        setContentView(R.layout.activity_settings)

        flingDetector = FlingDetector(this)

        flingDetector.onFlingLeft = Runnable { onFlingLeft() }

        editTextUsername = findViewById(R.id.txt_username)
        editTextEmail = findViewById(R.id.txt_email)
        editTextPassword = findViewById(R.id.txt_password)
        editTextFirstName = findViewById(R.id.txt_first_name)
        editTextLastName = findViewById(R.id.txt_last_name)
        buttonRegister = findViewById(R.id.btn_register)

        layoutRegister = findViewById(R.id.layout_register)
        layoutSettings = findViewById(R.id.layout_settings)

        buttonSyncTrack = findViewById(R.id.btn_sync_track)

        buttonRegister.setOnClickListener { onRegister() }
        buttonSyncTrack.setOnClickListener { onTrackSync() }

        user = userRepository.readUser()

        if (user == null) {
            layoutRegister.visibility = View.VISIBLE
            layoutSettings.visibility = View.GONE
        } else {
            layoutRegister.visibility = View.GONE
            layoutSettings.visibility = View.VISIBLE

            accountController.login(LoginDto(user!!.email, user!!.password + "-A"))
        }
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

    override fun onDestroy() {
        super.onDestroy()
        userRepository.close()
        offlineSessionsRepository.close()
        trackSummaryRepository.close()
        trackSummaryRepository.close()
        checkpointsRepository.close()
        wayPointsRepository.close()
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
        if (editTextEmail.text.length < 3 || !editTextEmail.text.contains('@')) {
            Snackbar.make(findViewById(R.id.activity_settings), "Invalid email!", Snackbar.LENGTH_LONG).show()
            return
        }
        if (editTextPassword.text.length < 8
            || editTextPassword.text.toString().matches(Regex("^[a-zA-Z0-9]*$"))
            || !editTextPassword.text.toString().matches(Regex(".*[a-z].*"))
            || !editTextPassword.text.toString().matches(Regex(".*[A-Z].*"))
        ) {
            Snackbar.make(findViewById(R.id.activity_settings), "Invalid password!", Snackbar.LENGTH_LONG).show()
            return
        }
        if (editTextLastName.text.isEmpty()) {
            Snackbar.make(findViewById(R.id.activity_settings), "First name is required!", Snackbar.LENGTH_LONG).show()
            return
        }
        if (editTextFirstName.text.isEmpty()) {
            Snackbar.make(findViewById(R.id.activity_settings), "Last name is required!", Snackbar.LENGTH_LONG).show()
            return
        }
        val registerDto = RegisterDto(
            editTextEmail.text.toString(),
            HashUtils.md5(editTextPassword.text.toString()) + "-A",
            editTextFirstName.text.toString(),
            editTextLastName.text.toString()
        )

        val user = User(
            editTextUsername.text.toString(),
            registerDto.email,
            HashUtils.md5(editTextPassword.text.toString()),
            registerDto.firstName,
            registerDto.lastName
        )

        accountController.createAccount(registerDto) {
            userRepository.saveUser(user)
            layoutRegister.visibility = View.GONE
            layoutSettings.visibility = View.VISIBLE
        }
    }

    // ========================================== HELPER FUNCTIONS =========================================

    private fun onTrackSync() {
        TrackUtils.syncTracks(
            offlineSessionsRepository,
            trackSummaryRepository,
            trackLocationsRepository,
            checkpointsRepository,
            wayPointsRepository,
            trackSyncController
        )
    }

}
