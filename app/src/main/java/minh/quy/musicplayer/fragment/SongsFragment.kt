package minh.quy.musicplayer.fragment

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_songs.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.SongFragmentAdapter
import minh.quy.musicplayer.model.Song
import java.util.*

class SongsFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    var contextSong: Context? = null
    var songlist: MutableList<Song> = arrayListOf()
    var adapterSong: SongFragmentAdapter? =null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        contextSong = context
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songlist = scanDeviceForMp3Files()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_songs, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MinhNQ", songlist.size.toString())
        initRecyclerview()

    }

    fun initRecyclerview() {
        rv_song_fragment.apply {
            layoutManager = LinearLayoutManager(contextSong, RecyclerView.VERTICAL, false)
            adapterSong = SongFragmentAdapter(contextSong!!)
            adapterSong?.setlistSong(songlist)
            adapter = adapterSong
        }
    }

    private fun scanDeviceForMp3Files(): ArrayList<Song> {
        val songs = ArrayList<Song>()
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
            cursor = contextSong?.getContentResolver()?.query(uri, projection, selection, null, sortOrder)
            if (cursor != null) {
                cursor.moveToFirst()

                while (!cursor.isAfterLast) {
                    val title = cursor.getString(0)
                    val artist = cursor.getString(1)
                    val artistId = cursor.getString(2).toLong()
                    val path = cursor.getString(3)
                    val displayName = cursor.getString(4)
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

    override fun onResume() {
        super.onResume()
        scanDeviceForMp3Files()
    }
}