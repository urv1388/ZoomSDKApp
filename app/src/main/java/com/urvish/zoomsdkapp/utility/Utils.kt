package com.urvish.zoomsdkapp.utility

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import androidx.core.app.ActivityCompat

object Utils {

    fun userPermissionAlertDialog(
        title: String,
        message: String,
        arrayOfPermission: Array<String>,
        permissionCode: Int, activiy: Activity
    ) {
        AlertDialog.Builder(activiy)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok",
                DialogInterface.OnClickListener { dialog, which ->
                    ActivityCompat.requestPermissions(
                        activiy,
                        arrayOfPermission,
                        permissionCode
                    )
                })
            .create().show()
    }
}