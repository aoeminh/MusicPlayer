package minh.quy.musicplayer.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fast_scroller.*
import kotlinx.android.synthetic.main.fragment_songs.*
import kotlinx.android.synthetic.main.popup_add_to_playlist.view.*
import layout.HomeFragment
import minh.quy.musicplayer.Constant
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.RequestPermission
import minh.quy.musicplayer.action.IOptionListener
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.AddToPlaylistAdapter
import minh.quy.musicplayer.adapter.SongFragmentAdapter
import minh.quy.musicplayer.funtiontoolbar.FunctionToolbarPlaylist
import minh.quy.musicplayer.model.Album
import minh.quy.musicplayer.model.PlayListSong
import minh.quy.musicplayer.model.Song
import minh.quy.musicplayer.service.PlayMusicService
import java.io.File
import java.util.jar.Manifest

class SongsFragment : BaseFragment(), OnItemCommonClick, IOptionListener {

    var songlist: MutableList<Song> = arrayListOf()
    var adapterSong: SongFragmentAdapter? = null
    var receiver: BroadcastReceiver? = null
    val WRITE_SETTING_PERMISSION = 99
    var itemClickPosition = 0
    var addAdapter: AddToPlaylistAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songlist.addAll(mainActivity.songlist)
        registUpdateView()
//        updateSongSelected(mainActivity.musicService?.currenSongId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_songs, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MinhNQ", songlist.size.toString())
        songlist.clear()
        songlist.addAll(mainActivity.songlist)
        initRecyclerview()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregistUpdateSongSelected()
    }

    override fun onItemClick(postion: Int) {
        if (!songlist[postion].isSelected) {
            playSong(postion)
        }
        gotoPlaySongFragment(postion)
        setSongQueue()
        setSongSelected(songlist[postion].songId!!)
//        updateSongSelected((songlist[postion].songId!!))
    }

    override fun onOptionClick(position: Int, view: View) {
        showPopUpOption(position, view)
    }

    fun showPopUpOption(position: Int, view: View) {

        itemClickPosition = position
        val popUpMenu = PopupMenu(context!!, view)
        popUpMenu.menuInflater.inflate(R.menu.menu_item_song, popUpMenu.menu)


        popUpMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {

                R.id.item_play_next_song -> {
                    playNext(songlist[position])
                }

                R.id.item_add_queue_playlist -> {
                    addToQueue(songlist[position])
                }

                R.id.item_add_to_playlist -> {
                    optionAddToPlaylist(position)
                }

                R.id.item_go_to_album -> {
                    gotoSongFragment(position, Constant.TypeListsong.ALBUM.type)
                }

                R.id.item_go_to_artist -> {
                    gotoSongFragment(position, Constant.TypeListsong.ARTIST.type)
                }

                R.id.item_set_ringtone -> {
                    if (isHasPermission()) {
                        setRingtone(songlist[position])
                    } else {
                        requestWriteSettingPermission()
                    }
                }
            }
            true
        }

        popUpMenu.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == WRITE_SETTING_PERMISSION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setRingtone(songlist[itemClickPosition])
            } else {
                showDialogSetting()
            }
        }

    }

    override fun showDialogSetting() {
        isNeedRequest = false
        var builder = AlertDialog.Builder(activity)
        builder.apply {
            setCancelable(false)
            setMessage(R.string.permission_necessary)
            setPositiveButton(R.string.ok) { dialog, which ->
                run {
                    requestWriteSettingPermission()
                }
            }
            setNegativeButton(R.string.cancel) { dialogInterface, i ->
                run {
                    Toast.makeText(context!!, "You don't have permission", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun optionAddToPlaylist(position: Int) {
        val builder = AlertDialog.Builder(context!!)
        val dialogView = layoutInflater.inflate(R.layout.popup_add_to_playlist, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        dialogView.rv_playlists_add_to_playlis.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addAdapter = AddToPlaylistAdapter(context, mainActivity.playlists)
            addAdapter?.setItemClick(object : OnItemCommonClick{
                override fun onItemClick(postion: Int) {
                    val playlistId = mainActivity.playlists[postion].id!!
                    val songId = songlist[position].songId!!

                    //if song exist
                    val listPlaylistSong =
                        mainActivity.musicDatabase?.getPlayListSongDAO()?.getAllSongInPlaylist(playlistId)
                    listPlaylistSong?.forEach {
                        if (songId.equals(it.songId)) {
                            Toast.makeText(context, R.string.song_exists, Toast.LENGTH_SHORT).show()
                            alertDialog?.dismiss()
                            return
                        }
                    }

                    val playlistSong = PlayListSong(
                        playlistId,
                        songId
                    )

                    val result =
                        mainActivity.musicDatabase?.getPlayListSongDAO()?.insertSongIntoPlayList(playlistSong)
                    Toast.makeText(context, R.string.added_to_playlist, Toast.LENGTH_SHORT).show()
                    alertDialog?.dismiss()
                    Log.d("minh", result.toString())
                    val playListSong = PlayListSong(mainActivity.playlists[postion].id!!,songlist[position].songId!!)
                    mainActivity.musicDatabase?.getPlayListSongDAO()?.insertSongIntoPlayList(playListSong)
                }
            })
            adapter = addAdapter
        }

        alertDialog?.show()
    }

    fun gotoSongFragment(position: Int, type: Int) {
        val fragment: ListSongFragment
        if (type == Constant.TypeListsong.ALBUM.type) {
            fragment =
                ListSongFragment.newInstance(
                    songlist[position].albumId,
                    songlist[position].albumName!!,
                    Constant.TypeListsong.ALBUM.type
                )
        } else {
            fragment =
                ListSongFragment.newInstance(
                    songlist[position].artistName!!,
                    songlist[position].artistName!!,
                    Constant.TypeListsong.ARTIST.type
                )
        }

        val transaction = mainActivity.fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main, fragment, null)
        transaction.addToBackStack(null)
        transaction.commit()
    }

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
        var cursor = context!!.getContentResolver().query(
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
                Toast.makeText(context, "Set as Ringtone Successfully.", Toast.LENGTH_SHORT).show();
            } catch (t: Throwable) {
                t.printStackTrace();
            }
            cursor.close();
        }

    }

    fun requestWriteSettingPermission() {
        if (!isHasPermission()) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.setData(Uri.parse("package:" + context!!.getPackageName()))
                startActivityForResult(intent, WRITE_SETTING_PERMISSION)
            } else {
                requestPermissions(
                    arrayOf(android.Manifest.permission.WRITE_SETTINGS),
                    WRITE_SETTING_PERMISSION
                )
            }

        }

    }

    fun isHasPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.System.canWrite(context!!)
        } else {
            return ActivityCompat.checkSelfPermission(
                context!!,
                android.Manifest.permission.WRITE_SETTINGS
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun playNext(song: Song) {
        mainActivity.musicService?.songList?.add(mainActivity.musicService?.songPos!! + 1, song)
    }

    fun addToQueue(song: Song) {
        mainActivity.musicService?.songList?.add(song)
    }

    fun setSongSelected(songId: String) {
        for (i in 0 until songlist.size) {
            songlist[i].isSelected = songlist[i].songId.equals(songId)
        }
        adapterSong?.notifyDataSetChanged()

    }

    fun setSongQueue() {
        mainActivity.musicService?.songList?.clear()
        mainActivity.musicService?.songList?.addAll(songlist)
    }

    fun gotoPlaySongFragment(postion: Int) {
        val fragment = PlaySongFragment.newInstance(postion)
        val transaction = mainActivity.fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main, fragment, null)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun playSong(postion: Int) {
        mainActivity.musicService?.setSongs(songlist)
        mainActivity.musicService?.setSongPosition(postion)
        mainActivity.musicService?.playMusic()
    }

    fun initRecyclerview() {
        rv_song_fragment.apply {
            layoutManager = LinearLayoutManager(contextBase, RecyclerView.VERTICAL, false)
            adapterSong = SongFragmentAdapter(contextBase!!, songlist)
            adapterSong?.setOnItemClick(this@SongsFragment)
            adapterSong?.setOptionListener(this@SongsFragment)
            adapter = adapterSong
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    hideScrollBar()
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    showScrollBar()
                }
            })
        }
        fast_scroller?.setRecyclerView(rv_song_fragment)
    }

    fun hideScrollBar() {
        Handler().postDelayed({
            fast_scroller?.visibility = View.GONE
        }, Constant.TIME_HIDE_SCROLL_BAR)
    }

    fun showScrollBar() {
        fast_scroller?.visibility = View.VISIBLE
    }

    fun registUpdateView() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    PlayMusicService.ACTION_UPDATE_VIEW -> {
                        setSongSelected(
                            intent.getStringExtra(
                                PlayMusicService.EXTRA_SONG_ID
                            )!!
                        )
                    }
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(PlayMusicService.ACTION_UPDATE_VIEW)
        LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver!!, intentFilter)
    }

    fun unregistUpdateSongSelected() {
        receiver?.let {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver!!)
        }
    }


    fun updateSongSelected(songId: String) {
        val intent = Intent(PlaySongFragment.ACTION_UPDATE_SONG)
        intent.putExtra(PlaySongFragment.EXTRA_SONG_ID, songId)
        LocalBroadcastManager.getInstance(mainActivity.applicationContext).sendBroadcast(intent)

    }

}