package com.hasunemiku2015.minecraftnano.api


/**
 * Annotation to specify priority in MinecraftNano API.
 * The higher the priority, the first to be processed.
 * @see Preprocessor
 * @see Function
 * @see AltFunction
 * @see Postprocessor
 * @see DestructionProcessor
 * @author hasunemiku2015
 * @date 2022/09/25 22:20
 */
annotation class ProcessPriority(val priority: ProcessPriorityLevel)

/**
 * Enum Class for specifying priority in ProcessingPriority annotation. DO NOT USE DISPLAY PRIORITY!
 * @see ProcessPriority
 * @author hasunemiku2015
 * @date 2022/09/25 22:20
 */
enum class ProcessPriorityLevel(val level: Int) {
    HIGHEST(5),
    HIGH(4),
    NORMAL(3),
    LOW(2),
    LOWEST(1),
    DISPLAY(0)
}