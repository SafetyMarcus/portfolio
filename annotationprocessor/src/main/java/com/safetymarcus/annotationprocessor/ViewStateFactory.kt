package com.safetymarcus.annotationprocessor

import com.squareup.kotlinpoet.*
import javax.lang.model.element.Element
import javax.lang.model.element.VariableElement

/**
 * Used to generate a full [TypeSpec] holding all properties and functions necessary for a
 * ViewState to be saved to file with a [FileSpec].
 *
 * @param element   An element tagged with the [Contract] annotation.
 * It will use this to then look for a class annotated with either [ViewState] in order to generate the
 * correct ViewState class.
 *
 * Generated view states will always be in the format of "${element.simpleName}ViewState"
 * e.g. a class called LoginContract tagged as the contract will generate a view state called
 * LoginContractViewState
 *
 * @author Marcus Hooper
 */
class ViewStateFactory(element: Element) {
    private var enclosed: Element? = null
    private val properties by lazy {
        enclosed?.enclosedElements?.filter { it is VariableElement } ?: listOf()
    }

    val fileName = "${element.simpleName}ViewState"
    private val classBuilder = TypeSpec.classBuilder(fileName)
    private val constructorFactory by lazy { ConstructorFactory(properties, classBuilder) }

    init {
        enclosed = element.enclosedElements.firstOrNull {
            it.getAnnotation(ViewState::class.java) != null
        }
    }

    /**
     * Assuming that an element annotated with the [ViewState][ViewState] annotation exists within
     * the originally passed in element, this will generate a view state encapsulating all of the
     * passed in properties with reasonable defaults and copy functions.
     */
    fun produce(): TypeSpec {
        if (enclosed != null) constructorFactory.produce()
        return classBuilder.build()
    }
}

/**
 * Factory used to generate the constructors for a view state class. This should only be used
 * by [ViewStateFactory]
 */
class ConstructorFactory(private val properties: List<Element>, private val classBuilder: TypeSpec.Builder) {
    /**
     * Creates the constructor for the view state, taking into account need to extend other classes
     * and override properties, and adds them to the passed in [classBuilder]
     */
    fun produce(): TypeSpec.Builder {
        val ctor = FunSpec.constructorBuilder()
        classBuilder.addModifiers(KModifier.DATA)
        addPropertiesToConstructor(ctor)
        return classBuilder.primaryConstructor(ctor.build())
    }

    private fun addPropertiesToConstructor(ctor: FunSpec.Builder) {
        properties.forEach {
            val prop = ParameterSpec.builder(it.simpleString, it.kotlinType)
            if (it.defaultValue.isNotEmpty()) prop.defaultValue(it.defaultValue)
            ctor.addParameter(
                prop
                    .build()
            )
            classBuilder.addProperty(
                PropertySpec
                    .builder(it.simpleString, it.kotlinType)
                    .initializer(it.simpleString)
                    .build()
            )
        }
    }
}