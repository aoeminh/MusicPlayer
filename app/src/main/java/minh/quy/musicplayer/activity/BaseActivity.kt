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

    fun showPopupCreatePlaylist(){
        val alertDialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.popup_create_new_playlist,null)
        alertDialogBuilder.setView(dialogView)
        val alertDialog = alertDialogBuilder.create()
        dialogView.btn_create_playlist.setOnClickListener {
            if(!dialogView.edt_name_playlist.text.toString().isEmpty()){
                musicDatabase?.getPlaylistDAO()?.insertPlaylist(Playlist(dialogView.edt_name_playlist.text.toString()))
                Log.d("MinhNQ"," insert playlist")
                alertDialog.dismiss()
            }else{
                Toast.makeText(this,"Enter playlist name",Toast.LENGTH_SHORT).show()
            }

        }

        dialogView.btn_cancle_playlist.setOnClickListener{alertDialog.dismiss()}
        alertDialog.show()

    }
}