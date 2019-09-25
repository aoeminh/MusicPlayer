package minh.quy.musicplayer.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.RequestPermission

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        requestPermission()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gotoMain()
        } else {
            showDialogSetting()
        }
    }

    fun requestPermission() {
        if (isHavePermission()) {
            gotoMain()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_SETTINGS
                ),
                RequestPermission.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        }
    }

    fun isHavePermission() = (ActivityCompat.checkSelfPermission(
        this,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        this,
        android.Manifest.permission.WRITE_SETTINGS
    ) == PackageManager.PERMISSION_GRANTED)

    fun showDialogSetting() {

        Log.d("minhnh", "onCreate SplashActivity")
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setCancelable(false)
            setMessage(R.string.permission_necessary)
            setPositiveButton(R.string.ok) { dialog, which ->
                run {
                    requestPermission()
                }
            }
            setNegativeButton(R.string.cancel) { dialogInterface, i ->
                run {
                    finish()
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun gotoMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }
}
