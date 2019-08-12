package minh.quy.musicplayer.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import minh.quy.musicplayer.Utils.RequestPermission
import minh.quy.musicplayer.database.MusicDatabase
import minh.quy.musicplayer.model.Album
import minh.quy.musicplayer.model.Artist
import minh.quy.musicplayer.model.Song
import minh.quy.musicplayer.service.PlayMusicService
import minh.quy.musicplayer.service.PlayMusicService.MusicBinder
import java.util.*


abstract class BaseActivity : AppCompatActivity() {

    var musicDatabase: MusicDatabase? = null
    var songlist: MutableList<Song> = arrayListOf()
    var albumList: MutableList<Album> = arrayListOf()
    var artistList: MutableList<Artist> = arrayListOf()

    var musicService: PlayMusicService? = null
    var playIntent: Intent? = null
    var musicBound = false

    private val musicConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MusicBinder
            //get service
            musicService = binder.service
            //pass list
            musicService?.setSongs(songlist)
            musicBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            musicBound = false
        }
    }


    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        musicDatabase = MusicDatabase.getInstanceDatabase(this)
        RequestPermission.requestPermision(this)
        songlist = scanDeviceForMp3Files()
        getAllAlbum()
        getAllArtist()
        startService()
    }

    private fun scanDeviceForMp3Files(): ArrayList<Song> {
        val songs = arrayListOf<Song>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATE_ADDED

        )
        val sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC"
        var cursor: Cursor? = null
        try {
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            cursor = contentResolver.query(uri, projection, selection, null, sortOrder)
            if (cursor != null) {
                cursor.moveToFirst()

                while (!cursor.isAfterLast) {
                    val title = cursor.getString(0)
                    val artist = cursor.getString(1)
                    val artistId = cursor.getString(2).toLong()
                    val path = cursor.getString(3)
                    val songDuration = cursor.getString(5).toInt()
                    val albumName = cursor.getString(6)
                    val albumId = cursor.getString(7).toLong()
                    val dateAdded = cursor.getString(8).toLong()
                    cursor.moveToNext()
                    if (path != null && path.endsWith(".mp3")) {
                        val song = Song(title, artist, artistId, albumId, albumName, songDuration, dateAdded, path)
                        songs.add(song)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("minhnhnh", e.toString())
        } finally {
            cursor?.close()
        }
        return songs
    }

    private fun getAllArtist() {
        for (i in 0 until songlist.size) {
            val song = songlist[i]
            var exist = false
            if (artistList.size == 0) {
                artistList.add(Artist(song.artistId, song.artistName, 0))
            }
            artist@ for (j in 0 until artistList.size) {
                val artist = artistList[j]
                if (artist.artistId == song.artistId) {
                    exist = true
                    artist.songs.add(song)
                    artist.songCount += 1
                    Log.d("MinhNQ", "exist + " + song.artistId)
                    break@artist
                } else {
                    exist = false
                }
            }
            Log.d("MinhNQ", " not exist + " + song.artistId)
            if (!exist) {
                val songs: MutableList<Song> = arrayListOf()
                songs.add(song)
                artistList.add(Artist(song.artistId, song.artistName, 1, songs))
            }
        }
        Log.d("MinhNQ", " size + " + artistList.size)
    }

    private fun getAllAlbum() {
        for (i in 0 until songlist.size) {
            val song = songlist[i]
            var exist = false
            if (albumList.size == 0) {
                albumList.add(Album(song.albumId, song.albumName, 0))
            }
            album@ for (j in 0 until albumList.size) {
                val album = albumList[j]
                if (album.albummId == song.albumId) {
                    exist = true
                    album.songs.add(song)
                    album.songCount += 1
                    Log.d("MinhNQ", "exist + " + song.albumId)
                    break@album
                } else {
                    exist = false
                }
            }
            Log.d("MinhNQ", " not exist + " + song.albumId)
            if (!exist) {
                val songs: MutableList<Song> = arrayListOf()
                songs.add(song)
                albumList.add(Album(song.albumId, song.albumName, 1, songs))
            }

        }
        Log.d("MinhNQ", " size + " + albumList.size)
    }

    fun startService() {
        if (playIntent != null) {
            playIntent = Intent(this, PlayMusicService::class.java)
        }
        bindService(playIntent,musicConnection, Context.BIND_AUTO_CREATE)
        startService(playIntent)
    }


}