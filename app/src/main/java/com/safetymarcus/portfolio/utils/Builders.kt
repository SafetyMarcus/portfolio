package com.safetymarcus.portfolio.utils

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.json.JSONObject
import java.io.Serializable

/**
 * Convenience class for building [JSONObject]s.
 *
 * e.g. JsonObjectBuilder().json {
 *     "string" to "Some string"
 *     "int" to 0
 * }
 *
 * This will return a [JSONObject] with a [String] and an [Int] property stored against the passed in keys
 */
class JsonObjectBuilder(private val json: JSONObject = JSONObject()) {
    fun json(build: JsonObjectBuilder.() -> Unit): JSONObject {
        this.build()
        return json
    }

    infix fun <T> String.to(value: T) {
        json.put(this, value)
    }
}

/**
 * Convenience class for performing [Fragment] changes (add, replace) and for showing [DialogFragment]s, while adding
 *  a variable number of arguments to [Fragment.setArguments].
 *
 * @param id    The id to be used to replace the fragment. Can be ignored in the case of show()
 */
class FragmentBuilder(val fragmentManager: FragmentManager, val id: Int = 0, var args: Bundle = Bundle()) {
    var cancellable: Boolean = true

    /**
     * Replaces any [Fragment]] currently being shown at the specified id with the given fragmentManager with a new
     * fragment of the same type as [T], after executing the given lambda
     */
    inline fun <reified T : Fragment> replace(build: FragmentBuilder.() -> Unit = {}) {
        this.build()
        fragmentManager.beginTransaction().replace(id, T::class.java.newInstance().apply {
            arguments = args
        }).commit()
        args = Bundle()
    }

    /**
     * Shows a new [DialogFragment] of the same type as [T] with the given fragmentManager, after executing
     * the given lambda
     */
    inline fun <reified T : DialogFragment> show(build: FragmentBuilder.() -> Unit = {}) {
        this.build()
        T::class.java.newInstance().apply {
            arguments = args
            this.isCancelable = cancellable
        }.show(fragmentManager, T::class.java.simpleName)
    }

    /**
     * Maps the passed in value to the [args] bundle if it has a known mapping,
     * otherwise throws [UnsupportedOperationException].
     */
    inline infix fun <reified T> String.to(value: T) {
        when (value) {
            is Int -> args.putInt(this, value)
            is String -> args.putString(this, value)
            else -> throw UnsupportedOperationException("${T::class.java} is currently not supported. Please update \"FragmentBuilder.String.to\"")
        }
    }
}

/**
 * Convenience class for building [Bundle]s
 *
 * e.g. BundleBuilder.bundle {
 *     "key" to "value"
 * }
 */
class BundleBuilder {
    val bundle = Bundle()

    inline fun bundle(build: BundleBuilder.() -> Unit = {}): Bundle {
        this.build()
        return bundle
    }

    /**
     * Maps the given string to the passed in value within the internal [Bundle] object
     */
    inline infix fun <reified T> String.to(value: T) {
        when (value) {
            is String -> bundle.putString(this, value)
            is Serializable -> bundle.putSerializable(this, value)
            else -> throw UnsupportedOperationException("${T::class.java} is currently not supported. Please update \"BundleBuilder.String.to\"")
        }
    }
}
