package minh.quy.musicplayer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media.session.MediaButtonReceiver
import minh.quy.musicplayer.R
import minh.quy.musicplayer.activity.MainActivity
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
        val ACTION_NOTIFICATION_BUTTON_CLICK = "action.button.click"
        val EXTRA_SONG_ID = "extra.song.playlisyId"
        val EXTRA_ACTION_TYPE = "extra.action.type"
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
    var mediaSession: MediaSessionCompat? = null
    lateinit var stateBuilder: PlaybackStateCompat.Builder
    var collap_notification: RemoteViews? = null
    var expand_notification: RemoteViews? = null
    var builder = NotificationCompat.Builder(this, CHANNEL_ID)
    var equalizer: Equalizer? = null

    override fun onBind(p0: Intent?): IBinder? {
        return this.binder
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        initMediaPlayer()
        initMediaSession()
        getRepeatAndSuffleMode()
        currentDuration = getSongDuration()
        currenSongId = getSongId()
        createNotificationChannel()
        register()
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
        saveRepeatAndSuffleMode()
        saveSongId()
        removeNotification()
        unregisterNotificationButtonClick()
    }

    fun initMediaPlayer() {
        mediaPlayer?.setWakeMode(
                getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK
        )
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnCompletionListener(this)
    }

    fun initMediaSession() {
        //        mediasessionManager =
//            getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager?
        mediaSession = MediaSessionCompat(applicationContext, "MediaPlayer")
//        mediaControl = mediaSession?.controller?.transportControls
        mediaSession?.setMediaButtonReceiver(null)
        stateBuilder = PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY
                                or PlaybackStateCompat.ACTION_PAUSE
                                or PlaybackStateCompat.ACTION_PLAY_PAUSE
                                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
        mediaSession?.setPlaybackState(stateBuilder.build())
        mediaSession?.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession?.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPause() {
                actionBtnPlay()
            }

            override fun onPlay() {
                super.onPlay()
                actionBtnPlay()
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                actionNext()
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                actionPrevious()
            }
        })

        mediaSession?.isActive = true
    }

    fun createNotification(actionType: Int) {
        val song = songList[songPos]
        var icon_pause_play = R.drawable.ic_play_pause_white
        var titlePlay_pause = ""
        if (actionType == PlaybackType.PAUSE.type) {
            icon_pause_play = R.drawable.ic_play_play_white
            titlePlay_pause = "Pause"
        } else if (actionType == PlaybackType.PLAY.type) {
            icon_pause_play = R.drawable.ic_play_pause_white
            titlePlay_pause = "Play"
        }

//        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val contentIntent =
                PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        //create new notification
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(song.songName)
                .setContentText(song.artistName)
                .setContentIntent(contentIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColorized(true)
                .addAction(
                        R.drawable.ic_play_previous_white,
                        "previous",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                this,
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        )
                )
                .addAction(
                        icon_pause_play,
                        titlePlay_pause,
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                this,
                                PlaybackStateCompat.ACTION_PAUSE
                        )
                )
                .addAction(
                        R.drawable.ic_play_next_white,
                        "next",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                this,
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        )
                )
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }

    }

    fun createCustomNotification() {
        val song = songList[songPos]
        creatExpandNotification(song)
        creatCollapNotification(song)
        val intent = Intent(this, MainActivity::class.java)
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        val contentIntent =
                PendingIntent.getActivity(this, 0, intent, 0)
        val notification = builder
                .setCustomBigContentView(expand_notification)
                .setCustomContentView(collap_notification)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(resources.getColor(R.color.background_fab))
                .setColorized(true)
                .setContentIntent(contentIntent)
                .setStyle(
                        androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle()
                                .setMediaSession(mediaSession?.sessionToken)
                )
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, notification)
        }

    }

    fun creatCollapNotification(song: Song): RemoteViews? {
        collap_notification =
                RemoteViews(packageName, R.layout.collap_custom_layout_notification)
        collap_notification?.setTextViewText(R.id.tv_songname_collap_noti, song.songName)
        collap_notification?.setTextViewText(R.id.tv_artist_collap_noti, song.artistName)

        if (mediaPlayer!!.isPlaying) {
            collap_notification?.setImageViewResource(
                    R.id.img_play_collap_notification,
                    R.drawable.ic_play_arrow_white_24dp
            )

        } else {
            collap_notification?.setImageViewResource(
                    R.id.img_play_collap_notification,
                    R.drawable.ic_pause_white_24dp
            )
        }
        collap_notification?.setOnClickPendingIntent(
                R.id.img_previous_collap_notification, createPendingIntent(
                PlaybackType.PREVIOUS.type
        )
        )
        collap_notification?.setOnClickPendingIntent(
                R.id.img_play_collap_notification, createPendingIntent(
                PlaybackType.PLAY.type
        )
        )
        collap_notification?.setOnClickPendingIntent(
                R.id.img_next_collap_notification, createPendingIntent(
                PlaybackType.NEXT.type
        )
        )

        return collap_notification
    }

    fun creatExpandNotification(song: Song): RemoteViews? {
        expand_notification = RemoteViews(packageName, R.layout.layout_notification_custom)
        expand_notification?.setTextViewText(R.id.tv_songname_notification, song.songName)
        expand_notification?.setTextViewText(R.id.tv_artist_notification, song.artistName)
        if (mediaPlayer!!.isPlaying) {
            expand_notification?.setImageViewResource(
                    R.id.img_play_notification,
                    R.drawable.ic_play_arrow_white_24dp
            )

        } else {
            expand_notification?.setImageViewResource(
                    R.id.img_play_notification,
                    R.drawable.ic_pause_white_24dp
            )
        }
        expand_notification?.setOnClickPendingIntent(
                R.id.img_previous_notification, createPendingIntent(
                PlaybackType.PREVIOUS.type
        )
        )
        expand_notification?.setOnClickPendingIntent(
                R.id.img_play_notification, createPendingIntent(
                PlaybackType.PLAY.type
        )
        )
        expand_notification?.setOnClickPendingIntent(
                R.id.img_next_notification, createPendingIntent(
                PlaybackType.NEXT.type
        )
        )
        return expand_notification

    }

    fun createPendingIntent(actionType: Int): PendingIntent {
        val intent = Intent()

        when (actionType) {
            PlaybackType.PREVIOUS.type -> {
                intent.action = ACTION_PREVIOUS
                intent.putExtra(EXTRA_ACTION_TYPE, actionType)
                return PendingIntent.getBroadcast(this, 0, intent, 0)
            }

            PlaybackType.PLAY.type -> {
                intent.action = ACTION_PLAY
                intent.putExtra(EXTRA_ACTION_TYPE, actionType)
                return PendingIntent.getBroadcast(this, 0, intent, 0)
            }

            PlaybackType.NEXT.type -> {
                intent.action = ACTION_NEXT
                intent.putExtra(EXTRA_ACTION_TYPE, actionType)
                return PendingIntent.getBroadcast(this, 0, intent, 0)
            }
        }
        return PendingIntent.getBroadcast(this, 0, intent, 0)
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
        createCustomNotification()
//        createNotification(PlaybackType.PLAY.type)
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
            updateNoti()
            mediaPlayer?.pause()
        } else {

            if (songPos < songList.size - 1) {
                updateNoti()
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

    fun register() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_NEXT)
        intentFilter.addAction(ACTION_PREVIOUS)
        intentFilter.addAction(ACTION_PLAY)
        registerReceiver(receiver, intentFilter)
    }

    fun unregisterNotificationButtonClick() {
        unregisterReceiver(receiver)
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            when (intent?.action) {
                ACTION_PLAY -> {

                    actionBtnPlay()
                }

                ACTION_PREVIOUS -> {
                    actionPrevious()
                }

                ACTION_NEXT -> {
                    actionNext()
                }

            }
        }
    }

    fun updateNoti() {
        creatCollapNotification(songList[songPos])
        creatExpandNotification(songList[songPos])
        createCustomNotification()
    }

}
