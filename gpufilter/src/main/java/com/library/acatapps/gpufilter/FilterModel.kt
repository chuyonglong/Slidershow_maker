package com.library.gpu.filter

import android.content.Context
import com.library.acatapps.gpufilter.FilterListAdapter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

class FilterModel {

    val gpuImageFilter: GPUImageFilter
    val title: String
    var selected = false
    var filterType = GPUFilterUtils.FilterType.NONE
    val context: Context
    val filterAdjuster: FilterListAdapter.FilterAdjuster
    var currentPercent = 100

    val originnalFilter: GPUImageFilter

    constructor(title: String, filterType: GPUFilterUtils.FilterType, context: Context) {
        this.title = title
        this.filterType = filterType
        this.context = context
        originnalFilter = GPUFilterUtils.initFilter(filterType, context)
        this.gpuImageFilter = GPUFilterUtils.initFilter(filterType, context)
        this.filterAdjuster = FilterListAdapter.FilterAdjuster(gpuImageFilter)
        this.filterAdjuster.adjust(currentPercent)
    }

    constructor(title: String, filter: GPUImageFilter, context: Context) {
        this.title = title
        this.context = context
        originnalFilter = filter
        gpuImageFilter = filter
        filterAdjuster = FilterListAdapter.FilterAdjuster(gpuImageFilter)
        filterAdjuster.adjust(currentPercent)
    }

}