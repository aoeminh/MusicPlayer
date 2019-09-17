package minh.quy.musicplayer.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.RequestPermission
import minh.quy.musicplayer.activity.MainActivity



open class BaseFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    var contextBase: Context? = null
    var isRequestPermission = false
    var isNeedRequest = true

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        contextBase = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        }
    }

    override fun onResume() {
        super.onResume()
        if (this is PlaylistFragment && isNeedRequest) {
            requestPermission()
            Log.d("minhnh", "onResume" + isRequestPermission)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("minhnh", "onResume" + "onRequestPermissionsResult")
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            mainActivity.songlist = mainActivity.scanDeviceForMp3Files()
            mainActivity.musicService?.songList?.clear()
            mainActivity.musicService?.songList?.addAll(mainActivity.songlist)
            mainActivity.getAllArtist()
            mainActivity.getAllAlbum()

        } else {
            if (this is PlaylistFragment) {
                showDialogSetting()
            }
        }
    }

    fun requestPermission() {
        if (!isHavePermission()) {
            isRequestPermission = true
            Log.d("minhnh", "requestPermission " + isRequestPermission)

            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                RequestPermission.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        }
    }

    fun isHavePermission() = ActivityCompat.checkSelfPermission(
        context!!,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    fun showDialogSetting() {
        isNeedRequest = false
        Log.d("minhnh","showDialogSetting")
        var builder = AlertDialog.Builder(activity)
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
                    closeApp()
                }
            }
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun closeApp(){
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(homeIntent)
        android.os.Process.killProcess(android.os.Process.myPid());

    }


}