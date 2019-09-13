package minh.quy.musicplayer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class BottomNavigationAdapter(val fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    val listFragment: MutableList<Fragment> = arrayListOf()
    val listTitle: MutableList<String> = arrayListOf()

    override fun getItem(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getCount(): Int {
        return listFragment.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        listFragment.add(fragment)
        listTitle.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return listTitle[position]
    }
}