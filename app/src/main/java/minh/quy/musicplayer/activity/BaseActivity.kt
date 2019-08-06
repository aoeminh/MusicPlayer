package minh.quy.musicplayer.activity

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import minh.quy.musicplayer.R
import minh.quy.musicplayer.database.MusicDatabase

abstract class BaseActivity: AppCompatActivity() {

    lateinit var musicDatabase: MusicDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        musicDatabase = MusicDatabase.getInstanceDatabase(this)

    }
    abstract fun getLayoutId(): Int

    fun showPopupCreatePlaylist(context: Context){


    }
}