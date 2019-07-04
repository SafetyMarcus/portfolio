package com.safetymarcus.annotationprocessor

import androidx.lifecycle.LifecycleOwner
import com.squareup.kotlinpoet.*
import javax.lang.model.element.Element

/**
 * The base class for generating a ViewStateStore interface. All stores will be generated in the format
 * of "$[className]Store" and will have a property for returning the current state of the store,
 * an "observe" function that allows registering to watch changes in the current state, and
 * a "performActions" function that will take in a vararg of properties.
 *
 * The type of argument allowed to be passed into "performActions"  will extend any class that was
 * tagged with the [Actions] annotation.
 *
 * @author Marcus Hooper
 */
class StoreFactory(private val pkg: String, private val className: String, private val element: Element) {

    val action: ClassName
        get() = ClassName(pkg, actionName)

    private val actionName
        get() = "$className.${element.enclosedElements.firstOrNull { inner ->
            inner.getAnnotation(Actions::class.java) != null
        }?.simpleName?.toString() ?: "Action"}"

    val fileName = "${className}Store"

    fun produce() = TypeSpec.interfaceBuilder(fileName)
        .addProperty(currentStateProperty)
        .addFunction(observeFunction)
        .addFunction(performActionsFunction)
        .build()

    private val currentStateProperty
        get() = PropertySpec.builder(
            "currentState",
            ClassName(pkg, "${className}ViewState")
        ).build()

    private val observeFunction
        get() = FunSpec
            .builder("observe")
            .addModifiers(KModifier.ABSTRACT)
            .addParameter("owner", LifecycleOwner::class)
            .addParameter(
                "observer", LambdaTypeName.get(
                    parameters = *arrayOf(TypeVariableName("${className}ViewState")),
                    returnType = Unit::class.asClassName()
                )
            ).build()

    private val performActionsFunction
        get() = FunSpec
            .builder("performActions")
            .addModifiers(KModifier.ABSTRACT)
            .addParameter("actions", action, KModifier.VARARG)
            .build()
}