package com.ravi.videotrimsample.fragments

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import com.ravi.videotrimsample.R
import android.view.InflateException
import android.view.View
import com.ravi.videotrimsample.customseekar.CrystalSeekbar
import android.widget.TextView
import com.ravi.videotrimsample.interfaces.OnSeekbarChangeListener
import com.ravi.videotrimsample.interfaces.OnSeekbarFinalValueListener
import com.ravi.videotrimsample.customseekar.CrystalRangeSeekbar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.ravi.videotrimsample.customseekar.MySeekbar


class Seekbar : Fragment() {
    protected var rootView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView != null) {
            val parent = rootView!!.parent as ViewGroup
            parent.removeView(rootView)
        }
        try {
            rootView = inflater.inflate(R.layout.seekbar, container, false)
        } catch (e: InflateException) {
            e.printStackTrace()
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    fun init() {
        setRangeSeekbar1()
        setRangeSeekbar2()
        setRangeSeekbar3()
        setRangeSeekbar4()
        setRangeSeekbar5()
        setRangeSeekbar6()
        setRangeSeekbar7()
        setRangeSeekbar8()
    }

    private fun setRangeSeekbar1() {

        // get seekbar from view
        val seekbar = rootView!!.findViewById<View>(R.id.rangeSeekbar1) as CrystalSeekbar

        // get min and max text view
        val tvMin = rootView!!.findViewById<View>(R.id.textMin1) as TextView

        // set listener
        seekbar.setOnSeekbarChangeListener(object : OnSeekbarChangeListener {
            override fun valueChanged(minValue: Number?) {
                tvMin.text = minValue.toString()
            }
        })

        // set final value listener
        seekbar.setOnSeekbarFinalValueListener(object : OnSeekbarFinalValueListener {
            override fun finalValue(value: Number?) {
                Log.d("CRS=>", value.toString())
            }
        })
    }

    private fun setRangeSeekbar2() {

        // get seekbar from view
//        final BubbleThumbSeekbar rangeSeekbar = (BubbleThumbSeekbar) rootView.findViewById(R.id.rangeSeekbar2);
//
//        // get min and max text view
//        final TextView tvMin = (TextView) rootView.findViewById(R.id.textMin2);
//
//        // set listener
//        rangeSeekbar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
//            @Override
//            public void valueChanged(Number minValue) {
//                tvMin.setText(String.valueOf(minValue));
//            }
//        });
    }

    private fun setRangeSeekbar3() {
//
//        // get seekbar from view
//        final BubbleThumbSeekbar rangeSeekbar = (BubbleThumbSeekbar) rootView.findViewById(R.id.rangeSeekbar3);
//
//        // get min and max text view
//        final TextView tvMin = (TextView) rootView.findViewById(R.id.textMin3);
//        final TextView tvMax = (TextView) rootView.findViewById(R.id.textMax3);
//
//        // set listener
//        rangeSeekbar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
//            @Override
//            public void valueChanged(Number minValue) {
//                tvMin.setText(String.valueOf(minValue));
//            }
//        });
    }

    private fun setRangeSeekbar4() {

        // get seekbar from view
        val rangeSeekbar = rootView!!.findViewById<View>(R.id.rangeSeekbar4) as CrystalSeekbar

        // get min and max text view
        val tvMin = rootView!!.findViewById<View>(R.id.textMin4) as TextView
        val tvMax = rootView!!.findViewById<View>(R.id.textMax4) as TextView

        // set properties
        rangeSeekbar
            .setCornerRadius(10f)
            .setBarColor(Color.parseColor("#93F9B5"))
            .setBarHighlightColor(Color.parseColor("#16E059"))
            .setMinValue(400f)
            .setMaxValue(800f)
            .setSteps(100f)
            .setThumbDrawable(R.drawable.thumb_android)
            .setThumbHighlightDrawable(R.drawable.thumb_android_pressed)
            .setDataType(CrystalRangeSeekbar.DataType.INTEGER)
            .apply()

        // set listener
        rangeSeekbar.setOnSeekbarChangeListener(object : OnSeekbarChangeListener {
            override fun valueChanged(minValue: Number?) {
                tvMin.text = minValue.toString()
            }
        })
    }

    private fun setRangeSeekbar5() {

        // get seekbar from view
        val rangeSeekbar = CrystalSeekbar(requireActivity())

        // get min and max text view
        val tvMin = rootView!!.findViewById<View>(R.id.textMin5) as TextView
        val tvMax = rootView!!.findViewById<View>(R.id.textMax5) as TextView

        // set listener
        rangeSeekbar.setOnSeekbarChangeListener(object : OnSeekbarChangeListener {
            override fun valueChanged(minValue: Number?) {
                tvMin.text = minValue.toString()
            }
        })

        // get range seekbar container
        val container = rootView!!.findViewById<View>(R.id.contRangeSeekbar5) as RelativeLayout
        container.addView(rangeSeekbar)
    }

    private fun setRangeSeekbar6() {

        // get seekbar from view
        val rangeSeekbar = rootView!!.findViewById<View>(R.id.rangeSeekbar6) as MySeekbar

        // get min and max text view
        val tvMin = rootView!!.findViewById<View>(R.id.textMin6) as TextView
        val tvMax = rootView!!.findViewById<View>(R.id.textMax6) as TextView

        // set listener
        rangeSeekbar.setOnSeekbarChangeListener(object : OnSeekbarChangeListener {
            override fun valueChanged(minValue: Number?) {
                tvMin.text = minValue.toString()
            }
        })
    }

    private fun setRangeSeekbar7() {

        // get seekbar from view
        val rangeSeekbar = rootView!!.findViewById<View>(R.id.rangeSeekbar7) as CrystalSeekbar

        // get min and max text view
        val tvMin = rootView!!.findViewById<View>(R.id.textMin7) as TextView
        val tvMax = rootView!!.findViewById<View>(R.id.textMax7) as TextView

        // set listener
        rangeSeekbar.setOnSeekbarChangeListener(object : OnSeekbarChangeListener {
            override fun valueChanged(minValue: Number?) {
                tvMin.text = minValue.toString()
            }
        })
    }

    private fun setRangeSeekbar8() {

        // get seekbar from view
        val rangeSeekbar = rootView!!.findViewById<View>(R.id.rangeSeekbar8) as CrystalSeekbar

        // get min and max text view
        val tvMin = rootView!!.findViewById<View>(R.id.textMin8) as TextView
        val tvMax = rootView!!.findViewById<View>(R.id.textMax8) as TextView

        // change position left to right
        rangeSeekbar.setPosition(CrystalSeekbar.Position.RIGHT).apply()

        // set listener
        rangeSeekbar.setOnSeekbarChangeListener(object : OnSeekbarChangeListener {
            override fun valueChanged(minValue: Number?) {
                tvMin.text = minValue.toString()
            }
        })
    }
}