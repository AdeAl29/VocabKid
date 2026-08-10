package com.example.vocabkid.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Process
import kotlin.math.PI
import kotlin.math.sin

class BackgroundMusicPlayer {
    private val lock = Any()
    private var audioThread: Thread? = null
    private var playbackGeneration = 0
    @Volatile private var isRunning = false
    private var sampleIndex = 0L

    fun start() {
        synchronized(lock) {
            if (isRunning) return

            isRunning = true
            val generation = ++playbackGeneration
            audioThread = Thread({ playLoop(generation) }, "VocabKidMusicPlayer").apply {
                isDaemon = true
                start()
            }
        }
    }

    fun stop() {
        synchronized(lock) {
            isRunning = false
            playbackGeneration += 1
        }
    }

    fun release() {
        val thread = synchronized(lock) {
            isRunning = false
            playbackGeneration += 1
            audioThread.also { audioThread = null }
        }
        thread?.join(300)
    }

    private fun playLoop(generation: Int) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)

        val minBufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSizeBytes = maxOf(minBufferSize, BUFFER_SAMPLES * BYTES_PER_SAMPLE * 2)
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setBufferSizeInBytes(bufferSizeBytes)
            .build()

        val buffer = ShortArray(BUFFER_SAMPLES)

        try {
            audioTrack.play()
            while (shouldKeepPlaying(generation)) {
                fillBuffer(buffer)
                audioTrack.write(buffer, 0, buffer.size)
            }
        } finally {
            runCatching {
                audioTrack.pause()
                audioTrack.flush()
            }
            audioTrack.release()
        }
    }

    private fun shouldKeepPlaying(generation: Int): Boolean {
        return isRunning && synchronized(lock) { generation == playbackGeneration }
    }

    private fun fillBuffer(buffer: ShortArray) {
        repeat(buffer.size) { index ->
            buffer[index] = (nextSample() * Short.MAX_VALUE)
                .toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
        }
    }

    private fun nextSample(): Double {
        val time = sampleIndex.toDouble() / SAMPLE_RATE.toDouble()
        val beatSample = sampleIndex % BEAT_SAMPLES
        val beatPhase = beatSample.toDouble() / BEAT_SAMPLES.toDouble()
        val beatIndex = ((sampleIndex / BEAT_SAMPLES) % MELODY.size).toInt()
        val chordIndex = ((sampleIndex / (BEAT_SAMPLES * 4)) % CHORDS.size).toInt()

        val noteEnvelope = cheerfulEnvelope(beatPhase)
        val melodyNote = MELODY[beatIndex]
        val melody = sine(melodyNote, time) * 0.034 * noteEnvelope
        val bell = sine(melodyNote * 2.0, time) * 0.012 * noteEnvelope

        val chord = CHORDS[chordIndex]
        val padEnvelope = 0.72 + 0.18 * sin(2.0 * PI * time / 6.0)
        val pad = (
            sine(chord[0], time) * 0.008 +
                sine(chord[1], time) * 0.006 +
                sine(chord[2], time) * 0.006
            ) * padEnvelope

        val bassEnvelope = cheerfulEnvelope((beatPhase * 2.0) % 1.0)
        val bass = sine(BASS[chordIndex], time) * 0.016 * bassEnvelope

        val sparkleEnvelope = if (beatIndex % 4 == 2) noteEnvelope else 0.0
        val sparkle = sine(melodyNote * 3.0, time) * 0.004 * sparkleEnvelope

        sampleIndex += 1
        return (melody + bell + pad + bass + sparkle).coerceIn(-0.18, 0.18)
    }

    private fun cheerfulEnvelope(phase: Double): Double {
        val activePhase = phase.coerceIn(0.0, 0.82)
        val pluck = sin(PI * activePhase / 0.82).coerceAtLeast(0.0)
        val bounce = 0.82 + 0.18 * sin(2.0 * PI * phase).coerceAtLeast(0.0)
        return pluck * bounce
    }

    private fun sine(frequency: Double, time: Double): Double {
        return sin(2.0 * PI * frequency * time)
    }

    private companion object {
        const val SAMPLE_RATE = 22_050
        const val BUFFER_SAMPLES = 1_024
        const val BYTES_PER_SAMPLE = 2
        const val BPM = 104
        const val BEAT_SAMPLES = SAMPLE_RATE * 60 / BPM

        val MELODY = doubleArrayOf(
            523.25, 659.25, 783.99, 1046.50,
            783.99, 659.25, 587.33, 659.25,
            440.00, 523.25, 659.25, 783.99,
            659.25, 523.25, 493.88, 523.25,
            349.23, 440.00, 523.25, 659.25,
            523.25, 440.00, 392.00, 440.00,
            392.00, 493.88, 587.33, 783.99,
            659.25, 587.33, 523.25, 659.25
        )

        val CHORDS = arrayOf(
            doubleArrayOf(261.63, 329.63, 392.00),
            doubleArrayOf(220.00, 261.63, 329.63),
            doubleArrayOf(174.61, 261.63, 349.23),
            doubleArrayOf(196.00, 246.94, 392.00)
        )

        val BASS = doubleArrayOf(
            130.81,
            110.00,
            87.31,
            98.00
        )
    }
}
