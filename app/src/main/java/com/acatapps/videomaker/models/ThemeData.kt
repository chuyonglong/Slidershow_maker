package com.acatapps.videomaker.models

import java.io.Serializable

class ThemeData(val themeVideoFilePath:String="none", val themeType: ThemType = ThemType.NOT_REPEAT, val themeName:String="None") :Serializable{

    enum class ThemType {
        REPEAT,NOT_REPEAT
    }
}