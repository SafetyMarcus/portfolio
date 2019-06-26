package com.safetymarcus.portfolio.video

import android.media.MediaMetadataRetriever
import android.net.Uri
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack
import com.safetymarcus.portfolio.PortfolioApplication
import com.safetymarcus.portfolio.utils.logDebug
import com.safetymarcus.portfolio.utils.logInfo
import java.io.File

/**
 * @author Marcus Hooper
 */

/**
 * Returns the duration of a media file in seconds as stored in the "duration" section of the file's metadata, rounded
 * up to the nearest second
 */
val File.seconds: Int
    get() = Math.ceil(this.milliseconds / 1000.00).toInt()

/**
 * Returns the duration of a media file in milliseconds as stored in the "duration" section of the file's metadata
 */
val File.milliseconds: Long
    get() {
        val resolver = MediaMetadataRetriever().apply {
            setDataSource(PortfolioApplication.INSTANCE, Uri.fromFile(this@milliseconds))
        }
        return try {
            resolver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
        } catch (e: IllegalStateException) {
            logDebug("File ${this.path} does not contain duration metadata", e)
            0
        } finally {
            resolver.release()
        }
    }

/**
 * Compares this track to any other track and will return the difference in durations of the two
 *
 * @param other A track to compare to this one
 *
 * @return The difference in duration of this track minus the [other] track. This can be negative
 */
fun Track.getTimeDifference(other: Track) =
    duration / trackMetaData.timescale.toFloat() - other.duration / other.trackMetaData.timescale.toFloat()

/**
 * Given a video and an audio track, this function will remove the samples from whichever track is longer until
 * they are both the same length. This should be used to avoid audio and video sync issues when combining multiple
 * tracks together. This is necessary because audio and video codecs record at different rates and with different
 * file sizes, which means for a single clip the audio and video tracks will almost always differ in length by
 * milliseconds. While not noticeable for one clip on its own, this becomes much more obvious when you start to
 * combine clips together.
 *
 * @return A [Pair] of Video track to Audio track. The returned data will always be in this order.
 */
private fun Pair<Track, Track>.normaliseTrackDurations(): Pair<Track, Track> {
    val (video, audio) = this
    val timeDifference = audio.getTimeDifference(video)
    logInfo("Time difference between audio and video was ${timeDifference}s")
    return when {
        timeDifference > 0 -> { //Audio was longer
            logInfo("Cropping the audio track")
            video to audio.normaliseTrack(timeDifference)
        }
        timeDifference < 0 -> { //Video was longer
            logInfo("Cropping the video track")
            video.normaliseTrack(timeDifference * -1) to audio
        }
        else -> video to audio
    }
}

/**
 * Will remove samples from the end of the track in order to make up the time difference provided.
 *
 * @param timeDifference The amount of time that needs to be made up by removing samples
 *
 * @return The new [CroppedTrack] with the samples removed from the end
 */
fun Track.normaliseTrack(timeDifference: Float): Track {
    val sampleDifference = sampleDurations.getSampleDifference(trackMetaData.timescale.toFloat(), timeDifference)
    logInfo("Samples to remove $sampleDifference")
    return CroppedTrack(this, 0, samples.size - sampleDifference)
}

/**
 * Given a set of sample durations, a timescale for the durations, and a difference to make up, this function will
 * return the number of samples that need to be removed in order to achieve a difference of 0 or less
 *
 * @param scale The scale that the time is being measured in. This will be used to convert the sample times
 * @param timeDifference The difference in time between two tracks that needs to be made up. This should already
 * be in the time scale of [scale]
 *
 * @return The number of samples that will need to be removed from the original track
 */
private fun LongArray.getSampleDifference(scale: Float, timeDifference: Float): Long {
    var remaining = timeDifference
    var counter = 0L
    for (i in size - 1 downTo 0) {
        logInfo("$remaining remaining at index $i")
        if (remaining <= 0) break
        counter++
        logInfo("Removing ${get(i) / scale}")
        remaining -= get(i) / scale //Reduce the total difference by the size of the sample
    }
    return counter
}
