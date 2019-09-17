package minh.quy.musicplayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import minh.quy.musicplayer.model.Song
import kotlin.random.Random

class PlayMusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    override fun onPrepared(p0: MediaPlayer?) {
        mediaPlayer?.start()
    }

    override fun onCompletion(p0: MediaPlayer?) {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        Log.d("minhnh", "onCompletion")

        //repeat one mode
        if (isRepeatOne) {
            mediaPlayer?.isLooping = true
            playMusic()
            return
        }

        // suffle modepaus
        if (isSuffle) {
            songPos = Random.nextInt(songList.size)
            playMusic()
            return
        }
        // repeat all mode or normal mode
        songPos++
        if (songPos > songList.size - 1) {
            if (isRepeatAll) {
                songPos = 0
                playMusic()
            }
        } else {
            playMusic()
        }
    }

    companion object{
        val ACTION_UPDATE_VIEW = "action.update.view"
        val EXTRA_SONG_ID = "extra.song.id"
    }

    var binder: Binder = MusicBinder()
    var mediaPlayer: MediaPlayer? = null
    var songList: MutableList<Song> = arrayListOf()
    var songPos: Int = 0
    var isRepeatOne = false
    var isRepeatAll = true
    var isSuffle = false
    var currenRepeat = 0
    var currenSongId: String? = null
    var currentDuration: Long? = 0
    var isFirstPlay = true

    override fun onBind(p0: Intent?): IBinder? {
        return this.binder
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        initMediaPlayer()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)

    }

    override fun onUnbind(intent: Intent?): Boolean {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        stopSelf()

        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun initMediaPlayer() {
        mediaPlayer?.setWakeMode(
            getApplicationContext(),
            PowerManager.PARTIAL_WAKE_LOCK
        )
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnCompletionListener(this)


    }

    fun setSongs(songs: MutableList<Song>) {
        this.songList.clear()
        this.songList.addAll(songs)
    }

    fun setSongPosition(position: Int) {
        songPos = position
    }

    fun playMusic() {
        mediaPlayer?.reset()
        songPos.run {
            val songUri = Uri.parse(songList.get(songPos).data)
            mediaPlayer?.setDataSource(applicationContext, songUri)
            mediaPlayer?.prepare()
            songList[songPos].isSelected = true
            currenSongId = songList[songPos].songId
        }

        sendBroadcastUpdateView(songPos)
    }

    inner class MusicBinder : Binder() {
        internal val service: PlayMusicService
            get() = this@PlayMusicService
    }

    fun sendBroadcastUpdateView(position: Int){
        val intent = Intent(ACTION_UPDATE_VIEW)
        intent.putExtra(EXTRA_SONG_ID,songList[position].songId)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


}