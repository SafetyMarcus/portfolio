package com.safetymarcus.portfolio.utils

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewAnimationUtils
import android.view.Window
import android.widget.EditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.addListener
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

/**
 * @author Marcus Hooper
 */

/**
 * Creates a scene transition from a [View], including the status bar and navigation bar backgrounds as shared elements.
 * The context of the view will be cast to an activity in ordeer to generate the scene transition.
 */
fun makeSceneTransition(view: View, elements: ArrayList<Pair<View, String>>): ActivityOptionsCompat {
    val activity = view.context as Activity
    activity.findViewById<View>(android.R.id.statusBarBackground)
        ?.let { elements.add(Pair(it, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME)) }
    activity.findViewById<View>(android.R.id.navigationBarBackground)
        ?.let { elements.add(Pair(it, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME)) }
    return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *elements.toTypedArray())
}

/**
 * Generates a [kotlin.Pair] object holding the properties needed to transition with this view
 */
val View.transition get() = kotlin.Pair(this, this.transitionName)

/**
 * Creates a circular reveal animation withe the given delay and start and finish listeners. This will by default
 * set the view to visible before the animation starts and start in the center of the view
 *
 * @param delay            Length of time before the circular reveal animation should start
 * @param centerX        The X position for the animation to start from
 * @param centerY        The Y position for the animation to start from
 * @param reverse        Whether or not the animation should be played forwards (expanding) or in reverse (collapsing)
 * @param startListener    A callback to be called when the animation starts playing
 * @param finishListener    A callback to be called when the animation finishes playing
 */
fun View.circularReveal(
    delay: Long = 0, centerX: Int = width / 2, centerY: Int = height / 2, reverse: Boolean = false,
    startListener: () -> Unit = {}, finishListener: () -> Unit = {}
) {
    val start = if (reverse) Math.hypot(width.toDouble(), height.toDouble()).toFloat() else 0f
    val end = if (reverse) 0f else Math.hypot(width.toDouble(), height.toDouble()).toFloat()
    ViewAnimationUtils.createCircularReveal(this, centerX, centerY, start, end).apply {
        addListener(onEnd = {
            if (reverse) isVisible = false
            finishListener()
        }, onStart = {
            isVisible = true
            startListener()
        })
        startDelay = delay
    }.start()
}

/**
 * A convenience method to only listen to the [ViewPager.OnPageChangeListener.onPageSelected]
 * calls from [OnPageChangeListener][ViewPager.OnPageChangeListener]
 */
fun ViewPager.onPageSelected(listener: (Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            //Do nothing
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            //Do nothing
        }

        override fun onPageSelected(position: Int) {
            listener(position)
        }
    })
}

/**
 * A convenience method to only listen to the [TextWatcher.onTextChanged] calls from [TextWatcher]. On text changed
 * will not be called in the case that the previous text was exactly the same
 */
fun EditText.addTextListener(listener: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        var currentText = ""

        override fun afterTextChanged(s: Editable?) {
            currentText = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //Do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s.toString().takeIf { it != currentText }?.let(listener)
        }
    })
}

/**
 * Sets up an app bar layout to swap between a [AppCompatTextView] title and a [Toolbar] title
 * depending on the scroll level of the screen.
 */
fun AppBarLayout.setUpForCollapsingTitle(toolbar: Toolbar, titleBar: AppCompatTextView, title: String) {
    titleBar.text = title
    addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, offset ->
        //Collapsed
        if (abs(offset) >= totalScrollRange) {
            toolbar.title = title
            titleBar.text = ""
        } else {
            titleBar.text = title
            toolbar.title = ""
        }
    })
}