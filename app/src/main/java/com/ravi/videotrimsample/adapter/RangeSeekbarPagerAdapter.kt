package com.ravi.videotrimsample.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class RangeSeekbarPagerAdapter(
    fm: FragmentManager?,
    private val fragments: List<Fragment>,
    private val titles: List<String>
) : FragmentPagerAdapter(
    fm!!
) {
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}