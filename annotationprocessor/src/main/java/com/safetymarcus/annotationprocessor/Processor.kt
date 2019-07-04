package com.safetymarcus.annotationprocessor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement


/**
 * @author Marcus Hooper
 */
@AutoService(Processor::class)
class Processor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Contract::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(Contract::class.java).forEach {
            generateViewState(it)
            generateStoreInterface(it)
        }
        return false
    }

    private fun generateViewState(element: Element) {
        //Creates a factory set up to handle generating a store based on its need to handle
        //networking. This will be used in the store that is generated next
        val factory = ViewStateFactory(element)
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        FileSpec.builder(element.getPackage(processingEnv), factory.fileName)
            .addType(factory.produce()).build()
            .writeTo(File(kaptKotlinGeneratedDir))
    }

    private fun generateStoreInterface(element: Element) {
        val pkg = processingEnv.elementUtils.getPackageOf(element).toString()
        val className = element.simpleString
        //Get the right factory based on if this store handles networking or not
        val storeFactory = StoreFactory(pkg, className, element)
        //Create the file in the build dir
        val file = FileSpec.builder(pkg, storeFactory.fileName).addType(storeFactory.produce()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir))
    }
}