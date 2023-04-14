package com.daasuu.gpuv.camerarecorder;



public interface CameraRecordListener {

    void onGetFlashSupport(boolean flashSupport);

    void onRecordComplete();

    void onRecordStart();

    void onError(Exception exception);

    void onCameraThreadFinish();


    void onVideoFileReady();
}
