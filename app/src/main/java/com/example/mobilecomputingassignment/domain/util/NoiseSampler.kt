// file: util/NoiseSampler.kt
package com.example.mobilecomputingassignment.util

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlin.math.log10
import kotlin.math.sqrt

object NoiseSampler {
    data class Result(val dbfs: Double, val rms: Double)

    /**
     * Samples the mic for ~N ms and returns dBFS (20*log10(rms/32767)).
     * Returns null if mic init fails or permission missing.
     */
    fun sampleDbFs(
        sampleRate: Int = 44100,
        durationMs: Int = 800,               // ~0.8s is enough
        source: Int = MediaRecorder.AudioSource.UNPROCESSED, // fallback to MIC inside
    ): Result? {
        val channel = AudioFormat.CHANNEL_IN_MONO
        val format  = AudioFormat.ENCODING_PCM_16BIT
        val minBuf  = AudioRecord.getMinBufferSize(sampleRate, channel, format)
        if (minBuf <= 0) return null

        val bufSize = maxOf(minBuf, (sampleRate * durationMs / 1000))
        val record = try {
            AudioRecord(source, sampleRate, channel, format, bufSize)
        } catch (_: Exception) { null } ?: return null

        if (record.state != AudioRecord.STATE_INITIALIZED) {
            // Try fallback source MIC
            val fallback = try {
                AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channel, format, bufSize)
            } catch (_: Exception) { null } ?: return null
            if (fallback.state != AudioRecord.STATE_INITIALIZED) return null
            return capture(fallback, bufSize)
        }
        return capture(record, bufSize)
    }

    private fun capture(rec: AudioRecord, bufSize: Int): Result? {
        return try {
            val buf = ShortArray(bufSize)
            rec.startRecording()
            val n = rec.read(buf, 0, buf.size)
            rec.stop()
            rec.release()
            if (n <= 0) return null

            // Compute RMS
            var sum = 0.0
            for (i in 0 until n) {
                val s = buf[i].toDouble()
                sum += s * s
            }
            val rms = sqrt(sum / n)
            //-81 silence, 0 max loudness
            val dbfs = 20.0 * log10((rms + 1e-9) / 32767.0) // avoid log(0)
            Result(dbfs = dbfs, rms = rms)
        } catch (_: Exception) {
            runCatching { rec.release() }
            null
        }
    }
}
