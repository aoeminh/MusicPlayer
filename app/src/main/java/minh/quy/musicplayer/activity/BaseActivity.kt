package minh.quy.musicplayer.activity

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.popup_create_new_playlist.*
import kotlinx.android.synthetic.main.popup_create_new_playlist.view.*
import kotlinx.android.synthetic.main.popup_create_new_playlist.view.edt_name_playlist
import minh.quy.musicplayer.R
import minh.quy.musicplayer.database.MusicDatabase
import minh.quy.musicplayer.model.Playlist

abstract class BaseActivity: AppCompatActivity() {

     var musicDatabase: MusicDatabase? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        musicDatabase = MusicDatabase.getInstanceDatabase(this)

    }
    abstract fun getLayoutId(): Int


}