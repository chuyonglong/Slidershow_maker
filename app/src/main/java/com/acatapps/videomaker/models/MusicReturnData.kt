package com.acatapps.videomaker.models

import java.io.Serializable

class MusicReturnData(val audioFilePath:String,var outFilePath:String="",val startOffset:Int,val length:Int):Serializable