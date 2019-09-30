package minh.quy.musicplayer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import minh.quy.musicplayer.R
import minh.quy.musicplayer.fragment.PlaySongFragment
import minh.quy.musicplayer.model.Song
import minh.quy.musicplayer.sharepreferences.UserPreferences
import kotlin.random.Random

enum class PlaybackType(var type: Int) {
    PLAY(0),
    PAUSE(1),
    NEXT(2),
    PREVIOUS(3)
}

class PlayMusicService : Service(), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener {
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
            } else {
                songPos = songList.size - 1
                mediaPlayer?.pause()
            }
        } else {
            playMusic()
        }
    }

    companion object {
        val ACTION_PLAY = "ACTION_PLAY"
        val ACTION_PAUSE = "ACTION_PAUSE"
        val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        val ACTION_NEXT = "ACTION_NEXT"
        val ACTION_STOP = " ACTION_STOP"
        val ACTION_UPDATE_VIEW = "action.update.view"
        val EXTRA_SONG_ID = "extra.song.playlisyId"
    }

    private val CHANNEL_ID = "channel_id"
    val NOTIFICATION_ID = 100
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
    var mediasessionManager: MediaSessionManager? = null
    var mediaSession: MediaSession? = null
    var mediaControl: MediaController.TransportControls? = null


    override fun onBind(p0: Intent?): IBinder? {
        return this.binder
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        initMediaPlayer()
        getRepeatAndSuffleMode()
        currentDuration = getSongDuration()
        currenSongId = getSongId()
        createNotificationChannel()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntent(intent)
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
        saveRepeatAndSuffleMode()
        saveSongId()

    }

    fun initMediaPlayer() {
        mediaPlayer?.setWakeMode(
            getApplicationContext(),
            PowerManager.PARTIAL_WAKE_LOCK
        )
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnCompletionListener(this)
        mediasessionManager =
            getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager?
        mediaSession = MediaSession(applicationContext, "MediaPlayer")
        mediaControl = mediaSession?.controller?.transportControls
        mediaSession?.isActive = true
        var state = PlaybackState.Builder()
            .setActions(PlaybackState.ACTION_PLAY)
            .setState(
                PlaybackState.STATE_STOPPED,
                PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                0f
            )
            .build();
        mediaSession?.setPlaybackState(state)
        mediaSession?.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession?.setCallback(object : MediaSession.Callback() {
            override fun onPause() {
                createNotification(PlaybackType.PAUSE.type)
            }

            override fun onPlay() {
                super.onPlay()
                createNotification(PlaybackType.PLAY.type)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                createNotification(PlaybackType.NEXT.type)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                createNotification(PlaybackType.PREVIOUS.type)
            }
        })
    }

    fun createNotification(actionType: Int) {
        val song = songList[songPos]
        var icon_pause_play = R.drawable.ic_play_pause_white
        var play_pausePendingIntent: PendingIntent = playbackAction(PlaybackType.PAUSE.type)
        if (actionType == PlaybackType.PAUSE.type) {
            icon_pause_play = R.drawable.ic_play_play_white
            play_pausePendingIntent = playbackAction(PlaybackType.PAUSE.type)
        } else if (actionType == PlaybackType.PLAY.type) {
            icon_pause_play = R.drawable.ic_play_pause_white
            play_pausePendingIntent = playbackAction(PlaybackType.PLAY.type)
        }

        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        //create new notification
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(largeIcon)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(song.artistName)
            .setContentInfo(song.songName)

            .addAction(
                R.drawable.ic_play_previous_white,
                "next",
                playbackAction(PlaybackType.PREVIOUS.type)
            )
            .addAction(icon_pause_play, "pause", playbackAction(PlaybackType.PREVIOUS.type))
            .addAction(R.drawable.ic_play_next_white, "previous", play_pausePendingIntent)
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }

    }

    fun handleIntent(intent: Intent?) {
        intent?.let {
            when (intent.action) {
                ACTION_PLAY -> mediaControl?.play()
                ACTION_PAUSE -> mediaControl?.pause()
                ACTION_PREVIOUS -> mediaControl?.skipToPrevious()
                ACTION_NEXT -> mediaControl?.skipToNext()
                else -> {
                }
            }
        }
    }

    fun removeNotification() {
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            cancel(NOTIFICATION_ID)
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun playbackAction(actionType: Int): PendingIntent {
        val intentAction = Intent(this, PlayMusicService.javaClass)
        when (actionType) {
            PlaybackType.PAUSE.type -> {
                intentAction.setAction(ACTION_PAUSE)
                return PendingIntent.getService(this, actionType, intentAction, 0)
            }
            PlaybackType.PLAY.type -> {
                intentAction.setAction(ACTION_PLAY)
                return PendingIntent.getService(this, actionType, intentAction, 0)
            }
            PlaybackType.NEXT.type -> {
                intentAction.setAction(ACTION_NEXT)
                return PendingIntent.getService(this, actionType, intentAction, 0)
            }
            PlaybackType.PREVIOUS.type -> {
                intentAction.setAction(ACTION_PREVIOUS)
                return PendingIntent.getService(this, actionType, intentAction, 0)
            }
            else -> {
                return PendingIntent.getService(this, actionType, intentAction, 0)
            }
        }
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

    fun sendBroadcastUpdateView(position: Int) {
        val intent = Intent(ACTION_UPDATE_VIEW)
        intent.putExtra(EXTRA_SONG_ID, songList[position].songId)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    fun saveRepeatAndSuffleMode() {
        val userPreferences = UserPreferences.getInstance(applicationContext)
        userPreferences?.saveRepeatMode(currenRepeat)
        userPreferences?.saveSuffleMode(isSuffle)

    }

    fun saveSongId() {
        val userPreferences = UserPreferences.getInstance(applicationContext)
        userPreferences?.saveSongId(currenSongId!!)
    }

    fun getSongId(): String? {
        val userPreferences = UserPreferences.getInstance(applicationContext)
        return userPreferences?.getSongId()
    }


    fun getSongDuration(): Long? {
        val userPreferences = UserPreferences.getInstance(applicationContext)
        return userPreferences?.getSongDuration()
    }


    fun getRepeatAndSuffleMode() {
        val userPreferences = UserPreferences.getInstance(applicationContext)
        currenRepeat = userPreferences!!.getRepeatMode()
        isSuffle = userPreferences.getSuffleMode()
        setRepeatMode()
    }

    fun setRepeatMode() {
        if (currenRepeat == PlaySongFragment.Repeat.REPEAT_ONE.value) {
            isRepeatOne = true
        } else if (currenRepeat == PlaySongFragment.Repeat.REPEAT_ALL.value) {
            isRepeatAll = true
        } else {
            isRepeatOne = false
            isRepeatAll = false
        }
    }

    fun actionNext() {
        if (isSuffle) {
            songPos = Random.nextInt(songList.size)
        } else {
            if (songPos < songList.size - 1) {
                songPos++
            } else {
                songPos = 0
            }
        }

        playMusic()
    }

    fun actionBtnPlay() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer?.pause()
        } else {
            if (songPos < songList.size - 1) {
                mediaPlayer?.start()
            } else {
                playMusic()
            }

        }
        sendBroadcastUpdateView(songPos)
    }

    fun actionPrevious() {
        if (isSuffle) {
            songPos = Random.nextInt(songList.size)
        } else {
            if (songPos == 0) {
                songPos = songList.size - 1
            } else {
                songPos--
            }
        }
        playMusic()
    }

}