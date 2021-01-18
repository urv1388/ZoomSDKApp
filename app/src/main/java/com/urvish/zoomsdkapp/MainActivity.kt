package com.urvish.zoomsdkapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.urvish.zoomsdkapp.utility.Utils.userPermissionAlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import us.zoom.sdk.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_PERMISSION_CODE = 1
        const val RECORD_AUDIO_PERMISSION_CODE = 2
        const val READ_STORAGE_PERMISSION_CODE = 3
        const val DOMAIN = "zoom.us"
    }

    private val arrayOfAllPermission = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val authListener = object : ZoomSDKAuthenticationListener {
        override fun onZoomSDKLoginResult(result: Long) {
            if (result.toInt() == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                // Once we verify that the request was successful, we may start the meeting
                startMeeting(this@MainActivity)
            }
        }

        override fun onZoomIdentityExpired() = Unit
        override fun onZoomSDKLogoutResult(p0: Long) = Unit
        override fun onZoomAuthIdentityExpired() = Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeSdk(this@MainActivity)
        checkAllPermission()
        checkedTextViewCamera.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userPermissionAlertDialog(
                    "Camera Permission",
                    "This permission is needed because of record video.",
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    CAMERA_PERMISSION_CODE, this
                )
            }
        }

        checkedTextViewAudio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userPermissionAlertDialog(
                    "Audio Permission",
                    "This permission is needed because of record audio.",
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                    ),
                    RECORD_AUDIO_PERMISSION_CODE, this
                )
            }
        }

        checkedTextViewReadStorage.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userPermissionAlertDialog(
                    "Read external storage permission",
                    "This permission is needed because zoom can share data.",
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    ),
                    READ_STORAGE_PERMISSION_CODE, this
                )
            }
        }
    }

    private fun initializeSdk(context: Context) {
        val sdk = ZoomSDK.getInstance()
        val params = ZoomSDKInitParams().apply {
            appKey = BuildConfig.APP_KEY
            appSecret = BuildConfig.APP_SECRET
            domain = DOMAIN
            enableLog = true // Optional: enable logging for debugging
        }
        val listener = object : ZoomSDKInitializeListener {
            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {
                if (errorCode == ZoomError.ZOOM_ERROR_SUCCESS) {
                    buttonJoinMeeting.isEnabled = true
                    buttonStartMeeting.isEnabled = true
                }
            }

            override fun onZoomAuthIdentityExpired() = Unit
        }
        sdk.initialize(context, listener, params)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_CODE, RECORD_AUDIO_PERMISSION_CODE, READ_STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty()) {
                    grantResults.forEachIndexed { index, value ->
                        if (value == PackageManager.PERMISSION_GRANTED) {
                            isPermissionGranted(
                                permissions[index], true
                            )
                        } else {
                            isPermissionGranted(
                                permissions[index], false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun isPermissionGranted(s: String?, isGranted: Boolean) {
        when (s) {
            Manifest.permission.CAMERA -> {
                checkedTextViewCamera.isChecked = isGranted
                checkedTextViewCamera.isEnabled = !isGranted
            }
            Manifest.permission.RECORD_AUDIO -> {
                checkedTextViewAudio.isChecked = isGranted
                checkedTextViewAudio.isEnabled = !isGranted
            }
            Manifest.permission.READ_EXTERNAL_STORAGE -> {
                checkedTextViewAudio.isChecked = isGranted
                checkedTextViewAudio.isEnabled = !isGranted
            }
        }
    }

    private fun checkAllPermission() {
        arrayOfAllPermission.forEachIndexed { _: Int, permission: String ->
            isPermissionGranted(
                permission, ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            )
        }
    }

    private fun joinMeeting(
        context: Context,
        mName: String = "Urvish",
        mNumber: String = "3867581641",
        mPassword: String = "eMC2"
    ) {

        val meetingService = ZoomSDK.getInstance().meetingService
        val options = JoinMeetingOptions()
        val params = JoinMeetingParams().apply {
            displayName = mName
            meetingNo = mNumber
            password = mPassword
        }
        meetingService.joinMeetingWithParams(context, params, options)
    }

    fun onClickStartMeeting(view: View) {
        if (ZoomSDK.getInstance().isLoggedIn) {
            startMeeting(this)
        } else {
            createLoginDialog()
        }
    }

    fun onClickJoinMeeting(view: View) {
        if (ZoomSDK.getInstance().isInitialized) {
            createJoinMeetingDialog()
        }
    }

    private fun login(username: String, password: String) {
        val result = ZoomSDK.getInstance().loginWithZoom(username, password)
        if (result == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
            // Request executed, listen for result to start meeting
            ZoomSDK.getInstance().addAuthenticationListener(authListener)
        }
    }

    /**
     * Start an instant meeting as a logged-in user. An instant meeting has a meeting number and
     * password generated when it is created.
     */
    private fun startMeeting(context: Context) {
        val zoomSdk = ZoomSDK.getInstance()
        if (zoomSdk.isLoggedIn) {
            val meetingService = zoomSdk.meetingService
            val options = StartMeetingOptions()
            meetingService.startInstantMeeting(context, options)
        }
    }

    private fun createLoginDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(R.layout.login_dialog)
        alertDialog.setTitle("Please enter your zoom login credentials!")
        alertDialog.setCancelable(false)
        alertDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setPositiveButton("Log In") { dialog, _ ->
            dialog as AlertDialog
            val emailInput = dialog.findViewById<TextInputEditText>(R.id.inputEditTextEmail)
            val passwordInput = dialog.findViewById<TextInputEditText>(R.id.inputEditTextPassword)
            val email = emailInput?.text?.toString()
            val password = passwordInput?.text?.toString()
            email?.takeIf { it.isNotEmpty() }?.let { emailAddress ->
                password?.takeIf { it.isNotEmpty() }?.let { pw ->
                    login(emailAddress, pw)
                }
            }
            dialog.dismiss()
        }.show()
    }

    private fun createJoinMeetingDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(R.layout.join_dialog)
        alertDialog.setTitle("Join Meeting")
        alertDialog.setTitle("You can join this meeting by enter all information!")
        alertDialog.setCancelable(false)
        alertDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setPositiveButton("Join") { dialog, _ ->
            dialog as AlertDialog
            val inputEditTextPersonName =
                dialog.findViewById<TextInputEditText>(R.id.inputEditTextPersonName)
            val textInputLayoutMeetingNo =
                dialog.findViewById<TextInputEditText>(R.id.inputEditTextMeetingNo)
            val textInputLayoutPassword =
                dialog.findViewById<TextInputEditText>(R.id.inputEditTextPassword)
            val personName = inputEditTextPersonName?.text?.toString()
            val meetingNo = textInputLayoutMeetingNo?.text?.toString()
            val password = textInputLayoutPassword?.text?.toString()
            personName?.takeIf { it.isNotEmpty() }?.let { pName ->
                meetingNo?.takeIf { it.isNotEmpty() }?.let { mNumber ->
                    password?.takeIf { it.isNotEmpty() }?.let { pw ->
                        joinMeeting(this@MainActivity, pName, mNumber, pw)
                    }
                }
            }
            dialog.dismiss()
        }.show()
    }
}