package com.safetymarcus.portfolio.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.Surface
import android.view.TextureView
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.safetymarcus.portfolio.PortfolioApplication
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Uses the application singleton instance to attempt to resolve the integer (id) into a string.
 * This is the same as calling [android.content.Context.getString]
 */
fun Int.getString(): String = PortfolioApplication.INSTANCE.getString(this)

/**
 * Uses the application singleton instance to attempt to resolve the integer (id) into a colour int.
 * This is the same as calling [ResourcesCompat.getColor]
 */
@ColorInt
fun Int.getColour() = ResourcesCompat.getColor(PortfolioApplication.INSTANCE.resources, this, null)

/**
 * Uses the application singleton instance to attempt to resolve the integer (id) into an integer specified in a resource
 * file. e.g. R.integer.column_count
 *
 * This is the same as calling [android.content.res.Resources.getInteger]
 */
@ColorInt
fun Int.getIntRes() = PortfolioApplication.INSTANCE.resources.getInteger(this)

/**
 * Uses the application singleton instance to attempt to resolve the integer (id) into a [Drawable]
 * This is the same as calling [ResourcesCompat.getDrawable]
 */
fun Int.getDrawable(): Drawable? = ResourcesCompat.getDrawable(PortfolioApplication.INSTANCE.resources, this, null)

/**
 * Returns the current integer in pixels after conversion based on device independent pixels, or 1 if that values is <= 0
 */
fun Int.convertToPixels() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(),
    PortfolioApplication.INSTANCE.resources.displayMetrics
).roundToInt().takeIf { it > 0 } ?: 1

/**
 * Animates a scaling animation on the x AND y axis from its current scale to the provided scale over 200ms.
 * This will also manage setting the view to [View.VISIBLE] when the animation starts and [View.GONE] when it ends
 */
fun View.scaleTo(scale: Float) {
    isVisible = true
    animate().scaleX(scale).scaleY(scale).setDuration(200)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                this@scaleTo.isVisible = scale > 0f
            }
        }).start()
}

/**
 * Logs the message with the exception on a new line if it exists, tagged using the calling class's name as a DEBUG
 * level log message
 */
fun Any.logDebug(message: String, e: Exception? = null) = Log.d("$javaClass", "$message${"\n${e?.message ?: ""}"}")

/**
 * Logs the message with the exception on a new line if it exists, tagged using the calling class's name as an INFO
 * level log message
 */
fun Any.logInfo(message: String, e: Exception? = null) = Log.i("$javaClass", "$message${"\n${e?.message ?: ""}"}")

/**
 * Used to set an activity to full screen. This should be used for interactions such as recording and watching videos
 */
fun Activity.setFullScreen() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
}

/**
 * Wraps a [String] property in a [SharedPreferences] instance for get and set calls. This ensures that all writes
 * are written immediately to the preferences and all reads have the latest value
 */
class StringPref(private val prefs: SharedPreferences, private val key: String) : ReadWriteProperty<Any?, String> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = prefs.getString(key, "") ?: ""

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        prefs.edit { putString(key, value) }
    }
}

/**
 * Wraps a [Boolean] property in a [SharedPreferences] instance for get and set calls. This ensures that all writes
 * are written immediately to the preferences and all reads have the latest value
 */
class BooleanPref(private val prefs: SharedPreferences, private val key: String) : ReadWriteProperty<Any?, Boolean> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = prefs.getBoolean(key, false)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }
}

/**
 * Wraps an [Int] property in a [SharedPreferences] instance for get and set calls. This ensures that all writes
 * are written immediately to the preferences and all reads have the latest value
 */
class IntPref(private val prefs: SharedPreferences, private val key: String, private val default: Int = 0) :
    ReadWriteProperty<Any?, Int> {

    override fun getValue(thisRef: Any?, property: KProperty<*>) = prefs.getInt(key, default)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        prefs.edit { putInt(key, value) }
    }
}

/**
 * Creates a copy of this array but with the replacement at the specified index. This will remove any existing values
 * at that index. This will just be a shallow copy of the existing array.
 *
 * @param index          The index of the existing item to replace
 * @param replacement    The new item to be placed at the specified index
 */
fun <T> List<T>.replaceAt(index: Int, replacement: T) = ArrayList(this).apply {
    if (index < size && index != -1) removeAt(index)
    add(index, replacement)
} as List<T>

/**
 * Outputs a string representation of this date object in the format of Day Month Year. e.g. 25 August 2018
 */
val Date.dayMonthYear: String
    get() = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(this)

/**
 * Outputs a string representation of this date object in the format of Day Month Year. e.g. 25 Aug 2018
 */
val Date.dayMonYear: String
    get() = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(this)

/**
 * Attempts to parse a string to return the first character from the first two words.
 * If any of the words cannot be parsed (length of 0, no spaces, etc.) this will just return empty strings.
 */
val String.initials
    get() = with(split(" ")) {
        "${getOrNull(0)?.firstOrNull() ?: ""}${getOrNull(1)?.firstOrNull() ?: ""}"
    }

/**
 * Used to compare two classes and assert if they are of the same class or not
 */
fun Any.sameClass(other: Any) = this.javaClass == other.javaClass

/**
 * Attempts to pull the given key out of the activity's Intent extras in a lazy way.
 * e.g. val title: String by extra(TITLE, "default title")
 */
inline fun <reified T : Any> Activity.extra(key: String, default: T) = lazy {
    (intent?.extras?.get(key) as? T) ?: default
}

/**
 * Attempts to pull the given key out of the fragment's Bundle arguments in a lazy way.
 * e.g. val title: String by argument(TITLE, "default title")
 */
inline fun <reified T : Any> Fragment.argument(key: String, default: T) = lazy {
    (arguments?.get(key) as? T) ?: default
}

fun TextureView.updateTransform() {
    val matrix = Matrix()

    // Compute the center of the view finder
    val centerX = width / 2f
    val centerY = height / 2f

    // Correct preview output to account for display rotation
    val rotationDegrees = when (display.rotation) {
        Surface.ROTATION_0 -> 0
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        Surface.ROTATION_270 -> 270
        else -> return
    }

    // Finally, apply transformations to our TextureView
    matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
    setTransform(matrix)
}

/**
 * Checks to see if the current resources configuration can be coerced to [Configuration.UI_MODE_NIGHT_YES]
 */
val Resources.nightModeEnabled
    get() = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES