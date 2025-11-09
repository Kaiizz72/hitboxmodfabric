package com.craftvn.anivoice

import android.media.AudioFormat
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.io.android.AndroidAudioPlayer
import be.tarsos.dsp.pitch.PitchShifter
import kotlin.math.pow

enum class VoiceMode(val semitones: Float) {
    ANIME_NU(+7f),   // cao hơn ~5-7 bán âm cho cảm giác "anime nữ"
    ANIME_NAM(-4f);  // trầm hơn vài bán âm cho cảm giác "anime nam"
}

class VoiceEngine {
    @Volatile
    private var dispatcher: AudioDispatcher? = null

    fun start(mode: VoiceMode) {
        stop()
        val sampleRate = 44100
        val bufferSize = 1024
        val overlap = 0

        val d = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, overlap)

        // TarsosDSP PitchShifter uses a pitch factor (ratio), convert semitones -> ratio
        val ratio = 2f.pow(mode.semitones / 12f)

        val shifter = PitchShifter(ratio, bufferSize, overlap, sampleRate.toFloat())

        // (Optional) lightweight compressor/limiter-like soft clipper
        val softClipper = object : AudioProcessor {
            override fun processingFinished() {}
            override fun process(audioEvent: AudioEvent): Boolean {
                val buf = audioEvent.floatBuffer
                for (i in buf.indices) {
                    var x = buf[i]
                    // Simple soft clip
                    val a = 0.8f
                    x = when {
                        x > a -> a + (x - a) / (1f + ((x - a) / (1f - a)))
                        x < -a -> -a + (x + a) / (1f + (-(x + a) / (1f - a)))
                        else -> x
                    }
                    buf[i] = x
                }
                return true
            }
        }

        d.addAudioProcessor(shifter)
        d.addAudioProcessor(softClipper)
        d.addAudioProcessor(AndroidAudioPlayer(sampleRate, bufferSize,
            AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT))

        dispatcher = d
        Thread(d, "Anivoice-Dispatcher").start()
    }

    fun stop() {
        dispatcher?.stop()
        dispatcher = null
    }
}