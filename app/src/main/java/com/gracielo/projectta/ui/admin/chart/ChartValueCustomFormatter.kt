package com.gracielo.projectta.ui.admin.chart

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.roundToInt


class ChartValueCustomFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        if(value==0.0f){return ""}
        else {
            return super.getFormattedValue(value,axis)
        }
    }

    override fun getFormattedValue(
        value: Float,
        entry: Entry?,
        dataSetIndex: Int,
        viewPortHandler: ViewPortHandler?
    ): String {
        if(value==0.0f){return ""}
        else {
            return super.getFormattedValue(value, entry, dataSetIndex, viewPortHandler)
        }
    }

    override fun getFormattedValue(value: Float): String {
        if(value==0.0f){return ""}
        else {
            val f =1.3f
            return super.getFormattedValue(value).toFloat().roundToInt().toString()
        }
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return super.getAxisLabel(value, axis)
    }

    override fun getBarLabel(barEntry: BarEntry?): String {
        return super.getBarLabel(barEntry)
    }

    override fun getBarStackedLabel(value: Float, stackedEntry: BarEntry?): String {
        return super.getBarStackedLabel(value, stackedEntry)
    }

    override fun getPointLabel(entry: Entry?): String {
        return super.getPointLabel(entry)
    }

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        return super.getPieLabel(value, pieEntry)
    }

    override fun getRadarLabel(radarEntry: RadarEntry?): String {
        return super.getRadarLabel(radarEntry)
    }

    override fun getBubbleLabel(bubbleEntry: BubbleEntry?): String {
        return super.getBubbleLabel(bubbleEntry)
    }

    override fun getCandleLabel(candleEntry: CandleEntry?): String {
        return super.getCandleLabel(candleEntry)
    }
}