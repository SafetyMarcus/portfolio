package com.safetymarcus.annotationprocessor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

/**
 * @author Marcus Hooper
 */

/**
 * Returns the simple name of a TypeMirror as a string.
 */
fun TypeMirror.simpleString(): String {
    val toString: String = this.toString()
    val indexOfDot: Int = toString.lastIndexOf('.')
    return if (indexOfDot == -1) toString else toString.substring(indexOfDot + 1)
}

/**
 * Returns true if the element can be made null by checking if any of the type mirrors are the word "Nullable"
 */
val Element.nullable
    get() = this.annotationMirrors
        .map { it.annotationType.simpleString() }
        .toList()
        .contains("Nullable")

/**
 * Convenience function to get the package of the element
 */
fun Element.getPackage(environment: ProcessingEnvironment) =
    environment.elementUtils.getPackageOf(this).toString()

/**
 * Returns the simple name of an Element
 */
val Element.simpleString
    get() = simpleName.toString()

/**
 * Generates a default constructor value based on the type of the element, taking into account nullability
 */
val Element.defaultValue
    get() = when {
        nullable -> "null"
        asType().kind == TypeKind.BOOLEAN -> "false"
        asType().kind == TypeKind.INT -> "0"
        asType().kind == TypeKind.FLOAT -> "0f"
        asType().toString() == "java.lang.String" -> "\"\""
        asType().toString().contains("Date") -> "Date()"
        asType().toString().contains("ArrayList") -> "ArrayList()"
        else -> ""
    }

/**
 * Gets the kotlin version of an element. This is necessary because at build time, all elements are the java version of
 * the element, even if the element is written in Kotlin. To generate kotlin code from this, you need to do some basic
 * conversion where possible.
 */
val Element.kotlinType
    get(): TypeName {
        val string = this.asType().toString()
        return (string.kotlinType ?: this.asType().asTypeName()).copy(nullable = nullable)
    }

/**
 * Attempts to turn a string into a [TypeName] representing a kotlin class, or returns null if it is unable to
 */
val String.kotlinType: TypeName?
    get() = when {
        this.contains("?") -> ClassName(this, this) //Contains optional so already a kotlin type
        this == "java.lang.String" -> String::class.asClassName() //Convert java string to kotlin string
        //Gets the type as specified between the carrots of the declaration. e.g. List<Int> will get Int
        this.contains("List") -> {
            val type = this.substring(this.indexOf("<") + 1, this.length - 1).kotlinType.toString()
            List::class.asClassName().parameterizedBy(
                ClassName(
                    type.substring(0, type.lastIndexOf(".")),
                    type.substring(type.lastIndexOf(".") + 1)
                )
            )
        }
        else -> null
    }