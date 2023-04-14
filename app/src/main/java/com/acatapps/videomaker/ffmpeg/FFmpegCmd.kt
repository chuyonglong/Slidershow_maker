package com.acatapps.videomaker.ffmpeg

import com.acatapps.videomaker.utils.Logger


object FFmpegCmd {

    fun trimAudio(input: String, startTime: Long, endTime: Long, outPut: String): Array<String> {
        return arrayOf(
            "-y",
            "-i", input, "-ss",
            "${startTime / 1000}",
            "-t",
            "${(endTime - startTime) / 1000}", "-vn", "-c:a", "copy", outPut
        )
    }
    fun trimAudio2(input: String, startTime: Long, endTime: Long, outPut: String): Array<String> {
        return arrayOf(
            "-y",
            "-i", input, "-ss",
            "${startTime / 1000}",
            "-t",
            "${(endTime - startTime) / 1000}", "-vn", outPut
        )
    }
    fun mergeAudioToVideo(audioPath: String , videoPath: String , outPut: String, volume:Float=1f ): Array<String>{
        return arrayOf("-y","-i" , videoPath , "-stream_loop" , "-1" , "-i" , audioPath ,"-filter_complex" , "[1:a]volume=${volume}" ,"-c:v" , "copy" , "-c:a" , "aac" , "-strict" , "experimental" ,"-q:a" , "330", "-shortest" , outPut)
    }

    fun cutVideo(
        input: String,
        startTime: Double,
        endTime: Double,
        outPut: String
    ): Array<String> {
        return arrayOf(
            "-y", "-i", input, "-ss", "${startTime / 1000}", "-t",
            "${(endTime - startTime) / 1000}", "-c", "copy", outPut
        )
    }
    fun cutVideo3(
        input: String,
        startTime: Double,
        endTime: Double,
        outPut: String
    ): Array<String> {
        return arrayOf(
            "-y", "-i", input, "-ss", "${startTime / 1000}", "-t",
            "${(endTime - startTime) / 1000}", "-c:a", "copy", "-q:v","8", outPut
        )
    }
    fun cutVideo2(
        input: String,
        startTime: String,//00:00 <- format start in 0s
        duration: String, //00:05 <- format = 5s
        outPut: String
    ): Array<String> {
        return arrayOf(
            "-ss",
            startTime,
            "-t",
            duration,
            "-accurate_seek",
            "-i",
            input,
            "-c",
            "copy",
            "-avoid_negative_ts",
            "1",
            outPut
        )
    }

    fun mergeAudio(outPath:String, inPathList:ArrayList<String>) :Array<String>{
        val outCmd = ArrayList<String>()
        outCmd.add("-y")
        for(inPath in inPathList) {
            outCmd.add("-i")
            outCmd.add(inPath)
        }
        outCmd.add("-filter_complex")
        outCmd.add("amix=inputs=${inPathList.size}:duration=longest")
        outCmd.add(outPath)
        outCmd.toArray()
        var final = ""
        Logger.e("final size = ${outCmd.size}")
        for(item in outCmd) {
            final += " $item"
        }
        Logger.e("$final")
        return outCmd.toTypedArray()
    }

}