package com.safetymarcus.portfolio.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.io.Serializable

/**
 * @author Marcus Hooper
 */
/**
 * Convenience class for performing [Fragment] changes (add, replace) and for showing [DialogFragment]s, while adding
 *  a variable number of arguments to [Fragment.setArguments].
 *
 * @param id    The id to be used to replace the fragment. Can be ignored in the case of show()
 */
class FragmentBuilder(
    val fragmentManager: FragmentManager,
    val id: Int = 0,
    var args: Bundle = Bundle()
) {
    var cancellable: Boolean = true

    /**
     * Replaces any [Fragment]] currently being shown at the specified id with the given fragmentManager with a new
     * fragment of the same type as [T], after executing the given lambda
     */
    inline fun <reified T : Fragment> replace(
        addToStack: Boolean = false,
        popFirst: Boolean = false,
        build: FragmentBuilder.() -> Unit = {}
    ) {
        this.build()
        if (popFirst) while (fragmentManager.backStackEntryCount > 1) fragmentManager.popBackStackImmediate()
        fragmentManager.beginTransaction().replace(id, T::class.java.newInstance().apply {
            arguments = args
        }).apply {
            if (addToStack) addToBackStack(T::class.java.simpleName)
        }.commit()
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
        args = Bundle()
    }

    /**
     * Maps the passed in value to the [args] bundle if it has a known mapping,
     * otherwise throws [UnsupportedOperationException].
     */
    inline infix fun <reified T> String.to(value: T) {
        when (value) {
            is Int -> args.putInt(this, value)
            is String -> args.putString(this, value)
            is Boolean -> args.putBoolean(this, value)
            is Serializable -> args.putSerializable(this, value)
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
            is ByteArray -> bundle.putByteArray(this, value)
            else -> throw UnsupportedOperationException("${T::class.java} is currently not supported. Please update \"BundleBuilder.String.to\"")
        }
    }

    companion object {
        fun bundle(build: BundleBuilder.() -> Unit = {}) = BundleBuilder().bundle(build)
    }
}

/**
 * A convenience builder class for alert dialogs, allowing easy calls to common set up values.
 *
 * In order to show a dialog with a body and no title, with a positive button that performs an
 * action and a negative button that does nothing, you would call.
 *
 * AlertBuilder(context).show {
 *      message = R.string.someMessage
 *      positive(R.string.ok) { //do something }
 *      negative(R.string.cancel)
 *      onDismiss = DialogInterface.OnDismissListener { //do something else }
 *  }
 */
class AlertBuilder(context: Context, style: Int = 0) {
    private val builder = AlertDialog.Builder(context, style)
    var title: Int = 0
        set(value) = builder.run { setTitle(value) }
    var message: Int = 0
        set(value) = builder.run { setMessage(value) }
    val positive = AlertButtonBuilder(DialogInterface.BUTTON_POSITIVE)
    val negative = AlertButtonBuilder(DialogInterface.BUTTON_NEGATIVE)
    val neutral = AlertButtonBuilder(DialogInterface.BUTTON_NEUTRAL)
    var onDismiss: DialogInterface.OnDismissListener? = null

    fun show(build: AlertBuilder.() -> Unit): AlertDialog {
        this.build()
        this attachButton positive attachButton negative attachButton neutral
        onDismiss?.let { builder.setOnDismissListener(it) }
        return builder.show()
    }

    /**
     * A convenience class for creating alert dialog buttons. Intended to only be used internally
     * within [AlertBuilder]. If [text] is equal to -1, this should not be attached to a dialog.
     *
     * @param which The position of the button. One of [DialogInterface.BUTTON_POSITIVE],
     * [DialogInterface.BUTTON_NEGATIVE], or [DialogInterface.BUTTON_NEUTRAL]
     */
    class AlertButtonBuilder(val which: Int) {
        var text: Int = -1
        var callback: DialogInterface.OnClickListener? = null

        operator fun invoke(text: Int, init: (() -> Unit)? = null) {
            this.text = text
            init?.let {
                callback = DialogInterface.OnClickListener { _, _ -> it() }
            }
        }
    }

    private infix fun attachButton(buttonBuilder: AlertButtonBuilder): AlertBuilder {
        if (buttonBuilder.text != -1)
            when (buttonBuilder.which) {
                DialogInterface.BUTTON_POSITIVE -> builder.setPositiveButton(
                    buttonBuilder.text,
                    buttonBuilder.callback
                )
                DialogInterface.BUTTON_NEGATIVE -> builder.setNegativeButton(
                    buttonBuilder.text,
                    buttonBuilder.callback
                )
                DialogInterface.BUTTON_NEUTRAL -> builder.setNeutralButton(
                    buttonBuilder.text,
                    buttonBuilder.callback
                )
            }
        return this
    }

    companion object {
        fun showAlert(context: Context, style: Int = 0, build: AlertBuilder.() -> Unit) =
            AlertBuilder(context, style).show(build)
    }
}

class IntentBuilder {
    lateinit var intent: Intent
    var bundle: Bundle? = null
    var requestCode = 0

    inline fun <reified T : AppCompatActivity> startActivity(
        context: Context?,
        props: IntentBuilder.() -> Unit = {}
    ) {
        val intent = classIntent<T>(context, props)
        if (requestCode > 0) context?.startActivityForResult(intent, requestCode, bundle)
        else context?.startActivity(intent, bundle)
    }

    inline fun emptyIntent(props: IntentBuilder.() -> Unit = {}): Intent {
        intent = Intent()
        this.props()
        return intent
    }

    inline fun <reified T : AppCompatActivity> classIntent(
        context: Context?,
        props: IntentBuilder.() -> Unit = {}
    ): Intent {
        intent = Intent(context, T::class.java)
        this.props()
        return intent
    }

    @Suppress("UNCHECKED_CAST")
    inline infix fun <reified T> String.to(value: T) {
        when (value) {
            is String -> intent.putExtra(this, value)
            is ArrayList<*> -> (value as? ArrayList<Parcelable>)?.let {
                intent.putParcelableArrayListExtra(
                    this,
                    it
                )
            }
            is Serializable -> intent.putExtra(this, value)
            is Boolean -> intent.putExtra(this, value)
            is Parcelable -> intent.putExtra(this, value)
            else -> throw UnsupportedOperationException("Please add support for ${T::class.java} to IntentBuilder.String.to()")
        }
    }

    companion object {
        inline fun <reified T : AppCompatActivity>
                startActivity(context: Context?, props: IntentBuilder.() -> Unit) =
            IntentBuilder().startActivity<T>(context, props)

        inline fun emptyIntent(props: IntentBuilder.() -> Unit) = IntentBuilder().emptyIntent(props)

        inline fun <reified T : AppCompatActivity> intent(
            context: Context,
            props: IntentBuilder.() -> Unit
        ) = IntentBuilder().classIntent<T>(context, props)
    }
}

fun Context.startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) =
    (this as? CoroutineActivity)?.startActivityForResult(intent, requestCode, options)
        ?: startActivity(intent, options)