package com.hasunemiku2015.minecraftnano.api

import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * Methods for registration of MinecraftNano components.
 * @see Preprocessor
 * @see Function
 * @see AltFunction
 * @see Postprocessor
 * @see DestructionProcessor
 * @see LifecycleProcessor
 * @author hasunemiku2015
 * @date 2022/09/25 22:35
 */
object NanoRegistry {
    private val PREPROCESSORS              = arrayListOf<Preprocessor>()
    private val FUNCTIONS                  = arrayListOf<Function>()
    private val ALT_FUNCTIONS              = arrayListOf<AltFunction>()
    private val POSTPROCESSORS             = arrayListOf<Postprocessor>()
    private val DESTRUCTION_PROCESSORS     = arrayListOf<DestructionProcessor>()


    fun <T> a(p0: T?, p1: T?): Int {
        var p0Priority = -100
        var p1Priority = -100
        if (p0 != null) {
            @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
            p0Priority = p0!!::class.findAnnotation<ProcessPriority>()!!.priority.level
        }
        if (p1 != null) {
            @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
            p1Priority = p1!!::class.findAnnotation<ProcessPriority>()!!.priority.level
        }
        return p0Priority - p1Priority
    }

    fun b() {
        PREPROCESSORS.sortWith(::a)
        POSTPROCESSORS.sortWith(::a)
        DESTRUCTION_PROCESSORS.sortWith(::a)
    }

    fun c(): List<Preprocessor> {
        return PREPROCESSORS
    }

    fun d(): List<Function> {
        return FUNCTIONS
    }

    fun e(): List<AltFunction> {
        return ALT_FUNCTIONS
    }

    fun f(): List<Postprocessor> {
        return POSTPROCESSORS
    }

    fun g(): List<DestructionProcessor> {
        return DESTRUCTION_PROCESSORS
    }

    /**
     * Registers a preprocessor.
     * @param preprocessor Preprocessor to be registered
     * @see Preprocessor
     */
    fun registerPreprocessor(preprocessor: Preprocessor) {
        if (preprocessor::class.hasAnnotation<ProcessPriority>()) {
            PREPROCESSORS.add(preprocessor)
        }
    }

    /**
     * Registers a command starting with '!!'.
     * @param function Command to be registered
     * @see Function
     */
    fun registerFunction(function: Function) {
        FUNCTIONS.add(function)
    }

    /**
     * Registers a command starting with '!^'.
     * @param service Command to be registered
     * @see AltFunction
     */
    fun registerAltFunction(service: AltFunction) {
        ALT_FUNCTIONS.add(service)
    }

    /**
     * Registers a postprocessor.
     * @param postprocessor Postprocessor to be registered.
     * @see Postprocessor
     */
    fun registerPostprocessor(postprocessor: Postprocessor) {
        if (postprocessor::class.hasAnnotation<ProcessPriority>()) {
            POSTPROCESSORS.add(postprocessor)
        }
    }

    /**
     * Registers a destructor.
     * @param destructionProcessor Destructor to be registered.
     * @see DestructionProcessor
     */
    fun registerDestructionProcessor(destructionProcessor: DestructionProcessor) {
        if (destructionProcessor::class.hasAnnotation<ProcessPriority>()) {
            DESTRUCTION_PROCESSORS.add(destructionProcessor)
        }
    }

    /**
     * Registers a compact processor.
     * @param lifecycleProcessor CompactProcessor to be registered.
     * @see LifecycleProcessor
     * @see Preprocessor
     * @see DestructionProcessor
     */
    fun registerLifecycleProcessor(lifecycleProcessor: LifecycleProcessor) {
        if (lifecycleProcessor::class.hasAnnotation<ProcessPriority>()) {
            PREPROCESSORS.add(lifecycleProcessor)
            DESTRUCTION_PROCESSORS.add(lifecycleProcessor)
        }
    }
}