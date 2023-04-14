package com.daasuu.gpuv.composer;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayDeque;
import java.util.Queue;


class AudioChannel {

    private static class AudioBuffer {
        int bufferIndex;
        long presentationTimeUs;
        ShortBuffer data;
    }

    static final int BUFFER_INDEX_END_OF_STREAM = -1;

    private static final int BYTES_PER_SHORT = 2;
    private static final long MICROSECS_PER_SEC = 1000000;

    private final Queue<AudioBuffer> emptyBuffers = new ArrayDeque<>();
    private final Queue<AudioBuffer> filledBuffers = new ArrayDeque<>();

    private final MediaCodec decoder;
    private final MediaCodec encoder;
    private final MediaFormat encodeFormat;

    private int inputSampleRate;
    private int inputChannelCount;
    private int outputChannelCount;

    private final MediaCodecBufferCompatWrapper decoderBuffers;
    private final MediaCodecBufferCompatWrapper encoderBuffers;

    private final AudioBuffer overflowBuffer = new AudioBuffer();

    private MediaFormat actualDecodedFormat;


    AudioChannel(final MediaCodec decoder,
                 final MediaCodec encoder, final MediaFormat encodeFormat) {
        this.decoder = decoder;
        this.encoder = encoder;
        this.encodeFormat = encodeFormat;

        decoderBuffers = new MediaCodecBufferCompatWrapper(this.decoder);
        encoderBuffers = new MediaCodecBufferCompatWrapper(this.encoder);
    }

    void setActualDecodedFormat(final MediaFormat decodedFormat) {
        actualDecodedFormat = decodedFormat;

        inputSampleRate = actualDecodedFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        if (inputSampleRate != encodeFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)) {
            throw new UnsupportedOperationException("Audio sample rate conversion not supported yet.");
        }

        inputChannelCount = actualDecodedFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        outputChannelCount = encodeFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);

        if (inputChannelCount != 1 && inputChannelCount != 2) {
            throw new UnsupportedOperationException("Input channel count (" + inputChannelCount + ") not supported.");
        }

        if (outputChannelCount != 1 && outputChannelCount != 2) {
            throw new UnsupportedOperationException("Output channel count (" + outputChannelCount + ") not supported.");
        }

        overflowBuffer.presentationTimeUs = 0;
    }

    void drainDecoderBufferAndQueue(final int bufferIndex, final long presentationTimeUs) {
        if (actualDecodedFormat == null) {
            throw new RuntimeException("Buffer received before format!");
        }

        final ByteBuffer data =
                bufferIndex == BUFFER_INDEX_END_OF_STREAM ?
                        null : decoderBuffers.getOutputBuffer(bufferIndex);

        AudioBuffer buffer = emptyBuffers.poll();
        if (buffer == null) {
            buffer = new AudioBuffer();
        }

        buffer.bufferIndex = bufferIndex;
        buffer.presentationTimeUs = presentationTimeUs;
        buffer.data = data == null ? null : data.asShortBuffer();

        if (overflowBuffer.data == null) {
            overflowBuffer.data = ByteBuffer
                    .allocateDirect(data.capacity())
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer();
            overflowBuffer.data.clear().flip();
        }

        filledBuffers.add(buffer);
    }

    boolean feedEncoder(long timeoutUs) {
        final boolean hasOverflow = overflowBuffer.data != null && overflowBuffer.data.hasRemaining();
        if (filledBuffers.isEmpty() && !hasOverflow) {

            return false;
        }

        final int encoderInBuffIndex = encoder.dequeueInputBuffer(timeoutUs);
        if (encoderInBuffIndex < 0) {
            return false;
        }

        final ShortBuffer outBuffer = encoderBuffers.getInputBuffer(encoderInBuffIndex).asShortBuffer();
        if (hasOverflow) {
            final long presentationTimeUs = drainOverflow(outBuffer);
            encoder.queueInputBuffer(encoderInBuffIndex,
                    0, outBuffer.position() * BYTES_PER_SHORT,
                    presentationTimeUs, 0);
            return true;
        }

        final AudioBuffer inBuffer = filledBuffers.poll();
        if (inBuffer.bufferIndex == BUFFER_INDEX_END_OF_STREAM) {
            encoder.queueInputBuffer(encoderInBuffIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            return false;
        }

        final long presentationTimeUs = remixAndMaybeFillOverflow(inBuffer, outBuffer);
        encoder.queueInputBuffer(encoderInBuffIndex,
                0, outBuffer.position() * BYTES_PER_SHORT,
                presentationTimeUs, 0);
        if (inBuffer != null) {
            decoder.releaseOutputBuffer(inBuffer.bufferIndex, false);
            emptyBuffers.add(inBuffer);
        }

        return true;
    }

    private static long sampleCountToDurationUs(final int sampleCount,
                                                final int sampleRate,
                                                final int channelCount) {
        return (sampleCount / (sampleRate * MICROSECS_PER_SEC)) / channelCount;
    }

    private long drainOverflow(final ShortBuffer outBuff) {
        final ShortBuffer overflowBuff = overflowBuffer.data;
        final int overflowLimit = overflowBuff.limit();
        final int overflowSize = overflowBuff.remaining();

        final long beginPresentationTimeUs = overflowBuffer.presentationTimeUs +
                sampleCountToDurationUs(overflowBuff.position(), inputSampleRate, outputChannelCount);

        outBuff.clear();
        overflowBuff.limit(outBuff.capacity());
        outBuff.put(overflowBuff);

        if (overflowSize >= outBuff.capacity()) {
            overflowBuff.clear().limit(0);
        } else {
            overflowBuff.limit(overflowLimit);
        }

        return beginPresentationTimeUs;
    }

    private long remixAndMaybeFillOverflow(final AudioBuffer input,
                                           final ShortBuffer outBuff) {
        final ShortBuffer inBuff = input.data;
        final ShortBuffer overflowBuff = overflowBuffer.data;

        outBuff.clear();

        inBuff.clear();

        if (inBuff.remaining() > outBuff.remaining()) {
            inBuff.limit(outBuff.capacity());
            outBuff.put(inBuff);

            inBuff.limit(inBuff.capacity());

            final long consumedDurationUs =
                    sampleCountToDurationUs(inBuff.position(), inputSampleRate, inputChannelCount);
            overflowBuff.put(inBuff);

            overflowBuff.flip();
            overflowBuffer.presentationTimeUs = input.presentationTimeUs + consumedDurationUs;
        } else {
            outBuff.put(inBuff);
        }

        return input.presentationTimeUs;
    }
}
