package minh.quy.musicplayer.Utils

import android.Manifest.permission.*
import android.R
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.*

class RequestPermission {


    companion object{
        val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
        @JvmStatic
        fun showDialog(
            msg: String, context: Context, permission: Array<String>
        ) {
            val alertBuilder = AlertDialog.Builder(context)
            alertBuilder.setCancelable(true)
            alertBuilder.setTitle("Permission necessary")
            alertBuilder.setMessage("$msg permission is necessary")
            alertBuilder.setPositiveButton(
                R.string.yes
            ) { dialog, which ->
                for (s in permission) {
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf<String>(s),
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
            }
            val alert = alertBuilder.create()
            alert.show()
        }

        fun requestPermision(context: Context): Boolean {
            val currentAPIVersion = Build.VERSION.SDK_INT
            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                if (checkSelfPermission(
                        context,
                        READ_EXTERNAL_STORAGE
                    ) + checkSelfPermission(
                        context,
                        WRITE_EXTERNAL_STORAGE
                    ) !== PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity,
                            READ_EXTERNAL_STORAGE
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            context,
                            WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        showDialog(
                            "External storage", context, arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
                        )

                    } else {
                        ActivityCompat
                            .requestPermissions(
                                context,
                                arrayOf<String>(
                                    READ_EXTERNAL_STORAGE,
                                    WRITE_EXTERNAL_STORAGE
                                ),
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                            )
                    }
                    return false
                } else {
                    return true
                }

            } else {
                return true
            }
        }
    }
}