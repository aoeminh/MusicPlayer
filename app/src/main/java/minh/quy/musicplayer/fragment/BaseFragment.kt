package minh.quy.musicplayer.fragment

import android.app.AlertDialog
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.RequestPermission
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.model.Song
import java.io.File


open class BaseFragment : Fragment() {

    val WRITE_SETTING_PERMISSION = 99

    lateinit var mainActivity: MainActivity
    var contextBase: Context? = null
    var isRequestPermission = false
    var isNeedRequest = true

    override fun onAttach(context: Context) {
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


    fun setRingtone(song: Song) {
        val path = song.data
        val file = File(path!!)
        val contentValues = ContentValues();
        contentValues.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath())
        val filterName = path.substring(path.lastIndexOf("/") + 1)
        contentValues.put(MediaStore.MediaColumns.TITLE, filterName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
        contentValues.put(MediaStore.MediaColumns.SIZE, file.length())
        contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true)
        val uri = MediaStore.Audio.Media.getContentUriForPath(path)
        val cursor = context!!.getContentResolver().query(
            uri!!,
            null,
            MediaStore.MediaColumns.DATA + "=?",
            arrayOf(path),
            null
        )
        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            val id = cursor.getString(0)
            contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true)
            context!!.getContentResolver().update(
                uri,
                contentValues,
                MediaStore.MediaColumns.DATA + "=?",
                arrayOf(path)
            )
            var newuri = ContentUris.withAppendedId(uri, id.toLong())
            try {
                RingtoneManager.setActualDefaultRingtoneUri(
                    context,
                    RingtoneManager.TYPE_RINGTONE,
                    newuri
                )
                Toast.makeText(context, resources.getString(R.string.set_ringtone_success), Toast.LENGTH_SHORT).show();
            } catch (t: Throwable) {
                t.printStackTrace();
            }
            cursor.close();
        }

    }

    open fun showToastOneSongAdded(){
        Toast.makeText(context, String.format(resources.getString(R.string.added_one_song),1), Toast.LENGTH_SHORT).show();

    }

    open fun showDialogSetting() {
        isNeedRequest = false
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