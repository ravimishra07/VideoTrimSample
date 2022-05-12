package com.ravi.videotrimsample.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ravi.videotrimsample.R
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import com.ravi.videotrimsample.adapter.RangeSeekbarPagerAdapter
import com.ravi.videotrimsample.fragments.RangeSeekbar
import com.ravi.videotrimsample.fragments.Seekbar
import java.util.ArrayList

/**
 * Created by owais.ali on 6/19/2016.
 */
class RangeSlider : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.range_slider)
        init()
    }

    fun <T> getView(resId: Int): T {
        return findViewById<View>(resId) as T
    }

    private fun init() {
        val tabLayout = getView<TabLayout>(R.id.tlSeekbar)
        val viewPager = getView<ViewPager>(R.id.vpSeekbar)

        // set fragments list
        val fragments: MutableList<Fragment> = ArrayList()
        fragments.add(Seekbar())
        fragments.add(RangeSeekbar())

        // set tabs title
        val tabTitles: MutableList<String> = ArrayList()
        tabTitles.add("Seekbar")
        tabTitles.add("Range Seekbar")


        // create view pager adapter
        val adapter = RangeSeekbarPagerAdapter(supportFragmentManager, fragments, tabTitles)

        // set adapter to pager
        viewPager.adapter = adapter

        // set view pager to tab layout
        tabLayout.setupWithViewPager(viewPager)
    }
}