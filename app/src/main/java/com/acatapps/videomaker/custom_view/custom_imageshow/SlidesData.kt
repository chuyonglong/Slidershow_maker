package com.acatapps.videomaker.custom_view.custom_imageshow

import java.io.Serializable


data class SlidesData(
    val id: Long,
    //幻灯片内容
    var slides: ArrayList<ImageSlideData>,
    //名称
    var slidesName: String,
    //创建时间
    val createTime: Long,
    //总时间
    var slideTime: Int,
    //第一张图片
    var firstPicturePath: String,

    var mImageList: ArrayList<String>
) : Serializable

