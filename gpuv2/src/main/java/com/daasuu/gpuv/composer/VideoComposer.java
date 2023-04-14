
package com.daasuu.gpuv.composer;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.player.EffectData;
import com.daasuu.gpuv.player.StickerInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Logger;
class VideoComposer {
    private static final String TAG = "VideoComposer";
    private static final int DRAIN_STATE_NONE = 0;
    private static final int DRAIN_STATE_SHOULD_RETRY_IMMEDIATELY = 1;
    private static final int DRAIN_STATE_CONSUMED = 2;

    private final MediaExtractor mediaExtractor;
    private final int trackIndex;
    private final MediaFormat outputFormat;
    private final MuxRender muxRender;
    private final MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
    private MediaCodec decoder;
    private MediaCodec encoder;
    private ByteBuffer[] decoderInputBuffers;
    private ByteBuffer[] encoderOutputBuffers;
    private MediaFormat actualOutputFormat;
    private DecoderSurface decoderSurface;
    private EncoderSurface encoderSurface;
    private boolean isExtractorEOS;
    private boolean isDecoderEOS;
    private boolean isEncoderEOS;
    private boolean decoderStarted;
    private boolean encoderStarted;
    private long writtenPresentationTimeUs;
    private final int timeScale;

    VideoComposer(MediaExtractor mediaExtractor, int trackIndex,
                  MediaFormat outputFormat, MuxRender muxRender, int timeScale) {
        this.mediaExtractor = mediaExtractor;
        this.trackIndex = trackIndex;
        this.outputFormat = outputFormat;
        this.muxRender = muxRender;
        this.timeScale = timeScale;
    }


    void setUp(Context context,
               GlFilter filter,
               Rotation rotation,
               Size outputResolution,
               Size inputResolution,
               FillMode fillMode,
               FillModeCustomItem fillModeCustomItem,
               final boolean flipVertical,
               final boolean flipHorizontal,
               ArrayList<EffectData> listEffectData,
               ArrayList<StickerInfo> listStickerInfo) {
        mediaExtractor.selectTrack(trackIndex);
        try {
            encoder = MediaCodec.createEncoderByType(outputFormat.getString(MediaFormat.KEY_MIME));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        Log.e("VideoComposer", outputFormat.toString());
        encoder.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        encoderSurface = new EncoderSurface(encoder.createInputSurface());
        encoderSurface.makeCurrent();
        encoder.start();
        encoderStarted = true;
        encoderOutputBuffers = encoder.getOutputBuffers();

        MediaFormat inputFormat = mediaExtractor.getTrackFormat(trackIndex);
        if (inputFormat.containsKey("rotation-degrees")) {
           inputFormat.setInteger("rotation-degrees", 0);
        }
        decoderSurface = new DecoderSurface(context , filter, listStickerInfo);

        decoderSurface.setRotation(rotation);
        decoderSurface.setOutputResolution(outputResolution);
        decoderSurface.setInputResolution(inputResolution);
        decoderSurface.setFillMode(fillMode);
        decoderSurface.setFillModeCustomItem(fillModeCustomItem);
        decoderSurface.setFlipHorizontal(flipHorizontal);
        decoderSurface.setFlipVertical(flipVertical);
        decoderSurface.completeParams();

        try {
            decoder = MediaCodec.createDecoderByType(inputFormat.getString(MediaFormat.KEY_MIME));

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        decoder.configure(inputFormat, decoderSurface.getSurface(), null, 0);

        decoder.start();
        decoderStarted = true;
        decoderInputBuffers = decoder.getInputBuffers();
    }


    boolean stepPipeline() {
        boolean busy = false;

        int status;
        while (drainEncoder() != DRAIN_STATE_NONE) {
            busy = true;
        }
        do {
            status = drainDecoder();
            if (status != DRAIN_STATE_NONE) {
                busy = true;
            }

        } while (status == DRAIN_STATE_SHOULD_RETRY_IMMEDIATELY);
        while (drainExtractor() != DRAIN_STATE_NONE) {
            busy = true;
        }

        return busy;
    }


    long getWrittenPresentationTimeUs() {
        return writtenPresentationTimeUs;
    }


    boolean isFinished() {
        return isEncoderEOS;
    }


    void release() {
        if (decoderSurface != null) {
            decoderSurface.release();
            decoderSurface = null;
        }
        if (encoderSurface != null) {
            encoderSurface.release();
            encoderSurface = null;
        }
        if (decoder != null) {
            if (decoderStarted) decoder.stop();
            decoder.release();
            decoder = null;
        }
        if (encoder != null) {
            if (encoderStarted) encoder.stop();
            encoder.release();
            encoder = null;
        }
    }

    private int drainExtractor() {
        if (isExtractorEOS) return DRAIN_STATE_NONE;
        int trackIndex = mediaExtractor.getSampleTrackIndex();
        if (trackIndex >= 0 && trackIndex != this.trackIndex) {
            return DRAIN_STATE_NONE;
        }
        int result = decoder.dequeueInputBuffer(0);
        if (result < 0) return DRAIN_STATE_NONE;
        if (trackIndex < 0) {
            isExtractorEOS = true;
            decoder.queueInputBuffer(result, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            return DRAIN_STATE_NONE;
        }
        int sampleSize = mediaExtractor.readSampleData(decoderInputBuffers[result], 0);
        boolean isKeyFrame = (mediaExtractor.getSampleFlags() & MediaExtractor.SAMPLE_FLAG_SYNC) != 0;
        decoder.queueInputBuffer(result, 0, sampleSize,
                mediaExtractor.getSampleTime() / timeScale, isKeyFrame ? MediaCodec.BUFFER_FLAG_SYNC_FRAME : 0);
        mediaExtractor.advance();
        return DRAIN_STATE_CONSUMED;
    }

    private int drainDecoder() {
        if (isDecoderEOS) return DRAIN_STATE_NONE;
        int result = decoder.dequeueOutputBuffer(bufferInfo, 0);
        switch (result) {
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                return DRAIN_STATE_NONE;
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                return DRAIN_STATE_SHOULD_RETRY_IMMEDIATELY;
        }
        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            encoder.signalEndOfInputStream();
            isDecoderEOS = true;
            bufferInfo.size = 0;
        }
        boolean doRender = (bufferInfo.size > 0);
        decoder.releaseOutputBuffer(result, doRender);
        if (doRender) {
            decoderSurface.awaitNewImage();
            decoderSurface.drawImage(bufferInfo.presentationTimeUs / 1000);
            encoderSurface.setPresentationTime(bufferInfo.presentationTimeUs * 1000);
            encoderSurface.swapBuffers();
        }
        return DRAIN_STATE_CONSUMED;
    }

    private int drainEncoder() {
        if (isEncoderEOS) return DRAIN_STATE_NONE;
        int result = encoder.dequeueOutputBuffer(bufferInfo, 0);
        switch (result) {
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                return DRAIN_STATE_NONE;
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                if (actualOutputFormat != null) {
                    throw new RuntimeException("Video output format changed twice.");
                }
                actualOutputFormat = encoder.getOutputFormat();
                muxRender.setOutputFormat(MuxRender.SampleType.VIDEO, actualOutputFormat);
                muxRender.onSetOutputFormat();
                return DRAIN_STATE_SHOULD_RETRY_IMMEDIATELY;
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                encoderOutputBuffers = encoder.getOutputBuffers();
                return DRAIN_STATE_SHOULD_RETRY_IMMEDIATELY;
        }
        if (actualOutputFormat == null) {
            throw new RuntimeException("Could not determine actual output format.");
        }

        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            isEncoderEOS = true;
            bufferInfo.set(0, 0, 0, bufferInfo.flags);
        }
        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            encoder.releaseOutputBuffer(result, false);
            return DRAIN_STATE_SHOULD_RETRY_IMMEDIATELY;
        }
        muxRender.writeSampleData(MuxRender.SampleType.VIDEO, encoderOutputBuffers[result], bufferInfo);
        writtenPresentationTimeUs = bufferInfo.presentationTimeUs;
        encoder.releaseOutputBuffer(result, false);
        return DRAIN_STATE_CONSUMED;
    }
}
