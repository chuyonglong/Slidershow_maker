package com.daasuu.gpuv.camerarecorder.capture;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;



public abstract class MediaEncoder implements Runnable {
    private final String TAG = getClass().getSimpleName();

    protected static final int TIMEOUT_USEC = 10000;

    public interface MediaEncoderListener {
        void onPrepared(MediaEncoder encoder);

        void onStopped(MediaEncoder encoder);

        void onExit();
    }

    protected final Object sync = new Object();

    protected volatile boolean isCapturing;

    protected int requestDrain;

    protected volatile boolean requestStop;

    protected boolean isEOS;

    protected boolean muxerStarted;

    protected int trackIndex;

    protected MediaCodec mediaCodec;

    protected final WeakReference<MediaMuxerCaptureWrapper> weakMuxer;

    private MediaCodec.BufferInfo bufferInfo;

    protected final MediaEncoderListener listener;

    MediaEncoder(final MediaMuxerCaptureWrapper muxer, final MediaEncoderListener listener) {
        if (listener == null) throw new NullPointerException("MediaEncoderListener is null");
        if (muxer == null) throw new NullPointerException("MediaMuxerCaptureWrapper is null");
        weakMuxer = new WeakReference<MediaMuxerCaptureWrapper>(muxer);
        muxer.addEncoder(this);
        this.listener = listener;
        synchronized (sync) {
            bufferInfo = new MediaCodec.BufferInfo();
            new Thread(this, getClass().getSimpleName()).start();
            try {
                sync.wait();
            } catch (final InterruptedException e) {
            }
        }
    }


    public boolean frameAvailableSoon() {
        synchronized (sync) {
            if (!isCapturing || requestStop) {
                return false;
            }
            requestDrain++;
            sync.notifyAll();
        }
        return true;
    }


    @Override
    public void run() {
        synchronized (sync) {
            requestStop = false;
            requestDrain = 0;
            sync.notify();
        }
        final boolean isRunning = true;
        boolean localRequestStop;
        boolean localRequestDrain;
        while (isRunning) {
            synchronized (sync) {
                localRequestStop = requestStop;
                localRequestDrain = (requestDrain > 0);
                if (localRequestDrain)
                    requestDrain--;
            }
            if (localRequestStop) {
                drain();
                signalEndOfInputStream();
                drain();
                release();
                break;
            }
            if (localRequestDrain) {
                drain();
            } else {
                synchronized (sync) {
                    try {
                        sync.wait();
                    } catch (final InterruptedException e) {
                        break;
                    }
                }
            }
        }
        Log.d(TAG, "Encoder thread exiting");
        synchronized (sync) {
            requestStop = true;
            isCapturing = false;
        }
        listener.onExit();
    }


    abstract void prepare() throws IOException;

    void startRecording() {
        Log.v(TAG, "startRecording");
        synchronized (sync) {
            isCapturing = true;
            requestStop = false;
            sync.notifyAll();
        }
    }


    void stopRecording() {
        Log.v(TAG, "stopRecording");
        synchronized (sync) {
            if (!isCapturing || requestStop) {
                return;
            }
            requestStop = true;
            sync.notifyAll();
        }
    }

    protected void release() {
        Log.d(TAG, "release:");
        try {
            listener.onStopped(this);
        } catch (final Exception e) {
            Log.e(TAG, "failed onStopped", e);
        }
        isCapturing = false;
        if (mediaCodec != null) {
            try {
                mediaCodec.stop();
                mediaCodec.release();
                mediaCodec = null;
            } catch (final Exception e) {
                Log.e(TAG, "failed releasing MediaCodec", e);
            }
        }
        if (muxerStarted) {
            final MediaMuxerCaptureWrapper muxer = weakMuxer != null ? weakMuxer.get() : null;
            if (muxer != null) {
                try {
                    muxer.stop();
                } catch (final Exception e) {
                    Log.e(TAG, "failed stopping muxer", e);
                }
            }
        }
        bufferInfo = null;
    }

    protected void signalEndOfInputStream() {
        Log.d(TAG, "sending EOS to encoder");
        encode(null, 0, getPTSUs());
    }


    protected void encode(final ByteBuffer buffer, final int length, final long presentationTimeUs) {
        if (!isCapturing) return;
        while (isCapturing) {
            final int inputBufferIndex = mediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
            if (inputBufferIndex >= 0) {
                final ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
                inputBuffer.clear();
                if (buffer != null) {
                    inputBuffer.put(buffer);
                }

                if (length <= 0) {
                    isEOS = true;
                    Log.i(TAG, "send BUFFER_FLAG_END_OF_STREAM");
                    mediaCodec.queueInputBuffer(inputBufferIndex, 0, 0,
                            presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    break;
                } else {
                    mediaCodec.queueInputBuffer(inputBufferIndex, 0, length,
                            presentationTimeUs, 0);
                }
                break;
            }
        }
    }

    private void drain() {
        if (mediaCodec == null) return;
        int encoderStatus, count = 0;
        final MediaMuxerCaptureWrapper muxer = weakMuxer.get();
        if (muxer == null) {
            Log.w(TAG, "muxer is unexpectedly null");
            return;
        }
        LOOP:
        while (isCapturing) {
            encoderStatus = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!isEOS) {
                    if (++count > 5)
                        break LOOP;
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                Log.v(TAG, "INFO_OUTPUT_FORMAT_CHANGED");

                if (muxerStarted) {
                    throw new RuntimeException("format changed twice");
                }

                final MediaFormat format = mediaCodec.getOutputFormat();
                trackIndex = muxer.addTrack(format);
                muxerStarted = true;
                if (!muxer.start()) {
                    synchronized (muxer) {
                        while (!muxer.isStarted())
                            try {
                                muxer.wait(100);
                            } catch (final InterruptedException e) {
                                break LOOP;
                            }
                    }
                }
            } else if (encoderStatus < 0) {

                Log.w(TAG, "drain:unexpected result from encoder#dequeueOutputBuffer: " + encoderStatus);
            } else {
                final ByteBuffer encodedData = mediaCodec.getOutputBuffer(encoderStatus);
                if (encodedData == null) {

                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                }
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {

                    Log.d(TAG, "drain:BUFFER_FLAG_CODEC_CONFIG");
                    bufferInfo.size = 0;
                }

                if (bufferInfo.size != 0) {
                    count = 0;
                    if (!muxerStarted) {
                        throw new RuntimeException("drain:muxer hasn't started");
                    }
                    bufferInfo.presentationTimeUs = getPTSUs();
                    muxer.writeSampleData(trackIndex, encodedData, bufferInfo);
                    prevOutputPTSUs = bufferInfo.presentationTimeUs;
                }
                mediaCodec.releaseOutputBuffer(encoderStatus, false);
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {

                    isCapturing = false;
                    break;
                }
            }
        }
    }


    private long prevOutputPTSUs = 0;

    long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        if (result < prevOutputPTSUs)
            result = (prevOutputPTSUs - result) + result;
        return result;
    }
}

