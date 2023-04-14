package com.acatapps.videomaker.transition

import com.acatapps.videomaker.R
import java.io.Serializable

open class GSTransition (val transitionCodeId:Int= R.raw.none_transition_code, val transitionName:String="None" , var lock: Boolean = false) :Serializable {

}