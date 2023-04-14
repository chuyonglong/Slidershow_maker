package com.acatapps.videomaker.models

class ThemeDataModel(val themeData: ThemeData) {

    val name = themeData.themeName
    val videoPath = themeData.themeVideoFilePath
    var selected = false

}